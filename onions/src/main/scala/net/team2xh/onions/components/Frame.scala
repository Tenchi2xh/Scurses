package net.team2xh.onions.components

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.utils.Varying

import scala.language.implicitConversions

import net.team2xh.onions.{Themes, Symbols, Component}
import net.team2xh.scurses.{Keys, Scurses}

case class Frame(title: Option[String] = None, var debug: Varying[Boolean] = false,
                 var theme: Varying[ColorScheme] = Themes.default)
                (implicit screen: Scurses) extends Component(None) {

  theme.subscribe { () =>
    clear()
    panel.markAllForRedraw()
  }
  debug.subscribe { () =>
    clear()
    panel.markAllForRedraw()
  }

  def currentTheme = theme.value
  
  val panel = FramePanel(this)
  panel.focus = true

  var focusedPanel = panel

  private[Frame] val titleOffset = 2

  var width = 0
  var height = 0
  def resize(size: (Int, Int)): Unit = {
    width = size._1 - 1
    height = size._2 - 1
  }

  resize(screen.size)

  def innerWidth = width
  def innerHeight = height - (if (debug.value) 1 else 0) - (if (title.isDefined) titleOffset else 0)

  var lastKeypress = -1

  def show(): Unit = {
    redraw()
    eventLoop()
  }

  def clear(): Unit = {
    for (y <- 0 until height) {
      screen.put(0, y, " " * width, background = currentTheme.background)
    }
  }

  def switchFocusTo(panel: FramePanel): Unit = {
    focusedPanel.getFocusedWidget.foreach(_.needsRedraw = true)
    focusedPanel.focus = false
    panel.focus = true
    focusedPanel = panel
    focusedPanel.getFocusedWidget.foreach(_.needsRedraw = true)
  }

  def eventLoop(): Unit = {
    val tree = panel.getTreeWalk

    var k = screen.keypress()
    while (k != Keys.ESC && k != Keys.CTRL_C) {
      lastKeypress = k
      k match {
        case Keys.RESIZE =>
          val s = screen.size
          resize(s)
          clear()
          panel.markAllForRedraw()
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
        case Keys.CTRL_SPACE =>
          focusedPanel.nextTab()
        case k if k == Keys.TAB || k == Keys.SHIFT_TAB =>
          val t = if (k == Keys.TAB) tree else tree.reverse
          val next = t.dropWhile(_.id != focusedPanel.id).tail.headOption
          next match {
            case Some(panel) =>
              switchFocusTo(panel)
            case None =>
              if (k == Keys.TAB)
                switchFocusTo(panel)
              else
                switchFocusTo(tree.last)
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
      if (!debug.value)
        draw()
      else {
        val start = System.currentTimeMillis
        draw()
        val ms = System.currentTimeMillis - start

        val key = if (lastKeypress >= 0) s"Keypress: $lastKeypress (${Keys.repr(lastKeypress)})" else "No key pressed"
        val time = s"Render time: ${ms}ms"
        val left = "%-24s | %-19s".format(key, time)
        val widget = if (focusedPanel.widgets.isEmpty) "Ø" else focusedPanel.widgets(focusedPanel.widgetFocus)
        val right = s"Panel $focusedPanel, $widget"
        val n = innerWidth + 1 - left.length
        val line = s"%s%${n}s".format(left, right)

        if (title.isDefined) screen.translateOffset(y = titleOffset)
        screen.put(0, innerHeight + 1, line,
                   foreground = currentTheme.foreground, background = currentTheme.background)
        panel.drawDebug(currentTheme)
        if (title.isDefined) screen.translateOffset(y = -titleOffset)

      }
      screen.refresh()
    }
  }

  def draw(): Unit = {
    screen.hideCursor()
    if (title.isDefined) screen.translateOffset(y = titleOffset)
    // Draw panels recursively
    panel.redraw(currentTheme)
    if (title.isDefined) {
      screen.translateOffset(y = -titleOffset)
      drawTitle()
    }
  }

  private[Frame] def drawTitle(): Unit = {
    title.foreach { title =>
      screen.put(0, 0, Symbols.TLC_S_TO_D,
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(innerWidth + 1, 0, Symbols.TRC_D_TO_S,
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(1, 0, Symbols.DH * (innerWidth - 1),
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(0, 1, Symbols.SV,
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(innerWidth + 1, 1, Symbols.SV,
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(0, 2, Symbols.SV_TO_SR,
        foreground = currentTheme.foreground, background = currentTheme.background)
      screen.put(innerWidth + 1, 2, Symbols.SV_TO_SL,
        foreground = currentTheme.foreground, background = currentTheme.background)

      val spaceLeft = (innerWidth - 1 - title.length) / 2
      val spaceRight = innerWidth - 1 - spaceLeft - title.length
      screen.put(1, 1, " " * spaceLeft + title + " " * spaceRight,
        foreground = currentTheme.foreground, background = currentTheme.background)

    }
  }

}
