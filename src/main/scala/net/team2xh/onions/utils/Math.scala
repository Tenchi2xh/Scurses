package net.team2xh.onions.utils

object Math {

  def aBitMoreThanMax(ns: Seq[Int]) = ns.max + math.pow(10, math.log10(ns.max / 10.0)).toInt

  def aBitLessThanMin(ns: Seq[Int]) = ns.min - math.pow(10, math.log10(ns.min.abs / 10.0)).toInt
}
