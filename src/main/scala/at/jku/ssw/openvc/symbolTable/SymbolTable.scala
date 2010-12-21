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
import symbols.{AttributeDeclarationSymbol, Symbol}

object SymbolTable {
  type Scope = Map[String, Symbol]

  @throws(classOf[IOException])
  def getScopesFromFile(fileName: String): Option[Seq[Scope]] = {
    @tailrec
    def getScopesFromInputStreamInner(objectInputStream: ObjectInputStream, list: Seq[Scope]): Seq[Scope] =
      (try {
        Some(objectInputStream.readObject.asInstanceOf[Scope])
      } catch {
        case _: EOFException => None
      }) match {
        case None => list
        case Some(scopes) => getScopesFromInputStreamInner(objectInputStream, scopes +: list)
      }

    try {
      val reader = new ObjectInputStream(new FileInputStream(fileName + ".sym"))
      val listOfScopes = getScopesFromInputStreamInner(reader, List())
      require(listOfScopes.size == 1)
      reader.close()
      Some(listOfScopes)
    } catch {
      case _: FileNotFoundException => None
    }

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

  def findInCurrentScope[A <: Symbol](name: String, symbolClass: Class[A]): Option[A] =
    currentScope.get(name).flatMap{
      symbol =>
        if (symbolClass.isInstance(symbol)) Some(symbol.asInstanceOf[A])
        else None
    }

  def insert(obj: Symbol): SymbolTable = new SymbolTable((this.scopes.head + (obj.name -> obj)) +: this.scopes.tail)

  def openScope: SymbolTable = new SymbolTable(Map[String, Symbol]() +: this.scopes)

  @throws(classOf[IOException])
  def writeToFile(file: String) {
    val writer = new ObjectOutputStream(new FileOutputStream(file + ".sym", false))
    try {
      this.scopes.init.reverse.foreach(scope => writer.writeObject(scope))
    }
    finally {
      writer.close()
    }
  }
}

abstract sealed class AbstractLibraryArchive extends java.io.Serializable {
  def close()

  def loadSymbol(name: String): Option[Symbol] =
    getInputStream(name + ".sym").flatMap{
      stream =>
        try {
          Option(new ObjectInputStream(stream).readObject.asInstanceOf[Symbol])
        } catch {
          case ex: ObjectStreamException => throw ex //thrown when the SerialVersionUID is different (e.g. after proguard removed methods, so that a different number is calculated for scala classes)
          case _: IOException => None
        }
        finally {
          stream.close()
        }
    }

  def loadSymbol[A <: Symbol](name: String, symbolClass: Class[A]): Option[A] =
    loadSymbol(name).flatMap{
      symbol =>
        if (symbolClass.isInstance(symbol)) Some(symbol.asInstanceOf[A])
        else None
    }

  def getInputStream(file: String): Option[InputStream]
}

@SerialVersionUID(-5906992646423894974L)
final class JarFileLibraryArchive(val file: String) extends AbstractLibraryArchive {

  @transient lazy val jarFile = new java.util.jar.JarFile(file)

  @throws(classOf[IOException])
  override def close() = this.jarFile.close()

  override def getInputStream(file: String): Option[InputStream] =
    jarFile.getEntry(file) match {
      case null => None
      case entry => Option(new BufferedInputStream(jarFile.getInputStream(entry)))
    }
}

@SerialVersionUID(-3176565630855442198L)
final class DirectoryLibraryArchive(val directory: String) extends AbstractLibraryArchive {
  override def close() = {}

  override def getInputStream(file: String): Option[InputStream] =
    try {
      Some(new FileInputStream(directory + File.separator + file))
    } catch {
      case _: FileNotFoundException => None
    }
}