package net.team2xh.onions.components

import net.team2xh.onions.Component

abstract class Widget(parent: FramePanel) extends Component(Some(parent)) {

  parent.widgets += this

  def focusable: Boolean

  def draw(focus: Boolean = false): Unit
  def handleKeypress(keypress: Int): Unit

  override def innerWidth: Int = parent.innerWidth - 3

}
