package net.team2xh.onions.components.widgets

import net.team2xh.onions.{Symbols, Palettes}
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{Drawing, Varying}
import net.team2xh.scurses.Scurses

case class BarGraph(parent: FramePanel, values: Varying[Seq[Int]],
                     labels: Seq[String] = Seq(),
                     max: Int = -1, palette: Seq[Int] = Palettes.default,
                     showLabels: Boolean = true, showValues: Boolean = true)
                    (implicit screen: Scurses) extends Widget(parent, values) {

  val gridWidth = 5

  override def draw(focus: Boolean, theme: ColorScheme): Unit = {
    val vs = values.value
    val ls = labels.take(vs.length) ++ (labels.length until vs.length).map(n => "#" + (n+1).toString)
    val labelLength = ls.map(_.length).max
    val graphLength = if (showLabels) innerWidth - labelLength - 4 else innerWidth - 1
    val graphHeight = innerHeight - 2
    val valueMin = 0  // For now, only from 0
    val valueMax =
      if (max == -1)
        vs.max + math.pow(10, math.log10(vs.max / 10).toInt).toInt  // A "bit" more than max value
      else
        max
    // Draw grid
    Drawing.drawGrid(0, 0, graphLength, graphHeight, gridWidth, theme.accent1, theme.background)
    // Draw bars
    val spacing = graphHeight / (vs.length - 1)
    val spaceTop = (graphHeight - (vs.length - 1) * spacing - 1) / 2
    for ((y, i) <- (spaceTop until spaceTop + vs.length * spacing by spacing).zipWithIndex) {
      val length = math.floor((vs(i) * graphLength.toDouble) / valueMax).toInt
      // Draw bars
      screen.put(0, 1 + y, Symbols.BLOCK_RIGHT + Symbols.BLOCK * length + Symbols.BLOCK_LEFT,
        foreground = palette(i % palette.length))
      // Draw values
      if (showValues)
        screen.put(length + 2, 1 + y, vs(i).toString, foreground = theme.foreground, background = theme.background)
    }
    // Draw legends
    if (showLabels)
      for (i <- vs.indices) {
        screen.put(graphLength + 2, 1 + i, Symbols.SQUARE,
          foreground = palette(i % palette.length), background = theme.background)
        screen.put(graphLength + 4, 1 + i, ls(i),
          foreground = theme.accent3, background = theme.background)
      }
    // Draw grid values
    for (x <- 0 until graphLength by gridWidth) {
      val index = math.floor((x * valueMax.toDouble) / graphLength).toInt
      screen.put(x, innerHeight - 1, index.toString, foreground = theme.accent3, background = theme.background)
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = parent.innerHeight - 3
}
