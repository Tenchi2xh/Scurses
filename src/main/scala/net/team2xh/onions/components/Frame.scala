package net.team2xh.onions.components

import net.team2xh.onions.{Symbols, Component}
import net.team2xh.scurses.{Keys, Scurses}

object Frame {
  def apply(implicit screen: Scurses): Frame = new Frame(None)
  def apply(title: String)(implicit screen: Scurses): Frame = new Frame(Some(title))
}

class Frame(title: Option[String] = None)(implicit screen: Scurses) extends Component(None) {

  var debug = false

  val panel = FramePanel(this)
  panel.focus = true

  var focusedPanel = panel

  private[Frame] val titleOffset = 2

  def innerWidth = screen.size._1 - 1
  def innerHeight = screen.size._2 - 1 - (if (debug) 1 else 0) - (if (title.isDefined) titleOffset else 0)

  override def redraw(): Unit = {
    // Draw panels recursively
    if (title.isDefined) screen.translateOffset(y = titleOffset)
    panel.redraw()
    if (title.isDefined) {
      screen.translateOffset(y = -titleOffset)
      drawTitle()
    }

    screen.refresh()
  }

  private[Frame] def drawTitle(): Unit = {
    title.foreach { t =>
      screen.put(0, 0, Symbols.TLC_S_TO_D)
      screen.put(innerWidth + 1, 0, Symbols.TRC_D_TO_S)
      screen.put(1, 0, Symbols.DH * (innerWidth - 1))
      screen.put(0, 1, Symbols.SV)
      screen.put(innerWidth + 1, 1, Symbols.SV)
      screen.put((innerWidth + 1 - t.length) / 2, 1, t)
      screen.put(0, 2, Symbols.SV_TO_SR)
      screen.put(innerWidth + 1, 2, Symbols.SV_TO_SL)
    }
  }

  def show(): Unit = {
    if (debug) drawDebug(0)
    else redraw()
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
    while (k != Keys.ENTER) {
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
      if (debug) drawDebug(k)
      else redraw()

      k = screen.keypress()
    }
  }

  def drawDebug(k: Int): Unit = {
    val start = System.currentTimeMillis
    redraw()
    val ms = System.currentTimeMillis - start

    val key = s"Keypress: $k (${k.toChar})"
    val time = s"Render time: ${ms}ms"
    val line = "%-17s | %-19s".format(key, time)

    if (title.isDefined) screen.translateOffset(y = titleOffset)
    screen.put(0, innerHeight + 1, line + " " * (innerWidth + 1 - line.length))
    panel.drawDebug()
    if (title.isDefined) screen.translateOffset(y = -titleOffset)

    screen.refresh()
  }

}
