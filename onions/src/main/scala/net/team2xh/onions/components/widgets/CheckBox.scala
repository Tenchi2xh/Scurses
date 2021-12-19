package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{Drawing, Varying}
import net.team2xh.scurses.{Keys, Scurses}

final case class CheckBox(parent: FramePanel, text: String, checked: Varying[Boolean] = false)(implicit screen: Scurses)
    extends Widget(parent, checked) {

  def focusable = true

  def drawText(foreground: Int, background: Int): Unit = {
    val line = " [%s] %s".format(if (checked.value) "√" else " ", Drawing.clipText(text, innerWidth - 7))
    screen.put(0, 0, line + " " * (innerWidth - line.length - 1), foreground = foreground, background = background)
  }

  override def redraw(focus: Boolean, theme: ColorScheme): Unit =
    drawText(theme.foreground(focus), theme.background(focus))

  override def handleKeypress(keypress: Int): Unit =
    if (keypress == Keys.ENTER || keypress == Keys.SPACE)
      checked := !checked.value

  override def innerHeight: Int = 1
}
