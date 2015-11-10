package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.Scurses

abstract class Widget(parent: FramePanel, values: Varying[_] *)
                     (implicit screen: Scurses) extends Component(Some(parent)) {

  parent.widgets += this

  for (value <- values)
    value.subscribe(() => {
      needsRedraw = true
      topLevel.redraw()
    })

  def focusable: Boolean

  var needsRedraw = true

  def draw(focus: Boolean, theme: ColorScheme): Unit = {
    redraw(focus, theme)
    needsRedraw = false
  }

  def redraw(focus: Boolean, theme: ColorScheme): Unit

  def handleKeypress(keypress: Int): Unit

  override def innerWidth: Int = parent.innerWidth - 3

  override def toString = this.getClass.getSimpleName + " @ " + Integer.toHexString(System.identityHashCode(this))
}
