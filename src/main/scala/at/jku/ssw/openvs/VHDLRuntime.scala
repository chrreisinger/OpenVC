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

package at.jku.ssw.openvs

object VHDLRuntime {
  import reflect.BeanProperty

  trait EnumType {
    def getValue(value: Int): String
  }

  final class VHDLRuntimeException(message: String) extends RuntimeException(message) {
    override def toString: String = "Runtime Exception: " + this.getMessage
  }

  final class MutableBoolean(@BeanProperty var value: Boolean)

  final class MutableByte(@BeanProperty var value: Byte)

  final class MutableCharacter(@BeanProperty var value: Char)

  final class MutableInteger(@BeanProperty var value: Int)

  final class MutableReal(@BeanProperty var value: Double)

  final class MutableLong(@BeanProperty var value: Long)

  abstract sealed class RuntimeArray1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double) T]
  (val data: Array[T], private[this] val length: Int, val left: Int, val right: Int)(implicit m: scala.reflect.Manifest[T]) {
    val low = math.min(left, right)
    val high = math.max(left, right)
    val ascending = left < right

    def getValue(index: Int): T = if (left < right) data(index - left) else data(left - index)

    def setValue(index: Int, value: T): Unit = if (left < right) data(index - left) = value else data(left - index) = value

    override def toString = data.mkString(" ")

  }

  final class RuntimeArray1DBoolean(data: Array[Boolean], length: Int, left: Int, right: Int) extends RuntimeArray1D[Boolean](data, length, left, right)
  final class RuntimeArray1DByte(data: Array[Byte], length: Int, left: Int, right: Int) extends RuntimeArray1D[Byte](data, length, left, right)
  final class RuntimeArray1DChar(data: Array[Char], length: Int, left: Int, right: Int) extends RuntimeArray1D[Char](data, length, left, right)
  final class RuntimeArray1DInt(data: Array[Int], length: Int, left: Int, right: Int) extends RuntimeArray1D[Int](data, length, left, right)
  final class RuntimeArray1DDouble(data: Array[Double], length: Int, left: Int, right: Int) extends RuntimeArray1D[Double](data, length, left, right)
  final class RuntimeArray1DAnyRef(data: Array[AnyRef], length: Int, left: Int, right: Int) extends RuntimeArray1D[AnyRef](data, length, left, right)

  abstract sealed class RuntimeArray2D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double) T]
  (private[this] val dim1Length: Int, private[this] val dim1Left: Int, private[this] val dim1Right: Int, private[this] val dim2Length: Int, private[this] val dim2Left: Int, private[this] val dim2Right: Int)
  (implicit m: scala.reflect.Manifest[T]) {
    private[this] val data = Array.ofDim[T](dim1Length, dim2Length)

    @throws(classOf[VHDLRuntimeException])
    def ascending(dim: Int) = dim match {
      case 0 => dim1Left < dim1Right
      case 1 => dim2Left < dim2Right
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    @throws(classOf[VHDLRuntimeException])
    def left(dim: Int) = dim match {
      case 0 => dim1Left
      case 1 => dim2Left
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    @throws(classOf[VHDLRuntimeException])
    def right(dim: Int) = dim match {
      case 0 => dim1Right
      case 1 => dim2Right
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    @throws(classOf[VHDLRuntimeException])
    def low(dim: Int) = dim match {
      case 0 => math.min(dim1Left, dim1Right)
      case 1 => math.min(dim2Left, dim2Right)
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    @throws(classOf[VHDLRuntimeException])
    def high(dim: Int) = dim match {
      case 0 => math.max(dim1Left, dim1Right)
      case 1 => math.max(dim2Left, dim2Right)
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    @throws(classOf[VHDLRuntimeException])
    def length(dim: Int) = dim match {
      case 0 => dim1Length
      case 1 => dim2Length
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    def getValue(dim1: Int, dim2: Int): T = {
      val index1 = if (dim1Left < dim1Right) (dim1 - dim1Left) else (dim1Left - dim1)
      val index2 = if (dim2Left < dim2Right) (dim2 - dim2Left) else (dim2Left - dim2)
      data(index1)(index2)
    }

    def setValue(dim1: Int, dim2: Int, value: T): Unit = {
      val index1 = if (dim1Left < dim1Right) (dim1 - dim1Left) else (dim1Left - dim1)
      val index2 = if (dim2Left < dim2Right) (dim2 - dim2Left) else (dim2Left - dim2)
      data(index1)(index2) = value
    }

    override def toString = data.map(_.mkString(" ")).mkString("\n")

  }

  final class RuntimeArray2DBoolean(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[Boolean](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  final class RuntimeArray2DByte(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[Byte](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  final class RuntimeArray2DChar(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[Char](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  final class RuntimeArray2DInt(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[Int](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  final class RuntimeArray2DDouble(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[Double](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  final class RuntimeArray2DAnyRef(dim1Length: Int, dim1Left: Int, dim1Right: Int, dim2Length: Int, dim2Left: Int, dim2Right: Int)
          extends RuntimeArray2D[AnyRef](dim1Length, dim1Left, dim1Right, dim2Length, dim2Left, dim2Right)

  abstract sealed class AbstractSignal[T] {
    type TimeType = Long

    var value: T

    def delayed(delay: TimeType): AbstractSignal[T]

    def stable(time: TimeType): Boolean

    def quiet(time: TimeType): Boolean

    var transaction: Boolean
    var event: Boolean
    var active: Boolean
    var last_event: TimeType
    var last_active: TimeType
    var last_value: T
    var driving: Boolean
    var driving_value: T

    override def toString = value.toString
  }

  final class RuntimeFile {
    import java.io._
    var inputStream: DataInputStream = null
    var outputStream: DataOutputStream = null
    var readOnlyMode = false

    def open(external_Name: String, open_Kind: Int): Unit =
      FILE_OPEN_KIND(open_Kind) match {
        case FILE_OPEN_KIND.READ_MODE =>
          inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(external_Name)))
          readOnlyMode = true
        case FILE_OPEN_KIND.WRITE_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, false)))
        case FILE_OPEN_KIND.APPEND_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, true)))
      }

    def close: Unit = {
      if (inputStream != null) inputStream.close
      if (outputStream != null) outputStream.close
      inputStream = null
      outputStream = null
    }

    def isOpen: Boolean = this.inputStream != null || this.outputStream != null

    @throws(classOf[VHDLRuntimeException])
    def writeChecks: Unit = {
      if (!isOpen) throw new VHDLRuntimeException("file is not open")
      if (readOnlyMode) throw new VHDLRuntimeException("file is in read mode")
    }

    @throws(classOf[VHDLRuntimeException])
    def readChecks: Unit = {
      if (!isOpen) throw new VHDLRuntimeException("file is not open")
      if (!readOnlyMode) throw new VHDLRuntimeException("file is in write mode")
    }

    @throws(classOf[VHDLRuntimeException])
    def eof: Boolean = {
      if (!isOpen) throw new VHDLRuntimeException("file is not open")
      if (readOnlyMode) {
        inputStream.mark(1)
        val nextByte = inputStream.read()
        inputStream.reset()
        nextByte == -1
      } else
        true
    }
  }

  object FILE_OPEN_KIND extends Enumeration {
    type FILE_OPEN_KIND = Value
    val READ_MODE, WRITE_MODE, APPEND_MODE = Value
  }

  object FILE_OPEN_STATUS extends Enumeration {
    type FILE_OPEN_STATUS = Value
    val OPEN_OK, STATUS_ERROR, NAME_ERROR, MODE_ERROR = Value
  }

  object SEVERITY_LEVEL extends Enumeration {
    type SEVERITY_LEVEL = Value
    val NOTE, WARNING, ERROR, FAILURE = Value
  }

  def createMutableInteger(value: Int) = new MutableInteger(value)

  def createMutableReal(value: Double) = new MutableReal(value)

  def createRuntimeArray1DBoolean(length: Int, left: Int, right: Int) = new RuntimeArray1DBoolean(Array.ofDim[Boolean](length), length, left, right)

  def createRuntimeArray1DByte(length: Int, left: Int, right: Int) = new RuntimeArray1DByte(Array.ofDim[Byte](length), length, left, right)

  def createRuntimeArray1DChar(length: Int, left: Int, right: Int) = new RuntimeArray1DChar(Array.ofDim[Char](length), length, left, right)

  def createRuntimeArray1DInt(length: Int, left: Int, right: Int) = new RuntimeArray1DInt(Array.ofDim[Int](length), length, left, right)

  def createRuntimeArray1DDouble(length: Int, left: Int, right: Int) = new RuntimeArray1DDouble(Array.ofDim[Double](length), length, left, right)

  def createRuntimeArray1DAnyRef(length: Int, left: Int, right: Int) = new RuntimeArray1DAnyRef(Array.ofDim[AnyRef](length), length, left, right)

  def createRuntimeArray1DBoolean(other: RuntimeArray1DBoolean, left: Int, right: Int) = new RuntimeArray1DBoolean(other.data, left - right.abs, left, right)

  def createRuntimeArray1DByte(other: RuntimeArray1DByte, left: Int, right: Int) = new RuntimeArray1DByte(other.data, left - right.abs, left, right)

  def createRuntimeArray1DChar(other: RuntimeArray1DChar, left: Int, right: Int) = new RuntimeArray1DChar(other.data, left - right.abs, left, right)

  def createRuntimeArray1DInt(other: RuntimeArray1DInt, left: Int, right: Int) = new RuntimeArray1DInt(other.data, left - right.abs, left, right)

  def createRuntimeArray1DDouble(other: RuntimeArray1DDouble, left: Int, right: Int) = new RuntimeArray1DDouble(other.data, left - right.abs, left, right)

  def createRuntimeArray1DAnyRef(other: RuntimeArray1DAnyRef, left: Int, right: Int) = new RuntimeArray1DAnyRef(other.data, left - right.abs, left, right)

  def getArrayIndex1D(index: Int, left: Int, right: Int): Int = if (left < right) index - left else left - index

  private val DefaultAssertLevel = SEVERITY_LEVEL.ERROR.id
  private val DefaultReportLevel = SEVERITY_LEVEL.NOTE.id
  private val DefaultAssertionMessage = "Assertion violation"

  private def levelToString(level: Int): String =
    SEVERITY_LEVEL(level) match {
      case SEVERITY_LEVEL.NOTE => "note: "
      case SEVERITY_LEVEL.WARNING => "warning: "
      case SEVERITY_LEVEL.ERROR => "error: "
      case SEVERITY_LEVEL.FAILURE => "failure: "
    }

  def report(designUnit: String, message: String, level: Int): Unit =
    println("report " + designUnit + " " + levelToString(level) + message)

  def report(designUnit: String, message: String): Unit = report(designUnit, message, DefaultReportLevel)

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, message: String, level: Int): Unit =
    throw new VHDLRuntimeException("assert " + designUnit + " " + levelToString(level) + message)

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String): Unit = assertVHDL(designUnit, DefaultAssertionMessage, DefaultAssertLevel)

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, level: Int): Unit = assertVHDL(designUnit, DefaultAssertionMessage, level)

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, message: String): Unit = assertVHDL(designUnit, message, DefaultAssertLevel)

  @throws(classOf[VHDLRuntimeException])
  def checkIsInRange(value: Int, low: Int, high: Int): Int =
    if (value < low || value > high)
      throw new VHDLRuntimeException("value:" + value + " is out of range " + low + " to " + high)
    else value

  @throws(classOf[VHDLRuntimeException])
  def checkIsInRange(value: Double, low: Double, high: Double): Double =
    if (value < low || value > high)
      throw new VHDLRuntimeException("value:" + value + " is out of range " + low + " to " + high)
    else value

  @throws(classOf[VHDLRuntimeException])
  def checkIsInRange(value: Long, low: Long, high: Long): Long =
    if (value < low || value > high)
      throw new VHDLRuntimeException("value:" + value + " is out of range " + low + " to " + high)
    else value

  @throws(classOf[VHDLRuntimeException])
  def pow(base: Int, exponent: Int): Int =
    if (exponent < 0)
      throw new VHDLRuntimeException("exponent:" + exponent + " is negative, that is not allowed")
    else math.pow(base, exponent).toInt

  def pow(base: Double, exponent: Int): Double =
    if (exponent < 0)
      1 / math.pow(base, math.abs(exponent))
    else math.pow(base, exponent)

  def stringAppend(s1: String, s2: String): String = s1.concat(s2)

  def mod(x: Int, y: Int): Int = {
    val mod = x % y
    return if ((mod < 0 && y > 0) || (mod > 0 && y < 0)) mod + y else mod
  }

  def mod(x: Double, y: Double): Double = {
    val mod = math.IEEEremainder(x, y)
    return if (y * mod < 0) mod + y else mod
  }

  def booleanAND(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => x._1 && x._2)

  def booleanNAND(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => !(x._1 && x._2))

  def booleanOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => x._1 || x._2)

  def booleanNOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => !(x._1 || x._2))

  def booleanXOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => x._1 ^ x._2)

  def booleanXNOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = left.zip(right).map(x => !(x._1 ^ x._2))


  @throws(classOf[VHDLRuntimeException])
  def file_open(file: RuntimeFile, external_Name: String): Unit = file_open(file, external_Name, FILE_OPEN_KIND.READ_MODE.id)

  @throws(classOf[VHDLRuntimeException])
  def file_open(file: RuntimeFile, external_Name: String, open_Kind: Int): Unit =
    try {
      if (file.isOpen) throw new VHDLRuntimeException("file is alreay open")
      file.open(external_Name, open_Kind)
    } catch {
      case fileNotFoundException: java.io.FileNotFoundException => throw new VHDLRuntimeException("file not found:" + fileNotFoundException.getMessage)
    }

  //scala can not overload a method with only a different return type
  def file_open_status(file: RuntimeFile, external_Name: String): Int = file_open_status(file, external_Name, FILE_OPEN_KIND.READ_MODE.id)

  def file_open_status(file: RuntimeFile, external_Name: String, open_Kind: Int): Int =
  //not used MODE_ERROR
    try {
      if (file.isOpen) return FILE_OPEN_STATUS.STATUS_ERROR.id
      file.open(external_Name, open_Kind)
      FILE_OPEN_STATUS.OPEN_OK.id
    } catch {
      case fileNotFoundException: java.io.FileNotFoundException => FILE_OPEN_STATUS.NAME_ERROR.id
    }

  def file_close(file: RuntimeFile): Unit = file.close

  //TODO read and write array types and record types
  def readB(file: RuntimeFile): Byte = {
    file.readChecks
    file.inputStream.readByte
  }

  def write(file: RuntimeFile, value: Byte): Unit = {
    file.writeChecks
    file.outputStream.writeByte(value)
  }

  def readZ(file: RuntimeFile): Boolean = {
    file.readChecks
    file.inputStream.readBoolean
  }

  def write(file: RuntimeFile, value: Boolean): Unit = {
    file.writeChecks
    file.outputStream.writeBoolean(value)
  }

  def readC(file: RuntimeFile): Char = {
    file.readChecks
    file.inputStream.readChar
  }

  def write(file: RuntimeFile, value: Char): Unit = {
    file.writeChecks
    file.outputStream.writeChar(value)
  }

  def readI(file: RuntimeFile): Int = {
    file.readChecks
    file.inputStream.readInt
  }

  def write(file: RuntimeFile, value: Int): Unit = {
    file.writeChecks
    file.outputStream.writeInt(value)
  }

  def readD(file: RuntimeFile): Double = {
    file.readChecks
    file.inputStream.readDouble
  }

  def write(file: RuntimeFile, value: Double): Unit = {
    file.writeChecks
    file.outputStream.writeDouble(value)
  }

  def readJ(file: RuntimeFile): Long = {
    file.readChecks
    file.inputStream.readLong
  }

  def write(file: RuntimeFile, value: Long): Unit = {
    file.writeChecks
    file.outputStream.writeLong(value)
  }

  def endfile(file: RuntimeFile): Boolean = file.eof

  def unboxToInt(i: AnyRef): Int = i.asInstanceOf[java.lang.Integer].intValue

  def unboxToDouble(d: AnyRef): Double = d.asInstanceOf[java.lang.Double].doubleValue
}