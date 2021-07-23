package net.team2xh.onions.utils

import scala.Numeric.Implicits._
import scala.collection.mutable.ArrayBuffer

object Math {

  def aBitMoreThanMax[T: Numeric](ns: Seq[T]) =
    ns.max.toInt + math.pow(10, math.log10(ns.max.toDouble / 10.0)).toInt

  def aBitLessThanMin[T: Numeric](ns: Seq[T]) =
    ns.min.toInt - math.pow(10, math.log10(ns.min.toDouble.abs / 10.0)).toInt

  val `√2π` = math.sqrt(2 * math.Pi)

  def gauss1d(x: Double, σ: Double, μ: Double = 0.0): Double = {
    val a = 1 / (σ * `√2π`)
    a * math.exp(-(x - μ) * (x - μ) / (2 * σ * σ))
  }

  def gauss2d(x: Double, y: Double, a: Double, σx: Double, σy: Double, μx: Double = 0.0, μy: Double = 0.0): Double =
    a * math.exp(-((x - μx) * (x - μx) / (2 * σx * σx) + (y - μy) * (y - μy) / (2 * σy * σy)))

  def simpleGauss1d(x: Double, σ: Double): Double =
    math.exp(-x * x / (2 * σ * σ))

  def simpleGauss2d(x: Double, y: Double, σ: Double): Double =
    math.exp(-(x * x / (2 * σ * σ) + y * y / (2 * σ * σ)))

  case class GaussianArray(width: Int, height: Int, kernelRadius: Int = 1) {

    val array = ArrayBuffer.fill[Double](width, height)(0.0)

    def add(x0: Int, y0: Int): Unit =
      for (x <- -kernelRadius to kernelRadius; y <- -kernelRadius to kernelRadius) {
        val xx = x0 + x
        val yy = y0 + y
        if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
          val v   = simpleGauss2d(x, y, kernelRadius / 2.0)
          val old = array(xx)(yy)
          array(xx).update(yy, old + v)
        }
      }

    def apply(x0: Int, y0: Int): Double =
      array(x0)(y0)

    def min: Double = array.map(_.min).min

    def max: Double = array.map(_.max).max

  }
}
