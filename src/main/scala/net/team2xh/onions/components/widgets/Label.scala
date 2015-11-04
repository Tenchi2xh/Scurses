package net.team2xh.onions.components.widgets

import net.team2xh.onions.{Utils, Component}
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.scurses.{Colors, Scurses}

case class Label(parent: FramePanel, text: String)
           (implicit screen: Scurses) extends Widget(parent) {

  val lines = Utils.wrapText(text, innerWidth - 2)

  def drawText(foreground: Int, background: Int): Unit = {
    for ((line, i) <- lines.zipWithIndex) {
      screen.put(0, i, " " + line + " ", foreground = foreground, background = background)
    }
  }

  override def draw(focus: Boolean): Unit = {
    val fg = if (focus) Colors.DIM_BLACK else Colors.BRIGHT_WHITE
    val bg = if (focus) Colors.BRIGHT_WHITE else Colors.DIM_BLACK
    drawText(fg, bg)
  }

  override def handleKeypress(keypress: Int): Unit = ???

  override def innerHeight: Int = lines.length
}
