package net.team2xh.onions.components.widgets

import java.text.DecimalFormat

import net.team2xh.onions.{Symbols, Palettes}
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{Drawing, Math}
import net.team2xh.scurses.Scurses

import scala.Numeric.Implicits._

case class Histogram[T: Numeric](parent: FramePanel, initialValues: Seq[T] = Seq[Double](),
                                 palette: Seq[Int] = Palettes.rainbow,
                                 min: Option[Int] = None, max: Option[Int] = None,
                                 labelY: String = "",  showLabels: Boolean = true,
                                 showValues: Boolean = true)
                                (implicit screen: Scurses) extends Widget(parent) {

  val gridSize = 4

  override def focusable: Boolean = false

  override def innerHeight: Int = parent.innerHeight - 3

  val limit = 400
  var values = initialValues
  var counter = 0
  val df = new DecimalFormat("#.#")

  def push(value: T): Unit = {
    values +:= value
    values = values.take(limit)
    counter = (counter + 1) % gridSize
    needsRedraw = true
  }

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {

    val valueMin = min.getOrElse(if (values.isEmpty) 0 else Math.aBitLessThanMin(values))
    val valueMax = max.getOrElse(if (values.isEmpty) 10 else Math.aBitMoreThanMax(values) + 1)

    val valuesLength = valueMax.toString.length max valueMin.toString.length
    val x0 = valuesLength + (if (showLabels) 2 else 0)
    val graphWidth = (if (showLabels) innerWidth - 3 else innerWidth - 1) - valuesLength
    val graphHeight = innerHeight - 1

    // Draw grid
    Drawing.drawGrid(x0, 0, graphWidth, graphHeight, gridSize, theme.accent1, theme.background,
      showVertical = true, showHorizontal = true, gridOffsetX = graphWidth % 4 + (gridSize - counter))

    // Draw axis values
    Drawing.drawAxisValues(x0 - valuesLength, 0, graphHeight, gridSize,
      valueMin, valueMax, theme.accent3, theme.background, horizontal = false)

    // Draw bars
    val charHeight = (valueMax - valueMin).toDouble / graphHeight
    for (i <- 0 until ((graphWidth - 1) min values.length)) {
      val v = values(i)
      val ny = graphHeight - math.round((graphHeight * (v.toDouble - valueMin)) / (valueMax - valueMin)).toInt

      val color = Palettes.mapToRGB((v.toDouble - valueMin).abs, (valueMax - valueMin).abs)

      for (y <- ny to graphHeight) {
        val isLower = v.toDouble % charHeight < charHeight / 2.0
        val s =
          if (y == graphHeight) Symbols.BLOCK_UPPER
          else if (y == ny && (!isLower || ny == 0)) Symbols.BLOCK_LOWER
          else Symbols.BLOCK
        screen.put(x0 + (graphWidth - i - 1), y, s, color, theme.background)
      }
    }

    // Draw labels
    if (showLabels) {
      Drawing.drawAxisLabels(x0, graphWidth, graphHeight, labelY = labelY, theme = theme)
    }
  }

  override def handleKeypress(keypress: Int): Unit = {}

}
