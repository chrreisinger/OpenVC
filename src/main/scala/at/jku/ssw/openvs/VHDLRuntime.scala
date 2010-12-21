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

  trait EnumerationType {
    def getValue(value: Int): String
  }

  trait RecordType {
    def copy(): AnyRef
  }

  final class VHDLRuntimeException(message: String) extends RuntimeException(message) {
    override def toString: String = "Runtime Exception: " + this.getMessage
  }

  final case class MutableBoolean(@BeanProperty var value: Boolean)

  final case class MutableByte(@BeanProperty var value: Byte)

  final case class MutableCharacter(@BeanProperty var value: Char)

  final case class MutableInteger(@BeanProperty var value: Int)

  final case class MutableReal(@BeanProperty var value: Double)

  final case class MutableLong(@BeanProperty var value: Long)

  final class RuntimeArray1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](val data: Array[A], val left: Int, val right: Int) {
    val low = math.min(left, right)
    val high = math.max(left, right)
    val ascending = left < right

    def getValue(index: Int): A = if (left < right) data(index - left) else data(left - index)

    def setValue(index: Int, value: A) = if (left < right) data(index - left) = value else data(left - index) = value

    override def toString = data.mkString("Array(", ",", ")")

  }

  final class RuntimeArray2D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A]
  (val data: Array[Array[A]], private[this] val dim1Left: Int, private[this] val dim1Right: Int, private[this] val dim2Left: Int, private[this] val dim2Right: Int) {

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
      case 0 => data.length
      case 1 => data(0).length
      case _ => throw new VHDLRuntimeException("dimension:" + dim + " is out of range 0 to 1")
    }

    def getValue(dim1: Int, dim2: Int): A = {
      val index1 = if (dim1Left < dim1Right) (dim1 - dim1Left) else (dim1Left - dim1)
      val index2 = if (dim2Left < dim2Right) (dim2 - dim2Left) else (dim2Left - dim2)
      data(index1)(index2)
    }

    def setValue(dim1: Int, dim2: Int, value: A) = {
      val index1 = if (dim1Left < dim1Right) (dim1 - dim1Left) else (dim1Left - dim1)
      val index2 = if (dim2Left < dim2Right) (dim2 - dim2Left) else (dim2Left - dim2)
      data(index1)(index2) = value
    }

    override def toString = data.map(_.mkString(" ")).mkString("\n")

  }

  abstract sealed class AbstractSignal[A] {
    type TimeType = Long

    var value: A

    def delayed(delay: TimeType): AbstractSignal[A]

    def stable(time: TimeType): Boolean

    def quiet(time: TimeType): Boolean

    var transaction: Boolean
    var event: Boolean
    var active: Boolean
    var last_event: TimeType
    var last_active: TimeType
    var last_value: A
    var driving: Boolean
    var driving_value: A

    override def toString = value.toString
  }

  final class RuntimeFile {
    import java.io._

    var inputStream: DataInputStream = null
    var outputStream: DataOutputStream = null
    private[this] var readOnlyMode = false

    def open(external_Name: String, open_Kind: Int) =
      FILE_OPEN_KIND(open_Kind) match {
        case FILE_OPEN_KIND.READ_MODE =>
          inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(external_Name)))
          readOnlyMode = true
        case FILE_OPEN_KIND.WRITE_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, false)))
        case FILE_OPEN_KIND.APPEND_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, true)))
      }

    def close() {
      if (inputStream != null) inputStream.close
      if (outputStream != null) outputStream.close
      inputStream = null
      outputStream = null
    }

    def isOpen: Boolean = this.inputStream != null || this.outputStream != null

    @throws(classOf[VHDLRuntimeException])
    def writeChecks {
      if (!isOpen) throw new VHDLRuntimeException("file is not open")
      if (readOnlyMode) throw new VHDLRuntimeException("file is in read mode")
    }

    @throws(classOf[VHDLRuntimeException])
    def readChecks {
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

  def createEmptyRuntimeArray1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](left: Int, right: Int) = new RuntimeArray1D(Array.ofDim[A]((left - right).abs), left, right)

  def createRuntimeArrayFromOther1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](other: RuntimeArray1D[A], left: Int, right: Int) = new RuntimeArray1D(other.data, left, right)

  def createRuntimeArrayFromOtherArray1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](other: Array[A], left: Int, right: Int) = new RuntimeArray1D(other, left, right)

  def getArrayIndex1D(index: Int, left: Int, right: Int): Int = if (left < right) index - left else left - index

  def fill[A: ClassManifest](n: Int, clazz: Class[A]) = Array.tabulate(n)(i => clazz.newInstance)

  def fill[A: ClassManifest](n1: Int, n2: Int, clazz: Class[A]) = Array.tabulate(n1, n2)((i, j) => clazz.newInstance)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, clazz: Class[A]) = Array.tabulate(n1, n2, n3)((i, j, k) => clazz.newInstance)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, n4: Int, clazz: Class[A]) = Array.tabulate(n1, n2, n3, n4)((i, j, k, l) => clazz.newInstance)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, clazz: Class[A]) = Array.tabulate(n1, n2, n3, n4, n5)((i, j, k, l, m) => clazz.newInstance)

  def fill[A: ClassManifest](n: Int, value: A) = Array.fill[A](n)(value)

  def fill[A: ClassManifest](n1: Int, n2: Int, value: A) = Array.fill[A](n1, n2)(value)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, value: A) = Array.fill[A](n1, n2, n3)(value)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, n4: Int, value: A) = Array.fill[A](n1, n2, n3, n4)(value)

  def fill[A: ClassManifest](n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, value: A) = Array.fill[A](n1, n2, n3, n4, n5)(value)

  def NOT(data: Array[Boolean]): Array[Boolean] = data.map(!_)

  def AND(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield left(i) && right(i)).toArray

  def NAND(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield !(left(i) && right(i))).toArray

  def OR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield left(i) || right(i)).toArray

  def NOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield !(left(i) || right(i))).toArray

  def XOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield left(i) ^ right(i)).toArray

  def XNOR(left: Array[Boolean], right: Array[Boolean]): Array[Boolean] = (for (i <- left.indices) yield !(left(i) ^ right(i))).toArray


  def LT(left: Array[Boolean], right: Array[Boolean]): Boolean = {
    for (i <- left.indices) if (left(i) >= right(i)) return false
    true
  }

  def LT(left: Array[Byte], right: Array[Byte]): Boolean = {
    for (i <- left.indices) if (left(i) >= right(i)) return false
    true
  }

  def LT(left: Array[Char], right: Array[Char]): Boolean = {
    for (i <- left.indices) if (left(i) >= right(i)) return false
    true
  }

  def LT(left: Array[Int], right: Array[Int]): Boolean = {
    for (i <- left.indices) if (left(i) >= right(i)) return false
    true
  }

  def LEQ(left: Array[Boolean], right: Array[Boolean]): Boolean = {
    for (i <- left.indices) if (left(i) > right(i)) return false
    true
  }

  def LEQ(left: Array[Byte], right: Array[Byte]): Boolean = {
    for (i <- left.indices) if (left(i) > right(i)) return false
    true
  }

  def LEQ(left: Array[Char], right: Array[Char]): Boolean = {
    for (i <- left.indices) if (left(i) > right(i)) return false
    true
  }

  def LEQ(left: Array[Int], right: Array[Int]): Boolean = {
    for (i <- left.indices) if (left(i) > right(i)) return false
    true
  }


  def SLL(src: Array[Boolean], right: Int): Array[Boolean] =
    if (right < 0) SRL(src, -right)
    else if (right == 0 || src.length == 0) src
    else {
      val destination = Array.ofDim[Boolean](src.length)
      if (src.length - right > 0)
        Array.copy(src, right, destination, 0, src.length - right)
      destination
    }

  def SRL(src: Array[Boolean], right: Int): Array[Boolean] =
    if (right < 0) SLL(src, -right)
    else if (right == 0 || src.length == 0) src
    else {
      val destination = Array.ofDim[Boolean](src.length)
      if (src.length - right > 0)
        Array.copy(src, 0, destination, right, src.length - right)
      destination
    }

  def SLA(src: Array[Boolean], right: Int, ascending: Boolean): Array[Boolean] =
    if (right < 0) SRA(src, -right, ascending)
    else if (right == 0 || src.length == 0) src
    else {
      val destination = Array.fill(src.length)(src(src.length - 1)) //msb
      if (src.length - right - 1 > 0)
        Array.copy(src, right, destination, 0, src.length - right - 1)
      if (ascending) destination else destination.reverse
    }

  def SRA(src: Array[Boolean], right: Int, ascending: Boolean): Array[Boolean] =
    if (right < 0) SLA(src, -right, ascending)
    else if (right == 0 || src.length == 0) src
    else {
      val destination = Array.fill(src.length)(src(0)) //lsb
      if (src.length - right - 1 > 0)
        Array.copy(src, 1, destination, right + 1, src.length - right - 1)
      if (ascending) destination else destination.reverse
    }

  def rotate(src: Array[Boolean], right: Int): Array[Boolean] = {
    //http://answers.yahoo.com/question/index?qid=20091007083508AAcpDsd
    val len = src.length;
    val destination = Array.ofDim[Boolean](src.length)
    // Normalize the amount to rotate to be between 0 and len
    val tmp = right % len
    val amount = if (tmp < 0) len + tmp else tmp
    // The offset is the amount from end of the original array.
    // This is where we start the copy.
    val offset = len - amount
    // Copy from the offset to the end of the array
    Array.copy(src, offset, destination, 0, amount)
    // Copy from the beginning to the offset
    Array.copy(src, 0, destination, amount, offset)
    destination
  }

  def ROL(src: Array[Boolean], right: Int): Array[Boolean] =
    if (right < 0) ROR(src, -right)
    else if (right == 0 || src.length == 0) src
    else rotate(src, -right)

  def ROR(src: Array[Boolean], right: Int): Array[Boolean] =
    if (right < 0) ROL(src, -right)
    else if (right == 0 || src.length == 0) src
    else rotate(src, right)


  @throws(classOf[VHDLRuntimeException])
  def file_open(file: RuntimeFile, external_Name: String): Unit = file_open(file, external_Name, FILE_OPEN_KIND.READ_MODE.id)

  @throws(classOf[VHDLRuntimeException])
  def file_open(file: RuntimeFile, external_Name: String, open_Kind: Int) =
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

  def file_close(file: RuntimeFile) = file.close

  //TODO read and write array types and record types
  def readB(file: RuntimeFile): Byte = {
    file.readChecks
    file.inputStream.readByte
  }

  def write(file: RuntimeFile, value: Byte) {
    file.writeChecks
    file.outputStream.writeByte(value)
  }

  def readZ(file: RuntimeFile): Boolean = {
    file.readChecks
    file.inputStream.readBoolean
  }

  def write(file: RuntimeFile, value: Boolean) {
    file.writeChecks
    file.outputStream.writeBoolean(value)
  }

  def readC(file: RuntimeFile): Char = {
    file.readChecks
    file.inputStream.readChar
  }

  def write(file: RuntimeFile, value: Char) {
    file.writeChecks
    file.outputStream.writeChar(value)
  }

  def readI(file: RuntimeFile): Int = {
    file.readChecks
    file.inputStream.readInt
  }

  def write(file: RuntimeFile, value: Int) {
    file.writeChecks
    file.outputStream.writeInt(value)
  }

  def readD(file: RuntimeFile): Double = {
    file.readChecks
    file.inputStream.readDouble
  }

  def write(file: RuntimeFile, value: Double) {
    file.writeChecks
    file.outputStream.writeDouble(value)
  }

  def readJ(file: RuntimeFile): Long = {
    file.readChecks
    file.inputStream.readLong
  }

  def write(file: RuntimeFile, value: Long) {
    file.writeChecks
    file.outputStream.writeLong(value)
  }

  def endfile(file: RuntimeFile): Boolean = file.eof
}