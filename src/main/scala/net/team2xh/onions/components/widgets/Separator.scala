package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.scurses.Scurses

case class Separator(parent: FramePanel)
               (implicit screen: Scurses) extends Widget(parent) {

  override val isFocusable: Boolean = false

  override def draw(focus: Boolean): Unit = {
    screen.put(0, 0, Symbols.SH * innerWidth)
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def innerHeight: Int = 1
}
