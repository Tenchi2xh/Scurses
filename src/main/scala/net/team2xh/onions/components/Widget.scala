package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.Scurses

abstract class Widget(parent: FramePanel, values: Varying[_] *)
                     (implicit screen: Scurses) extends Component(Some(parent)) {

  parent.widgets += this

  for (value <- values)
    value.subscribe(topLevel.redraw)

  def focusable: Boolean

  def draw(focus: Boolean = false): Unit
  def handleKeypress(keypress: Int): Unit

  override def innerWidth: Int = parent.innerWidth - 3

}
