package net.team2xh.onions.components.widgets

import net.team2xh.onions.Component
import net.team2xh.onions.components.Widget

class Label(parent: Component) extends Widget(parent) {

  override def drawUnfocused(): Unit = {

  }

  override def handleKeypress(keypress: Int): Unit = ???

  override def innerWidth: Int = parent.innerWidth

  override def innerHeight: Int = 1
}
