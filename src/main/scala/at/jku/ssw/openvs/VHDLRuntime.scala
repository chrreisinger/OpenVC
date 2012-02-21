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

  abstract sealed class AbstractSignal[A] {
    type TimeType = Long

    var value: A

    def delayed(delay: TimeType): AbstractSignal[A] = null

    def stable(time: TimeType): Boolean = true

    def quiet(time: TimeType): Boolean = true

    var transaction: Boolean = _
    var event: Boolean = _
    var active: Boolean = _
    var last_event: TimeType = _
    var last_active: TimeType = _
    var last_value: A = _
    var driving: Boolean = _
    var driving_value: A = _

    override def toString = value.toString
  }

  final class ScalarSignal[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](var value: A) extends AbstractSignal[A] {

  }

  final class Array1DSignal[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](var value: RuntimeArray1D[ScalarSignal[A]]) extends AbstractSignal[RuntimeArray1D[ScalarSignal[A]]] {

  }

  final class Array2DSignal[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](var value: RuntimeArray2D[ScalarSignal[A]]) extends AbstractSignal[RuntimeArray2D[ScalarSignal[A]]] {

  }

  final class RuntimeFile {
    import java.io._

    var inputStream: DataInputStream = null
    var outputStream: DataOutputStream = null
    private[this] var readOnlyMode = false

    def open(external_Name: String, open_Kind: Int) {
      FILE_OPEN_KIND(open_Kind) match {
        case FILE_OPEN_KIND.READ_MODE =>
          inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(external_Name)))
          readOnlyMode = true
        case FILE_OPEN_KIND.WRITE_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, false)))
        case FILE_OPEN_KIND.APPEND_MODE => outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(external_Name, true)))
      }
    }

    def close() {
      if (inputStream != null) inputStream.close()
      if (outputStream != null) outputStream.close()
      inputStream = null
      outputStream = null
    }

    def isOpen: Boolean = this.inputStream != null || this.outputStream != null

    @throws(classOf[VHDLRuntimeException])
    def writeChecks() {
      if (!isOpen) throw new VHDLRuntimeException("file is not open")
      if (readOnlyMode) throw new VHDLRuntimeException("file is in read mode")
    }

    @throws(classOf[VHDLRuntimeException])
    def readChecks() {
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

  private val DefaultAssertLevel: Byte = SEVERITY_LEVEL.ERROR.id.asInstanceOf[Byte]
  private val DefaultReportLevel: Byte = SEVERITY_LEVEL.NOTE.id.asInstanceOf[Byte]
  private val DefaultAssertionMessage = "Assertion violation"

  private def levelToString(level: Byte): String =
    SEVERITY_LEVEL(level) match {
      case SEVERITY_LEVEL.NOTE => "note: "
      case SEVERITY_LEVEL.WARNING => "warning: "
      case SEVERITY_LEVEL.ERROR => "error: "
      case SEVERITY_LEVEL.FAILURE => "failure: "
    }

  def report(designUnit: String, message: String, level: Byte) {
    println("report " + designUnit + " " + levelToString(level) + message)
  }

  def report(designUnit: String, message: String) {
    report(designUnit, message, DefaultReportLevel)
  }

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, message: String, level: Byte) {
    throw new VHDLRuntimeException("assert " + designUnit + " " + levelToString(level) + message)
  }

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String) {
    assertVHDL(designUnit, DefaultAssertionMessage, DefaultAssertLevel)
  }

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, level: Byte) {
    assertVHDL(designUnit, DefaultAssertionMessage, level)
  }

  @throws(classOf[VHDLRuntimeException])
  def assertVHDL(designUnit: String, message: String) {
    assertVHDL(designUnit, message, DefaultAssertLevel)
  }

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
  def checkIsInRange[A](array: RuntimeArray1D[A], low: Int, high: Int): RuntimeArray1D[A] =
    if (array.range.start < low || array.range.end > high)
      throw new VHDLRuntimeException("array index out of range:" + low + " " + high)
    else array

  @throws(classOf[VHDLRuntimeException])
  def pow(base: Int, exponent: Int): Int =
    if (exponent < 0)
      throw new VHDLRuntimeException("exponent:" + exponent + " is negative, that is not allowed")
    else math.pow(base, exponent).toInt

  def pow(base: Double, exponent: Int): Double =
    if (exponent < 0)
      1 / math.pow(base, math.abs(exponent))
    else math.pow(base, exponent)

  def mod(x: Int, y: Int): Int = {
    val mod = x % y
    if ((mod < 0 && y > 0) || (mod > 0 && y < 0)) mod + y else mod
  }

  def createRuntimeArrayFromAggregate[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range: VHDLRange, values: ArrayImpl[A], choices: ArrayImpl[(AnyRef, A)], defaultValue: A) = {
    /*
    TODO
    val array = fill(range.size, defaultValue)
    Array.copy(values, 0, array, 0, values.length)
    for (choice <- choices) {
      choice._1 match {
        case range: RangeType => java.util.Arrays.fill(array, range.start, range.end, choice._2)
        case i: Int => array(i) = choice._2
      }
    }*/
    new RuntimeArray1D(null, range)
  }

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](other: RuntimeArray1D[A], range: VHDLRange) =
    new RuntimeArray1D(other.data, range)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](other: RuntimeArray2D[A], range1: VHDLRange, range2: VHDLRange) =
    new RuntimeArray2D(other.data, range1, range2)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](other: RuntimeArray3D[A], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange) =
    new RuntimeArray3D(other.data, range1, range2, range3)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](other: RuntimeArray4D[A], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange) =
    new RuntimeArray4D(other.data, range1, range2, range3, range4)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](other: RuntimeArray5D[A], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, range5: VHDLRange) =
    new RuntimeArray5D(other.data, range1, range2, range3, range4, range5)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range: VHDLRange, clazz: Class[A]) =
    new RuntimeArray1D(ArrayImpl.tabulate(range.size)(i => clazz.newInstance), range)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, clazz: Class[A]) =
    new RuntimeArray2D(ArrayImpl.tabulate(range1.size, range2.size)((i, j) => clazz.newInstance), range1, range2)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, clazz: Class[A]) =
    new RuntimeArray3D(ArrayImpl.tabulate(range1.size, range2.size, range3.size)((i, j, k) => clazz.newInstance), range1, range2, range3)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, clazz: Class[A]) =
    new RuntimeArray4D(ArrayImpl.tabulate(range1.size, range2.size, range3.size, range4.size)((i, j, k, l) => clazz.newInstance), range1, range2, range3, range4)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, range5: VHDLRange, clazz: Class[A]) =
    new RuntimeArray5D(ArrayImpl.tabulate(range1.size, range2.size, range3.size, range4.size, range5.size)((i, j, k, l, m) => clazz.newInstance), range1, range2, range3, range4, range5)

  def createRuntimeArray(range: VHDLRange, array: ArrayType) =
    new RuntimeArray1D(ArrayImpl.tabulate(range.size)(i => array.copy), range)

  def createRuntimeArray(range1: VHDLRange, range2: VHDLRange, array: ArrayType) =
    new RuntimeArray2D(ArrayImpl.tabulate(range1.size, range2.size)((i, j) => array.copy), range1, range2)

  def createRuntimeArray(range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, array: ArrayType) =
    new RuntimeArray3D(ArrayImpl.tabulate(range1.size, range2.size, range3.size)((i, j, k) => array.copy), range1, range2, range3)

  def createRuntimeArray(range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, array: ArrayType) =
    new RuntimeArray4D(ArrayImpl.tabulate(range1.size, range2.size, range3.size, range4.size)((i, j, k, l) => array.copy), range1, range2, range3, range4)

  def createRuntimeArray(range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, range5: VHDLRange, array: ArrayType) =
    new RuntimeArray5D(ArrayImpl.tabulate(range1.size, range2.size, range3.size, range4.size, range5.size)((i, j, k, l, m) => array.copy), range1, range2, range3, range4, range5)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range: VHDLRange, value: A) =
    new RuntimeArray1D(ArrayImpl.fill[A](range.size)(value), range)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, value: A) =
    new RuntimeArray2D(ArrayImpl.fill[A](range1.size, range2.size)(value), range1, range2)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, value: A) =
    new RuntimeArray3D(ArrayImpl.fill[A](range1.size, range2.size, range3.size)(value), range1, range2, range3)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, value: A) =
    new RuntimeArray4D(ArrayImpl.fill[A](range1.size, range2.size, range3.size, range4.size)(value), range1, range2, range3, range4)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A: ClassManifest](range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, range5: VHDLRange, value: A) =
    new RuntimeArray5D(ArrayImpl.fill[A](range1.size, range2.size, range3.size, range4.size, range5.size)(value), range1, range2, range3, range4, range5)

  def createRuntimeArray[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](array: Array[A], range: VHDLRange) =
    new RuntimeArray1D(ArrayImpl(array: _*), range)

  def slice[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](array: RuntimeArray1D[A], range: VHDLRange) =
    new RuntimeArray1D(array.data.slice(indexPosition(array.range, range.start), indexPosition(array.range, range.end) + range.step), range)

  def concatenate[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](left: RuntimeArray1D[A], right: RuntimeArray1D[A], rangeStart: Int) =
    new RuntimeArray1D(left.data ++ right.data, new VHDLRange(rangeStart, rangeStart + ((left.range.size + right.range.size - 1) * left.range.step), left.range.step))

  def concatenate[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](left: A, right: RuntimeArray1D[A], rangeStart: Int) =
    new RuntimeArray1D(left +: right.data, new VHDLRange(rangeStart, rangeStart + (right.range.size * right.range.step), right.range.step)) //prepend

  def concatenate[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](left: RuntimeArray1D[A], right: A, rangeStart: Int) =
    new RuntimeArray1D(left.data :+ right, new VHDLRange(rangeStart, rangeStart + (left.range.size * left.range.step), left.range.step)) //append

  def concatenate[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](left: A, right: A, rangeStart: Int) =
    new RuntimeArray1D(ArrayImpl(left, right), null) //TODO

  def concatenate(s1: String, s2: String, newRange: VHDLRange): String = s1.concat(s2)

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
  def file_open(file: RuntimeFile, external_Name: String) {
    file_open(file, external_Name, FILE_OPEN_KIND.READ_MODE.id)
  }

  @throws(classOf[VHDLRuntimeException])
  def file_open(file: RuntimeFile, external_Name: String, open_Kind: Int) {
    try {
      if (file.isOpen) throw new VHDLRuntimeException("file is alreay open")
      file.open(external_Name, open_Kind)
    } catch {
      case fileNotFoundException: java.io.FileNotFoundException => throw new VHDLRuntimeException("file not found:" + fileNotFoundException.getMessage)
    }
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

  def file_close(file: RuntimeFile) {
    file.close()
  }

  //TODO read and write array types and record types
  def readB(file: RuntimeFile): Byte = {
    file.readChecks()
    file.inputStream.readByte
  }

  def write(file: RuntimeFile, value: Byte) {
    file.writeChecks()
    file.outputStream.writeByte(value)
  }

  def readZ(file: RuntimeFile): Boolean = {
    file.readChecks()
    file.inputStream.readBoolean
  }

  def write(file: RuntimeFile, value: Boolean) {
    file.writeChecks()
    file.outputStream.writeBoolean(value)
  }

  def readC(file: RuntimeFile): Char = {
    file.readChecks()
    file.inputStream.readChar
  }

  def write(file: RuntimeFile, value: Char) {
    file.writeChecks()
    file.outputStream.writeChar(value)
  }

  def readI(file: RuntimeFile): Int = {
    file.readChecks()
    file.inputStream.readInt
  }

  def write(file: RuntimeFile, value: Int) {
    file.writeChecks()
    file.outputStream.writeInt(value)
  }

  def readD(file: RuntimeFile): Double = {
    file.readChecks()
    file.inputStream.readDouble
  }

  def write(file: RuntimeFile, value: Double) {
    file.writeChecks()
    file.outputStream.writeDouble(value)
  }

  def readJ(file: RuntimeFile): Long = {
    file.readChecks()
    file.inputStream.readLong
  }

  def write(file: RuntimeFile, value: Long) {
    file.writeChecks()
    file.outputStream.writeLong(value)
  }

  def endfile(file: RuntimeFile): Boolean = file.eof
}