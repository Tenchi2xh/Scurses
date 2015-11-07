package net.team2xh.onions.utils

import scala.language.implicitConversions

object Varying {
  implicit def stringToVarying(value: String): Varying[String] = new Varying(value)
  implicit def intToVarying(value: Int): Varying[Int] = new Varying(value)
  implicit def seqIntToVarying(values: Seq[Int]): Varying[Seq[Int]] = new Varying(values)

  def from[T](varying: Varying[T], initial: T, result: T => T): Varying[T] = new Varying[T](initial) {
    varying.subscribe(() => {
      this := result(varying.value)
    })
  }
}

class Varying[T](initialValue: T) {

  var storedValue = initialValue
  private var callbacks: List[() => Unit] = Nil

  def :=(newValue: T): Varying[T] = {
    storedValue = newValue
    for (callback <- callbacks) callback()
    this
  }

  def subscribe(callback: () => Unit): Unit = {
    callbacks ::= callback
  }

  def value = storedValue
}
