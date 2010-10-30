/*
 *     OpenVC, an open source VHDL compiler/simulator
 *     Copyright (C) 2010  Christian Reisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.jku.ssw.openvc.symbolTable

import scala.annotation.tailrec
import java.io._
import dataTypes._
import at.jku.ssw.openvc.symbolTable.symbols.{AttributeDeclarationSymbol, Symbol}

object SymbolTable {
  type Scope = Map[String, Symbol]

  @throws(classOf[IOException])
  def getScopesFromInputStream(input: InputStream): Seq[Scope] = {
    @tailrec
    def getScopesFromInputStreamInner(objectInput: ObjectInputStream, list: Seq[Scope]): Seq[Scope] = {
      val obj = try
      {
        objectInput.readObject.asInstanceOf[Scope]
      } catch {
        case _: EOFException => null
      }
      if (obj == null) return list
      getScopesFromInputStreamInner(objectInput, obj +: list)
    }
    val reader = new ObjectInputStream(input)
    val listOfScopes = getScopesFromInputStreamInner(reader, List())
    reader.close()
    listOfScopes
  }

  //TODO change to vals and move to Context
  var booleanType: EnumerationType = null
  var bitType: EnumerationType = null
  var characterType: EnumerationType = null
  var severityLevel: EnumerationType = null
  var integerType: IntegerType = null
  var realType: RealType = null
  val universalIntegerType = IntegerType("universal_integer", Integer.MIN_VALUE, Integer.MAX_VALUE, None)
  val universalRealType = RealType("universal_real", java.lang.Double.MIN_VALUE, java.lang.Double.MAX_VALUE, None)
  var timeType: PhysicalType = null
  var naturalType: IntegerType = null
  var positiveType: IntegerType = null
  var stringType: ArrayType = null
  var bitVector: ArrayType = null
  var fileOpenKind: EnumerationType = null
  var fileOpenStatus: EnumerationType = null
  var foreignAttribute: AttributeDeclarationSymbol = null
}

final class SymbolTable(val scopes: Seq[SymbolTable.Scope]) {
  override def toString = scopes.mkString

  def currentScope = scopes.head

  def depth = scopes.size

  def find(name: String): Option[Symbol] = {
    for (scope <- this.scopes) {
      val obj = scope.get(name)
      if (obj.isDefined) return obj
    }
    None
  }

  def findInCurrentScope[A <: Symbol](name: String, clazz: Class[A]): Option[A] =
    currentScope.get(name).flatMap{
      symbol =>
        if (symbol.getClass ne clazz)
          None
        else Some(symbol.asInstanceOf[A])
    }

  def insert(obj: Symbol): SymbolTable = new SymbolTable((this.scopes.head + (obj.name -> obj)) +: this.scopes.tail)

  def openScope: SymbolTable = new SymbolTable(Map[String, Symbol]() +: this.scopes)

  @throws(classOf[IOException])
  def writeToFile(file: String) {
    val writer = new ObjectOutputStream(new FileOutputStream(file + ".sym", false))
    try
    {
      this.scopes.init.reverse.foreach(scope => if (scopes.nonEmpty) writer.writeObject(scope))
    }
    finally {
      writer.close()
    }
  }
}

abstract sealed class AbstractLibraryArchive {
  def close()

  def loadSymbol(name: String): Option[Symbol] =
    getInputStream(name).flatMap{
      stream =>
        try
        {
          val reader = new ObjectInputStream(stream)
          Some(reader.readObject.asInstanceOf[Symbol])
        } catch {
          case _: IOException => None
        }
        finally {
          stream.close()
        }
    }

  def loadSymbol[A <: Symbol](name: String, symbolClass: Class[A]): Option[A] =
    loadSymbol(name).flatMap{
      symbol =>
        if (symbol.getClass eq symbolClass) Some(symbol.asInstanceOf[A])
        else None
    }

  def getInputStream(file: String): Option[InputStream]
}

final class JarFileLibraryArchive(file: String) extends AbstractLibraryArchive {

  import java.util.jar.{JarFile, JarEntry}

  val jarFile = new JarFile(file)
  type mapType = Map[String, JarEntry]
  val files = {
    @tailrec
    def initInner(map: mapType, entries: java.util.Enumeration[JarEntry]): mapType = {
      if (!entries.hasMoreElements()) return map
      val entry = entries.nextElement()
      initInner(map + (entry.getName() -> entry), entries)
    }
    initInner(Map(), jarFile.entries())
  }

  @throws(classOf[IOException])
  override def close() = this.jarFile.close()

  override def getInputStream(file: String): Option[InputStream] =
    this.files.get(file).map(entry => new BufferedInputStream(this.jarFile.getInputStream(entry)))
}

final class DirectoryLibraryArchive(directory: String) extends AbstractLibraryArchive {
  override def close() = {}

  override def getInputStream(file: String): Option[InputStream] =
    try
    {
      Some(new FileInputStream(directory + File.separator + file))
    } catch {
      case _ => None
    }
}