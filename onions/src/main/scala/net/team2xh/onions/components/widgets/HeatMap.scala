package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.Math.GaussianArray
import net.team2xh.onions.utils.{Drawing, Math, Varying}
import net.team2xh.onions.{Palettes, Symbols}
import net.team2xh.scurses.Scurses

import scala.Numeric.Implicits._

case class HeatMap[T: Numeric](parent: FramePanel, values: Varying[Seq[(T, T)]],
                               labelX: String = "", labelY: String = "",
                               radius: Varying[Int] = 5, showLabels: Boolean = false)
                              (implicit screen: Scurses) extends Widget(parent, values, radius) {

  val gridSize = 8

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    val (xs, ys) = values.value.unzip

    val maxX = Math.aBitMoreThanMax(xs)
    val maxY = Math.aBitMoreThanMax(ys)
    val minX = Math.aBitLessThanMin(xs)
    val minY = Math.aBitLessThanMin(ys)

    val valuesLength = maxY.toString.length max minY.toString.length
    val x0 = valuesLength + (if (showLabels) 2 else 0)
    val graphWidth = (if (showLabels) innerWidth - 2 else innerWidth) - valuesLength
    val graphHeight = if (showLabels) innerHeight - 3 else innerHeight - 2

    // Draw grid
//    Drawing.drawGrid(x0, 0, graphWidth, graphHeight, gridSize, theme.accent1, theme.background,
//      showVertical = false, showHorizontal = false)
    // Draw axis values
    Drawing.drawAxisValues(x0 - valuesLength, 0, graphHeight, gridSize, minY, maxY, theme.accent3, theme.background, horizontal = false)
    Drawing.drawAxisValues(x0, graphHeight + 1, graphWidth, gridSize, minX, maxX, theme.accent3, theme.background)

    // Draw labels
    if (showLabels) {
      Drawing.drawAxisLabels(x0, graphWidth, graphHeight, labelX, labelY, theme)
    }

    // Prepare data
    // for each point of data, put a gaussian on the 2d array around its coordinate
    val dh = graphHeight * 2
    val array = GaussianArray(graphWidth, dh + 2, kernelRadius = radius.value)
    for (value <- values.value) {
      val nx = math.round((graphWidth * (value._1.toDouble - minX)) / (maxX - minX)).toInt
      val ny = dh - math.round((dh * (value._2.toDouble - minY)) / (maxY - minY)).toInt
      array.add(nx, ny)
    }
    // then take min/max of everything and draw colors
    val max = array.max
    for (x <- 0 until graphWidth; y <- 0 to dh by 2) {
      val v1 = array(x, y)
      val v2 = if (y != dh + 1) array(x, y + 1) else -1
      val c1 = Palettes.mapToRGB(v1, max, quadratic = true)
      val c2 = if (v2 != -1) Palettes.mapToRGB(v2, max, quadratic = true) else -1
      screen.put(x0 + x, y / 2, Symbols.BLOCK_UPPER, c1, c2)
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = parent.innerHeight - 3
}
