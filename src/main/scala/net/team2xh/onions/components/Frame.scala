package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.onions.drawing.Box
import net.team2xh.scurses.Scurses

object Frame {
  def apply(implicit screen: Scurses): Frame = new Frame(None)
  def apply(title: String)(implicit screen: Scurses): Frame = new Frame(Some(title))
}

class Frame(title: Option[String] = None)(implicit screen: Scurses) extends Component(None) {

  val panel = FramePanel(this, innerWidth, innerHeight)

  def innerWidth = screen.size._1 - 1
  def innerHeight = screen.size._2 - 1

  override def redraw(): Unit = {
    val columns = panel.horizontalDepth
    val rows = panel.verticalDepths

    // Draw boxes
//    Box.drawColumns(0, 0, innerWidth + 2, innerHeight + 2, columns, rows)

    // Draw panels recursively
    panel.redraw()

    screen.refresh()
  }

  def show(): Unit = {
    redraw()
    eventLoop()
  }
  
  def eventLoop(): Unit = {
    screen.keypress()
  }

}
