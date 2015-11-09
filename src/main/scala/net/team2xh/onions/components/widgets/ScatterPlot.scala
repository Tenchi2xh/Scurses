package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{Drawing, Varying, Math}
import net.team2xh.scurses.{Colors, Scurses}

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
    // Draw values
    Drawing.drawAxisLabels(x0 - valuesLength, 0, graphHeight, gridSize, maxY, theme.accent3, theme.background, horizontal = false)
    Drawing.drawAxisLabels(x0, graphHeight + 1, graphWidth, gridSize, maxY, theme.accent3, theme.background)
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = parent.innerHeight - 3
}
