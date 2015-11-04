package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.scurses.Scurses

object Frame {
  def apply(implicit screen: Scurses): Frame = new Frame(None)
  def apply(title: String)(implicit screen: Scurses): Frame = new Frame(Some(title))
}

class Frame(title: Option[String] = None)(implicit screen: Scurses) extends Component(None) {

  val panel = FramePanel(this, innerWidth, innerHeight)
  panel.focus = true

  var focusedPanel = panel

  def innerWidth = screen.size._1 - 1
  def innerHeight = screen.size._2 - 1

  override def redraw(): Unit = {
    // Draw panels recursively
    panel.redraw()

    screen.refresh()
  }

  def show(): Unit = {
    redraw()
    eventLoop()
  }
  
  def eventLoop(): Unit = {
    var k = screen.keypress()
    while (k != 'q'.toInt && k != 10) {
      k match {
        // TODO: Put these magic numbers somewhere
        case 16 => // Up arrow
          val l = panel.widgets.length
          if (l > 0) {
            focusedPanel.widgetFocus += -1 + l
            focusedPanel.widgetFocus %= l
          }
        case 14 => // Down arrow
          val l = panel.widgets.length
          if (l > 0) {
            focusedPanel.widgetFocus += 1
            focusedPanel.widgetFocus %= l
          }
        case 2 => // Left arrow
          // TODO: Find left if no immediate left
          val l = focusedPanel.left
          if (l.isDefined) {
            focusedPanel.focus = false
            l.get.focus = true
            focusedPanel = l.get
          }
        case 6 => // Right arrow
          // TODO: Find right if no immediate right
          val r = focusedPanel.right
          if (r.isDefined) {
            focusedPanel.focus = false
            r.get.focus = true
            focusedPanel = r.get
          }
        case _ => // Delegate
      }
      redraw()

      screen.put(0, 0, k.toString)
      screen.refresh()

      k = screen.keypress()
    }
  }

}
