package net.team2xh.onions

import java.awt.Color

import net.team2xh.scurses.Colors

object Palettes {

  val default = Seq(1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15)
  val rainbow = Seq(170, 203, 214, 226, 118, 81, 69)

  def mapToPalette(value: Double, max: Double, palette: Seq[Int]): Int = {
    val i = math.round((palette.length - 1) * value / max).toInt
    palette(i)
  }

  def mapToRGB(value: Double, max: Double,
               inverted: Boolean = false,
               quadratic: Boolean = false): Int = {

    val normalized = if (!inverted) 1 - value / max else value / max
    val squared = if (quadratic) normalized * normalized else normalized
    val color = Color.getHSBColor(squared.toFloat * 0.6666666f, 0.75f, 1.0f)
    Colors.fromRGBInt(color.getRGB)
  }

}
