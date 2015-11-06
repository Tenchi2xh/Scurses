package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.scurses.Scurses

object Separator {
  def apply(parent: FramePanel, symbol: String = Symbols.SH)(implicit screen: Scurses) = {
    new Separator(parent, symbol)
  }
}

class Separator(parent: FramePanel, symbol: String = Symbols.SH)
               (implicit screen: Scurses) extends Widget(parent) {

  val focusable = false

  override def draw(focus: Boolean): Unit = {
    screen.put(0, 0, symbol * innerWidth)
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def innerHeight: Int = 1
}
