package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{Drawing, Math, Varying}
import net.team2xh.onions.{Palettes, Symbols}
import net.team2xh.scurses.Scurses

import java.text.DecimalFormat
import scala.Numeric.Implicits._

/** Widget that displays a horizontal bar chart, given a sequence of values.
  *
  * @param parent     Parent panel in which the widget will be added
  * @param values     Sequence of values to display on the chart
  * @param labels     Sequence of names (labels) associated with each value, in order
  * @param min        Minimum value to display on the chart (scales to content by default)
  * @param max        Maximum value to display on the chart (scales to content by default)
  * @param palette    Color palette to use for the bars
  * @param showLabels Enables the display of the labels
  * @param showValues Enables the display of the axis values
  * @param screen     Implicit Scurses screen
  */
final case class BarChart[T: Numeric](parent: FramePanel,
                                      values: Varying[Seq[T]],
                                      labels: Seq[String] = Seq(),
                                      min: Option[Int] = None,
                                      max: Option[Int] = None,
                                      palette: Seq[Int] = Palettes.default,
                                      showLabels: Boolean = true,
                                      showValues: Boolean = true
)(implicit screen: Scurses)
    extends Widget(parent, values) {

  val gridWidth                   = 4
  override def focusable: Boolean = false
  override def innerHeight: Int   = parent.innerHeight - 3

  val df = new DecimalFormat("#.#")

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    val vs = values.value
    // If not enough labels were specified, name them according to their order
    val ls = labels.take(vs.length) ++ (labels.length until vs.length).map(n => "#" + (n + 1).toString)
    // Width of the largest label
    val labelLength = ls.map(_.length).max
    // Width of the chart itself
    val graphWidth  = if (showLabels) innerWidth - labelLength - 4 else innerWidth - 1
    val graphHeight = innerHeight - 2
    val valueMin    = min.getOrElse(Math.aBitLessThanMin(vs))
    val valueMax    = max.getOrElse(Math.aBitMoreThanMax(vs))
    // Draw grid
    Drawing.drawGrid(0,
                     0,
                     graphWidth,
                     graphHeight,
                     gridWidth,
                     theme.accent1,
                     theme.background,
                     showVertical = true,
                     showHorizontal = false
    )
    // Draw bars
    val spacing  = graphHeight / (vs.length - 1)
    val spaceTop = (graphHeight - (vs.length - 1) * spacing - 1) / 2
    val zero     = math.floor(((0 - valueMin) * graphWidth.toDouble) / (valueMax - valueMin)).toInt
    for ((y, i) <- (spaceTop until spaceTop + vs.length * spacing by spacing).zipWithIndex) {
      val length = math.round(((vs(i).toDouble - valueMin) * graphWidth) / (valueMax - valueMin)).toInt
      // Draw the bars themselves
      if (vs(i).toDouble >= 0) {
        screen.put(zero,
                   1 + y,
                   Symbols.BLOCK_RIGHT + Symbols.BLOCK * (length - zero) + Symbols.BLOCK_LEFT,
                   foreground = palette(i % palette.length),
                   background = theme.background
        )
      } else {
        screen.put(length - 1,
                   1 + y,
                   Symbols.BLOCK_RIGHT + Symbols.BLOCK * (zero - length) + Symbols.BLOCK_LEFT,
                   foreground = palette(i % palette.length),
                   background = theme.background
        )
      }
      // Draw values
      val valuePos = if (vs(i).toInt >= 0) length + 2 else zero + 1
      val text = vs(i) match {
        case _: Double => df.format(vs(i).toDouble)
        case _: Int    => vs(i).toString
      }
      if (showValues)
        screen.put(valuePos, 1 + y, text, foreground = theme.foreground, background = theme.background)

    }
    // Draw legends
    val spaceTopLabels = (graphHeight - vs.length) / 2
    if (showLabels)
      for (i <- vs.indices) {
        screen.put(graphWidth + 2,
                   1 + spaceTopLabels + i,
                   Symbols.SQUARE,
                   foreground = palette(i % palette.length),
                   background = theme.background
        )
        screen.put(graphWidth + 4,
                   1 + spaceTopLabels + i,
                   ls(i),
                   foreground = theme.accent3,
                   background = theme.background
        )
      }
    // Draw grid values
    Drawing.drawAxisValues(0,
                           innerHeight - 1,
                           graphWidth,
                           gridWidth,
                           valueMin,
                           valueMax,
                           theme.accent3,
                           theme.background
    )
  }

  override def handleKeypress(keypress: Int): Unit = {}

}
