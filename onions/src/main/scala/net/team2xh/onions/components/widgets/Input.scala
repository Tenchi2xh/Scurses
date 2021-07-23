package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.{Drawing, Varying}
import net.team2xh.scurses.{Keys, Scurses}

case class Input(parent: FramePanel, var defaultText: String = "Input")(implicit screen: Scurses)
    extends Widget(parent) {

  var text: Varying[String] = ""
  def cursorIndex           = text.value.length

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    val cursorSymbol = if (focus) Symbols.BLOCK else " "
    val limit        = innerWidth - 3
    val t            = if (cursorIndex == 0 && !focus) "<" + defaultText + ">" else text.value
    val fg           = if (cursorIndex == 0 && !focus) theme.background else theme.foreground(focus)
    val l            = t.length
    val clippedText  = Drawing.clipText(t, limit, before = true)
    screen.put(0,
               0,
               " " + clippedText + cursorSymbol + " " * (innerWidth - l - 3) + " ",
               foreground = fg,
               background = if (focus) theme.background(focus) else theme.accent1
    )
  }

  override def handleKeypress(keypress: Int): Unit = {
    keypress match {
      case Keys.BACKSPACE =>
        if (cursorIndex > 0)
          text := text.value.init
      case _ => text := text.value + keypress.toChar
    }
    needsRedraw = true
  }

  override def focusable: Boolean = true
  override def innerHeight: Int   = 1
}
