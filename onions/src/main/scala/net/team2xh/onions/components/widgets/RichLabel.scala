package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{TextWrap, Varying}
import net.team2xh.scurses.RichText.RichText
import net.team2xh.scurses.Scurses

case class RichLabel(parent: FramePanel, text: Varying[RichText])(implicit screen: Scurses)
    extends Widget(parent, text) {

  var lines = Seq[RichText]()

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    lines = TextWrap.wrapText(text.value, innerWidth - 1).toSeq
    for ((line, i) <- lines.zipWithIndex)
      screen.putRichText(1, i, lines(i), theme.foreground, theme.background)
  }

  override def handleKeypress(keypress: Int): Unit = {}

  override def focusable: Boolean = false
  override def innerHeight: Int   = lines.length
}
