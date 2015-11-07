package net.team2xh.onions.components

import scala.language.implicitConversions

import net.team2xh.onions.{Symbols, Component}
import net.team2xh.scurses.{Keys, Scurses}

case class Frame(title: Option[String] = None, debug: Boolean = false)(implicit screen: Scurses) extends Component(None) {

  val panel = FramePanel(this)
  panel.focus = true

  var focusedPanel = panel

  private[Frame] val titleOffset = 2

  var width = screen.size._1 - 1
  var height = screen.size._2 - 1 - (if (debug) 1 else 0) - (if (title.isDefined) titleOffset else 0)

  def innerWidth = width
  def innerHeight = height

  var lastKeypress = -1

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
    while (k != Keys.ENTER) {
      lastKeypress = k
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

      k = screen.keypress()
    }
  }

  override def redraw(): Unit = {
    this.synchronized {
      if (!debug)
        draw()
      else {
        val start = System.currentTimeMillis
        draw()
        val ms = System.currentTimeMillis - start

        val key = if (lastKeypress >= 0) s"Keypress: $lastKeypress (${lastKeypress.toChar})" else "No key pressed"
        val time = s"Render time: ${ms}ms"
        val line = "%-17s | %-19s".format(key, time)

        if (title.isDefined) screen.translateOffset(y = titleOffset)
        screen.put(0, innerHeight + 1, line + " " * (innerWidth + 1 - line.length))
        panel.drawDebug()
        if (title.isDefined) screen.translateOffset(y = -titleOffset)

      }
      screen.refresh()
    }
  }

  def draw(): Unit = {
    if (title.isDefined) screen.translateOffset(y = titleOffset)
    // Draw panels recursively
    panel.redraw()
    if (title.isDefined) {
      screen.translateOffset(y = -titleOffset)
      drawTitle()
    }
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

}
