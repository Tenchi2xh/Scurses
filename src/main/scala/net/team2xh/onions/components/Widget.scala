package net.team2xh.onions.components

import net.team2xh.onions.Component

abstract class Widget(parent: Component) extends Component(Some(parent)) {

  def drawFocused(): Unit = drawUnfocused()
  def drawUnfocused(): Unit
  def handleKeypress(keypress: Int): Unit

}
