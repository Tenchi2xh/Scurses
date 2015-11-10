package net.team2xh.onions

object Palettes {

  val default = Seq(1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15)
  val rainbow = Seq(170, 203, 214, 226, 118, 81, 69)

  def mapToPalette(value: Double, max: Double, palette: Seq[Int]): Int = {
    val i = math.round((palette.length - 1) * value / max).toInt
    palette(i)
  }

}
