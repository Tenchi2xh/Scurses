package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{Drawing, Varying, Math}
import net.team2xh.scurses.{Colors, Scurses}

import scala.collection.mutable

case class ScatterPlot(parent: FramePanel, values: Varying[Seq[(Int, Int)]],
                  labelX: String = "", labelY: String = "",
                  color: Int = 81, showLabels: Boolean = true)
                 (implicit screen: Scurses) extends Widget(parent, values) {

  val gridSize = 4

  override def draw(focus: Boolean, theme: ColorScheme): Unit = {
    val (xs, ys) = values.value.unzip

    val maxX = Math.aBitMoreThanMax(xs)
    val maxY = Math.aBitMoreThanMax(ys)
    val minX = Math.aBitLessThanMin(xs)
    val minY = Math.aBitLessThanMin(ys)

    val valuesLength = maxY.toString.length max minY.toString.length
    val x0 = valuesLength + (if (showLabels) 2 else 0)
    val graphWidth = (if (showLabels) innerWidth - 3 else innerWidth - 1) - valuesLength
    val graphHeight = innerHeight - 2

    // Draw grid
    Drawing.drawGrid(x0, 0, graphWidth, graphHeight, gridSize, theme.accent1, theme.background,
                     showVertical = true, showHorizontal = true)
    // Draw axis values
    Drawing.drawAxisLabels(x0 - valuesLength, 0, graphHeight, gridSize, minY, maxY, theme.accent3, theme.background, horizontal = false)
    Drawing.drawAxisLabels(x0, graphHeight + 1, graphWidth, gridSize, minX, maxX, theme.accent3, theme.background)

    // Draw labels
    if (showLabels) {
      val lX = if (labelX.length > graphWidth) labelX.substring(0, graphWidth - 3) + "..."
        else labelX
      val lY = if (labelY.length > graphHeight) labelY.substring(0, graphHeight - 3) + "..."
        else labelY
      screen.put(x0 + (graphWidth - lX.length) / 2, graphHeight + 2, lX, theme.accent3, theme.background)
      val y0 = (graphHeight - lY.length) / 2
      for ((char, y) <- lY.zipWithIndex) {
        screen.put(0, y0 + y, "" + char, theme.accent3, theme.background)
      }
    }

    // Prepare values (we use half vertical resolution)
    val points = mutable.MutableList.fill[Int](graphWidth+1, graphHeight+1)(0)
    val charHeight = maxY.toDouble / graphHeight
    for (value <- values.value) {
      val nx = math.round((graphWidth.toDouble * (value._1 - minX)) / (maxX - minX)).toInt
      val ny = graphHeight - math.round((graphHeight.toDouble * (value._2 - minY)) / (maxY - minY)).toInt
      val point = points(nx)(ny)
      val isLower = if ((value._2 % charHeight) < (charHeight / 2.0)) 1 else 2
      points(nx).update(ny, point | isLower)
    }
    // Plot values
    for (x <- 0 to graphWidth; y <- 0 to graphHeight) {
      val point = points(x)(y)
      val symbol = point match {
        case 0 => ""
        case 1 => Symbols.BLOCK_UPPER
        case 2 => Symbols.BLOCK_LOWER
        case 3 => Symbols.BLOCK
      }
      if (point != 0)
        screen.put(x0 + x, y, symbol, foreground = color, background = theme.background)
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = parent.innerHeight - 3
}
