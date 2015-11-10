package net.team2xh.onions.components.widgets

import net.team2xh.onions.{Symbols, Palettes}
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{Drawing, Varying, Math}
import net.team2xh.scurses.Scurses

case class BarGraph(parent: FramePanel, values: Varying[Seq[Int]],
                     labels: Seq[String] = Seq(),
                     min: Int = -1, max: Int = -1, palette: Seq[Int] = Palettes.default,
                     showLabels: Boolean = true, showValues: Boolean = true)
                    (implicit screen: Scurses) extends Widget(parent, values) {

  val gridWidth = 4

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    val vs = values.value
    val ls = labels.take(vs.length) ++ (labels.length until vs.length).map(n => "#" + (n+1).toString)
    val labelLength = ls.map(_.length).max
    val graphWidth = if (showLabels) innerWidth - labelLength - 4 else innerWidth - 1
    val graphHeight = innerHeight - 2
    val valueMin =
      if (min == -1)
        Math.aBitLessThanMin(vs)
      else
        min
    val valueMax =
      if (max == -1)
        Math.aBitMoreThanMax(vs)
      else
        max
    // Draw grid
    Drawing.drawGrid(0, 0, graphWidth, graphHeight, gridWidth, theme.accent1, theme.background,
                     showVertical = true, showHorizontal = false)
    // Draw bars
    val spacing = graphHeight / (vs.length - 1)
    val spaceTop = (graphHeight - (vs.length - 1) * spacing - 1) / 2
    val zero = math.floor(((0 - valueMin) * graphWidth.toDouble) / (valueMax - valueMin)).toInt
    for ((y, i) <- (spaceTop until spaceTop + vs.length * spacing by spacing).zipWithIndex) {
      val length = math.floor(((vs(i) - valueMin) * graphWidth.toDouble) / (valueMax - valueMin)).toInt
      // Draw bars
      if (vs(i) >= 0) {
        screen.put(zero, 1 + y, Symbols.BLOCK_RIGHT + Symbols.BLOCK * (length-zero) + Symbols.BLOCK_LEFT,
          foreground = palette(i % palette.length), background = theme.background)
      } else {
        screen.put(zero - length - 1, 1 + y, Symbols.BLOCK_RIGHT + Symbols.BLOCK * length + Symbols.BLOCK_LEFT,
          foreground = palette(i % palette.length), background = theme.background)
      }
      // Draw values
      val valuePos = if (vs(i) >= 0) length + 2 else zero + 1
      if (showValues)
        screen.put(valuePos, 1 + y, vs(i).toString, foreground = theme.foreground, background = theme.background)

    }
    // Draw legends
    val spaceTopLabels = (graphHeight - vs.length) / 2
    if (showLabels)
      for (i <- vs.indices) {
        screen.put(graphWidth + 2, 1 + spaceTopLabels + i, Symbols.SQUARE,
          foreground = palette(i % palette.length), background = theme.background)
        screen.put(graphWidth + 4, 1 + spaceTopLabels + i, ls(i),
          foreground = theme.accent3, background = theme.background)
      }
    // Draw grid values
    Drawing.drawAxisValues(0, innerHeight - 1, graphWidth, gridWidth, valueMin, valueMax,
                           theme.accent3, theme.background, horizontal = true)
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = parent.innerHeight - 3
}
