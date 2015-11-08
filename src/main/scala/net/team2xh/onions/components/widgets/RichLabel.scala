package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{TextWrap, Varying}
import net.team2xh.scurses.RichText.RichText
import net.team2xh.scurses.Scurses

case class RichLabel(parent: FramePanel, text: Varying[RichText])
                    (implicit screen: Scurses) extends Widget(parent, text) {

  override def draw(focus: Boolean, theme: ColorScheme): Unit = {
    screen.put(0, 0, text.value, theme)
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = 1
}
