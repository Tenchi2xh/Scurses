package net.team2xh.onions.components.widgets

import net.team2xh.onions.Component
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.TextWrap
import net.team2xh.scurses.{Colors, Scurses}

case class Label(parent: FramePanel, text: String, alignment: Int = TextWrap.ALIGN_LEFT)
                (implicit screen: Scurses) extends Widget(parent) {

  var enabled = true
  def focusable = enabled

  var lines = Seq[String]()

  def drawText(foreground: Int, background: Int): Unit = {
    lines = TextWrap.wrapText(text, innerWidth - 1, alignment)
    for ((line, i) <- lines.zipWithIndex) {
      screen.put(0, i, " " + line + " " * (innerWidth - line.length - 1),
        foreground = foreground, background = background)
    }
  }

  override def draw(focus: Boolean): Unit = {
    val fg = if (focus && focusable) Colors.DIM_BLACK else Colors.BRIGHT_WHITE
    val bg = if (focus && focusable) Colors.BRIGHT_WHITE else Colors.DIM_BLACK
    drawText(fg, bg)
  }

  override def handleKeypress(keypress: Int): Unit = {
    keypress match {
      case _ =>
    }
  }

  override def innerHeight: Int = lines.length
}
