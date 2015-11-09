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

    val valuesLength = maxX.toString.length
    val x0 = valuesLength + (if (showLabels) 2 else 0)
    val graphWidth = (if (showLabels) innerWidth - 3 else innerWidth - 1) - valuesLength
    val graphHeight = innerHeight - 2

    // Draw grid
    Drawing.drawGrid(x0, 0, graphWidth, graphHeight, gridSize, theme.accent1, theme.background,
                     showVertical = true, showHorizontal = true)
    // Draw axis values
    Drawing.drawAxisLabels(x0 - valuesLength, 0, graphHeight, gridSize, maxY, theme.accent3, theme.background, horizontal = false)
    Drawing.drawAxisLabels(x0, graphHeight + 1, graphWidth, gridSize, maxX, theme.accent3, theme.background)

    // Prepare values (we use half vertical resolution)
    val points = mutable.MutableList.fill[Int](graphWidth+1, graphHeight+1)(0)
    val charHeight = maxY.toDouble / graphHeight
    for (value <- values.value) {
      val nx = math.round((graphWidth.toDouble * value._1) / maxX).toInt
      val ny = graphHeight - math.round((graphHeight.toDouble * value._2) / maxY).toInt
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
