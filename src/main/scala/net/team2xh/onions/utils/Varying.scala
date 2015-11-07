package net.team2xh.onions.utils

import scala.language.implicitConversions

object Varying {
  implicit def stringToVarying(value: String): Varying[String] = Varying(value)
  implicit def intToVarying(value: Int): Varying[Int] = Varying(value)
}

case class Varying[T](initialValue: T) {

  private var storedValue = initialValue
  private var callbacks: List[() => Unit] = Nil

  def :=(newValue: T): Unit = {
    storedValue = newValue
    for (callback <- callbacks) callback()
  }

  def subscribe(callback: () => Unit): Unit = {
    callbacks ::= callback
  }

  def value = storedValue
}
