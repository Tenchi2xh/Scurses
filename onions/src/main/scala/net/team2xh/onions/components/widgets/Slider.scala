package net.team2xh.onions.components.widgets

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.{Keys, Scurses}

case class Slider(parent: FramePanel, minValue: Int, maxValue: Int)(var currentValue: Varying[Int] = minValue)(implicit
    screen: Scurses
) extends Widget(parent, minValue) {

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    val width = innerWidth - 2
    val v     = currentValue.value
    val nx    = math.round((width.toDouble * (v - minValue)) / (maxValue - minValue)).toInt
    val knob =
      if (nx == 0)
        Symbols.SV + " " + v + " " + Symbols.SV_TO_SR
      else if (nx == width)
        Symbols.SV_TO_SL + " " + v + " " + Symbols.SV
      else
        Symbols.SV_TO_SL + " " + v + " " + Symbols.SV_TO_SR

    val left  = ((width - knob.length) * nx) / width
    val right = width - knob.length - left
    screen.put(0,
               0,
               " " + Symbols.SH * left + knob + Symbols.SH * right + " ",
               theme.foreground(focus),
               theme.background(focus)
    )
  }

  override def handleKeypress(keypress: Int): Unit = {
    if (keypress == '<' || keypress == Keys.SPACE) {
      currentValue := (currentValue.value - 1) max minValue
    } else if (keypress == '>' || keypress == Keys.ENTER) {
      currentValue := (currentValue.value + 1) min maxValue
    }
    needsRedraw = true
  }

  override def focusable: Boolean = true
  override def innerHeight: Int   = 1
}
