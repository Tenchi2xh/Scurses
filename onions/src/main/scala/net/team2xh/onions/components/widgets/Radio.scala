package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.{Drawing, Varying}
import net.team2xh.scurses.{Keys, Scurses}

object Radio {
  def apply(parent: FramePanel, options: Seq[String])(implicit screen: Scurses): Varying[Int] = {
    val choice: Varying[Int] = 0
    for ((option, i) <- options.zipWithIndex) {
      Radio(parent, option, i, choice)
    }
    choice := 0
  }
}

private[widgets] case class Radio(parent: FramePanel, text: String, id: Int, choice: Varying[Int])
                                 (implicit screen: Scurses) extends Widget(parent) {

  def focusable = true

  var checked: Boolean = false

  choice.subscribe(() => {
    checked = id == choice.value
    needsRedraw = true
  })

  def drawText(foreground: Int, background: Int): Unit = {
    val line = " (%s) %s".format(if (checked) "●" else " ", Drawing.clipText(text, innerWidth - 7))
    screen.put(0, 0, line + " " * (innerWidth - line.length - 1),
      foreground = foreground, background = background)
  }

  override def redraw(focus: Boolean, theme: ColorScheme): Unit = {
    drawText(theme.foreground(focus), theme.background(focus))
  }

  override def handleKeypress(keypress: Int): Unit = {
    if (keypress == Keys.ENTER || keypress == Keys.SPACE)
        choice := id
  }

  override def innerHeight: Int = 1
}
