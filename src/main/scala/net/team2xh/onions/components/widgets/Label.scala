package net.team2xh.onions.components.widgets

import net.team2xh.onions.Component
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{Varying, TextWrap}
import net.team2xh.scurses.{Keys, Colors, Scurses}

case class Label(parent: FramePanel, text: Varying[String],
                 alignment: Varying[Int] = TextWrap.ALIGN_LEFT, var action: () => Unit = () => {})
                (implicit screen: Scurses) extends Widget(parent, text, alignment) {

  var enabled = true
  def focusable = enabled

  var lines = Seq[String]()

  def drawText(foreground: Int, background: Int): Unit = {
    lines = TextWrap.wrapText(text.value, innerWidth - 1, alignment.value)
    for ((line, i) <- lines.zipWithIndex) {
      screen.put(0, i, " " + line + " " * (innerWidth - line.length - 1),
        foreground = foreground, background = background)
    }
  }

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    drawText(theme.foreground(focus), theme.background(focus))
  }

  override def handleKeypress(keypress: Int): Unit = {
    if (keypress == Keys.ENTER || keypress == Keys.SPACE) action()
  }

  override def innerHeight: Int = lines.length
}
