package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.scurses.Scurses

object Separator {
  def apply(parent: FramePanel, symbol: String = Symbols.SH)(implicit screen: Scurses) =
    new Separator(parent, symbol)
}

class Separator(parent: FramePanel, symbol: String = Symbols.SH)(implicit screen: Scurses) extends Widget(parent) {

  val focusable = false

  override def redraw(focus: Boolean, theme: ColorScheme): Unit =
    screen.put(0, 0, symbol * innerWidth, foreground = theme.accent3, background = theme.background)

  override def handleKeypress(keypress: Int): Unit = {}

  override def innerHeight: Int = 1
}
