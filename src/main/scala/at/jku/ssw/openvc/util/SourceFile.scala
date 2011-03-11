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

package at.jku.ssw.openvc.util

import org.antlr.runtime.{CharStream, ANTLRFileStream, ANTLRStringStream, ANTLRInputStream}
import java.io.InputStream

object SourceFile {
  def fromFile(fileName: String, encoding: Option[String] = None): SourceFile =
    new ANTLRStream(fileName, new ANTLRFileStream(fileName, encoding.orNull))

  def fromText(code: String, fileName: String): SourceFile =
    new ANTLRStream(fileName, new ANTLRStringStream(code))

  def fromInputStream(stream: InputStream, fileName: String, encoding: Option[String] = None): SourceFile =
    new ANTLRStream(fileName, new ANTLRInputStream(stream, encoding.orNull))
}

sealed abstract class SourceFile extends scala.io.Source {
  val content: Array[Char]
  val fileName: String

  lazy val contentAsString = {
    //create a string and set the value, offset and count fields, so we avoid all array copy calls
    //content and contentAsString share the same array
    val constructor = classOf[String].getDeclaredConstructor(java.lang.Integer.TYPE, java.lang.Integer.TYPE, classOf[Array[Char]])
    constructor.setAccessible(true)
    constructor.newInstance(new java.lang.Integer(0), new java.lang.Integer(content.length), content)
  }

  lazy val iter = content.iterator
}

private[util] final class ANTLRStream(val fileName: String, val stringStream: ANTLRStringStream) extends SourceFile with CharStream {
  val content = {
    //data is protected, ANTLR should add a method to get the data field => use reflection
    val data = try {
      stringStream.getClass.getDeclaredField("data")
    } catch {
      case _: NoSuchFieldException => stringStream.getClass.getSuperclass.getDeclaredField("data")
    }
    data.setAccessible(true)
    data.get(stringStream).asInstanceOf[Array[Char]]
  }

  def substring(start: Int, stop: Int): String = contentAsString.substring(start, stop + 1) //also no array copy

  //just forward all calls to the ANTLRStringStream
  def LT(i: Int): Int = stringStream.LT(i)

  def getLine: Int = stringStream.getLine

  def setLine(line: Int) {stringStream.setLine(line)}

  def setCharPositionInLine(pos: Int) {stringStream.setCharPositionInLine(pos)}

  def getCharPositionInLine: Int = stringStream.getCharPositionInLine

  def consume() {stringStream.consume()}

  def LA(i: Int): Int = Character.toLowerCase(stringStream.LA(i))

  def mark: Int = stringStream.mark

  def index: Int = stringStream.index

  def rewind(marker: Int) {stringStream.rewind(marker)}

  def rewind() {stringStream.rewind()}

  def release(marker: Int) {stringStream.release(marker)}

  def seek(index: Int) {stringStream.seek(index)}

  override def size: Int = stringStream.size

  def getSourceName: String = stringStream.getSourceName
}