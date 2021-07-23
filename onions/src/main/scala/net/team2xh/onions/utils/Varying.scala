package net.team2xh.onions.utils

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.scurses.RichText.RichText

import scala.language.implicitConversions

object Varying {
  implicit def stringToVarying(value: String): Varying[String]                                  = new Varying(value)
  implicit def richTextToVarying(value: RichText): Varying[RichText]                            = new Varying(value)
  implicit def numericToVarying[T: Numeric](value: T): Varying[T]                               = new Varying(value)
  implicit def booleanToVarying(value: Boolean): Varying[Boolean]                               = new Varying(value)
  implicit def colorSchemeToVarying(value: ColorScheme): Varying[ColorScheme]                   = new Varying(value)
  implicit def seqNumericToVarying[T: Numeric](values: Seq[T]): Varying[Seq[T]]                 = new Varying(values)
  implicit def seqTuple2NumericToVarying[T: Numeric](values: Seq[(T, T)]): Varying[Seq[(T, T)]] = new Varying(values)

  def from[T](varying: Varying[T], initial: T, result: T => T): Varying[T] = new Varying[T](initial) {
    varying.subscribe { () =>
      this := result(varying.value)
    }
  }
}

class Varying[T](initialValue: T) {

  var storedValue                         = initialValue
  private var callbacks: List[() => Unit] = Nil

  def :=(newValue: T): Varying[T] = {
    storedValue = newValue
    for (callback <- callbacks) callback()
    this
  }

  def subscribe(callback: () => Unit): Unit =
    callbacks ::= callback

  def value = storedValue
}
