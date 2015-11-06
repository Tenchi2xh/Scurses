package net.team2xh.onions.components

import net.team2xh.onions.Component
import net.team2xh.scurses.{Keys, Scurses}

object Frame {
  def apply(implicit screen: Scurses): Frame = new Frame(None)
  def apply(title: String)(implicit screen: Scurses): Frame = new Frame(Some(title))
}

class Frame(title: Option[String] = None)(implicit screen: Scurses) extends Component(None) {

  val panel = FramePanel(this)
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

  def switchFocusTo(panel: FramePanel): Unit = {
    focusedPanel.focus = false
    panel.focus = true
    focusedPanel = panel
  }

  def eventLoop(): Unit = {
    val tree = panel.getTreeWalk

    var k = screen.keypress()
    while (k != 'q'.toInt && k != Keys.ENTER) {
      k match {
        case Keys.UP =>
          val l = panel.widgets.length
          if (l > 0) {
            if (!focusedPanel.focusPreviousWidget) {
              val next = focusedPanel.getNextDirection(_.top, _.left)
              next foreach (panel => switchFocusTo(panel))
            }
          }
        case Keys.DOWN =>
          val l = panel.widgets.length
          if (l > 0) {
            if (!focusedPanel.focusNextWidget) {
              val next = focusedPanel.getNextDirection(_.bottom, _.left)
              next foreach (panel => switchFocusTo(panel))
            }
          }
        case Keys.LEFT =>
          val next = focusedPanel.getNextDirection(_.left, _.top)
          next foreach (panel => switchFocusTo(panel))
        case Keys.RIGHT =>
          val next = focusedPanel.getNextDirection(_.right, _.top)
          next foreach (panel => switchFocusTo(panel))
        case Keys.TAB =>
          val next = tree.dropWhile(_.id != focusedPanel.id).tail.headOption
          next match {
            case Some(panel) =>
              switchFocusTo(panel)
            case None =>
              switchFocusTo(panel)
          }
        case keypress =>
          focusedPanel.getFocusedWidget foreach {
            widget => widget.handleKeypress(keypress)
          }
      }
      redraw()

      screen.put(0, 0, k.toString)
      screen.refresh()

      k = screen.keypress()
    }
  }

}
