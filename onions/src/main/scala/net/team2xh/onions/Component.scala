package net.team2xh.onions

import net.team2xh.onions.Themes.ColorScheme

abstract class Component(parent: Option[Component]) {

  def innerWidth: Int
  def innerHeight: Int

  def topLevel: Component = {
    parent match {
      case None => this
      case Some(p) => p.topLevel
    }
  }

  def redraw(): Unit = {
    topLevel.redraw()
  }

}
