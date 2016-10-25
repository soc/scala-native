package java.lang

import scalanative.native, native._

final class Float(override val floatValue: scala.Float)
    extends Number
    with Comparable[Float] {
  @inline def this(s: String) =
    this(Float.parseFloat(s))

  @inline override def byteValue(): scala.Byte =
    floatValue.toByte

  @inline override def shortValue(): scala.Short =
    floatValue.toShort

  @inline def intValue(): scala.Int =
    floatValue.toInt

  @inline def longValue(): scala.Long =
    floatValue.toLong

  @inline def doubleValue(): scala.Double =
    floatValue.toDouble

  @inline override def equals(that: Any): scala.Boolean =
    that match {
      case that: Float =>
        // As floats with different NaNs are considered equal,
        // use floatToIntBits instead of floatToRawIntBits
        val a = Float.floatToIntBits(floatValue)
        val b = Float.floatToIntBits(that.floatValue)
        a == b

      case _ =>
        false
    }

  @inline override def hashCode(): Int =
    Float.hashCode(floatValue)

  @inline override def compareTo(that: Float): Int =
    Float.compare(floatValue, that.floatValue)

  @inline override def toString(): String =
    Float.toString(floatValue)

  @inline def isNaN(): scala.Boolean =
    Float.isNaN(floatValue)

  @inline def isInfinite(): scala.Boolean =
    Float.isInfinite(floatValue)
}

object Float {
  final val BYTES             = 4
  final val MAX_EXPONENT      = 127
  final val MAX_VALUE         = 3.40282346638528860e+38f
  final val MIN_EXPONENT      = -126
  final val MIN_NORMAL        = 1.17549435E-38f
  final val MIN_VALUE         = 1.40129846432481707e-45f
  final val NaN               = 0.0f / 0.0f
  final val NEGATIVE_INFINITY = 1.0f / -0.0f
  final val POSITIVE_INFINITY = 1.0f / 0.0f
  final val SIZE              = 32
  final val TYPE              = classOf[scala.Float]

  @inline def compare(x: scala.Float, y: scala.Float): scala.Int =
    if (x > y) 1
    else if (x < y) -1
    else if (x == y && 0.0f != x) 0
    else {
      if (isNaN(x)) {
        if (isNaN(y)) 0
        else 1
      } else if (isNaN(y)) {
        -1
      } else {
        val f1 = floatToRawIntBits(x)
        val f2 = floatToRawIntBits(y)
        (f1 >> 31) - (f2 >> 31)
      }
    }

  @inline def floatToIntBits(value: scala.Float): scala.Int =
    if (value != value) 0x7fc00000
    else floatToRawIntBits(value)

  @inline def floatToRawIntBits(value: scala.Float): scala.Int =
    value.cast[scala.Int]

  @inline def hashCode(value: scala.Float): scala.Int =
    floatToIntBits(value)

  @inline def intBitsToFloat(value: scala.Int): scala.Float =
    value.cast[scala.Float]

  @inline def isFinite(f: scala.Float): scala.Boolean =
    !isInfinite(f)

  @inline def isInfinite(v: scala.Float): scala.Boolean =
    v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY

  @inline def isNaN(v: scala.Float): scala.Boolean =
    v != v

  @inline def max(a: scala.Float, b: scala.Float): scala.Float =
    Math.max(a, b)

  @inline def min(a: scala.Float, b: scala.Float): scala.Float =
    Math.min(a, b)

  def parseFloat(s: String): scala.Float = {
    val cstr = toCString(s)
    val end  = stackalloc[CString]

    errno.errno = 0
    val res = stdlib.strtof(cstr, end)

    if (errno.errno == 0) res
    else throw new NumberFormatException(s)
  }

  @inline def sum(a: scala.Float, b: scala.Float): scala.Float =
    a + b

  def toHexString(f: scala.Float): String =
    if (f != f) {
      "NaN"
    } else if (f == POSITIVE_INFINITY) {
      "Infinity"
    } else if (f == NEGATIVE_INFINITY) {
      "-Infinity"
    } else {
      val bitValue = floatToIntBits(f)
      val negative = (bitValue & 0x80000000) != 0
      val exponent = (bitValue & 0x7f800000) >>> 23
      var significand = (bitValue & 0x007FFFFF) << 1

      if (exponent == 0 && significand == 0) {
        if (negative) "-0x0.0p0"
        else "0x0.0p0"
      } else {
        val hexString = new StringBuilder(10)

        if (negative) {
          hexString.append("-0x")
        } else {
          hexString.append("0x")
        }

        if (exponent == 0) {
          hexString.append("0.")

          var fractionDigits = 6
          while ((significand != 0) && ((significand & 0xF) == 0)) {
            significand >>>= 4
            fractionDigits -= 1
          }

          val hexSignificand = Integer.toHexString(significand)
          if (significand != 0 && fractionDigits > hexSignificand.length) {
            var digitDiff = fractionDigits - hexSignificand.length - 1
            while (digitDiff != 0) {
              hexString.append('0')
              digitDiff -= 1
            }
          }

          hexString.append(hexSignificand)
          hexString.append("p-126")
        } else {
          hexString.append("1.")

          var fractionDigits = 6
          while ((significand != 0) && ((significand & 0xF) == 0)) {
            significand >>>= 4
            fractionDigits -= 1
          }

          val hexSignificand = Integer.toHexString(significand)
          if (significand != 0 && fractionDigits > hexSignificand.length) {
            var digitDiff = fractionDigits - hexSignificand.length - 1
            while (digitDiff != 0) {
              hexString.append('0')
              digitDiff -= 1
            }
          }

          hexString.append(hexSignificand)
          hexString.append('p')
          hexString.append(Integer.toString(exponent - 127))
        }

        hexString.toString
      }
    }

  def toString(f: scala.Float): String = {
    if (isNaN(f)) {
      "NaN"
    } else if (f == POSITIVE_INFINITY) {
      "Infinity"
    } else if (f == NEGATIVE_INFINITY) {
      "-Infinity"
    } else {
      val cstr = stackalloc[CChar](32)
      stdio.snprintf(cstr, 32, c"%f", f.toDouble)
      fromCString(cstr)
    }
  }

  @inline def valueOf(s: String): Float =
    valueOf(parseFloat(s))

  @inline def valueOf(f: scala.Float): Float =
    new Float(f)
}
