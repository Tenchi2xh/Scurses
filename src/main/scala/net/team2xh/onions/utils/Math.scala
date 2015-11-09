package net.team2xh.onions.utils

object Math {

  def aBitMoreThanMax(ns: Seq[Int]) = ns.max + math.pow(10, math.log10(ns.max / 10).toInt).toInt

}
