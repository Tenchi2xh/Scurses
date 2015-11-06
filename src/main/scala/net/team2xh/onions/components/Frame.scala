package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.scurses.{Keys, Scurses}

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
    val tree = panel.getTreeWalk

    var k = screen.keypress()
    while (k != 'q'.toInt && k != Keys.ENTER) {
      k match {
        case Keys.UP_ARROW =>
          val l = panel.widgets.length
          if (l > 0) {
            focusedPanel.widgetFocus += -1 + l
            focusedPanel.widgetFocus %= l
          }
        case Keys.DOWN_ARROW =>
          val l = panel.widgets.length
          if (l > 0) {
            focusedPanel.widgetFocus += 1
            focusedPanel.widgetFocus %= l
          }
        case Keys.TAB =>
          val next = tree.dropWhile(_ != focusedPanel).tail.headOption
          next match {
            case Some(panel) =>
              focusedPanel.focus = false
              panel.focus = true
              focusedPanel = panel
            case None =>
              focusedPanel.focus = false
              panel.focus = true
              focusedPanel = panel
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
