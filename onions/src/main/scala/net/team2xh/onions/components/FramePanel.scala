package net.team2xh.onions.components

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.{Component, Symbols}
import net.team2xh.scurses.RichText.RichTextHelper
import net.team2xh.scurses.Scurses

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object FramePanel {
  def expandRight(parent: FramePanel)(implicit screen: Scurses): FramePanel = {
    val newRight = new FramePanel(parent)
    newRight.right = parent.right
    newRight.left = Some(parent)
    parent.right.foreach(panel => panel.left = Some(newRight))
    parent.right = Some(newRight)
    newRight
  }

  def expandDown(parent: FramePanel)(implicit screen: Scurses): FramePanel = {
    val newBottom = new FramePanel(parent)
    newBottom.bottom = parent.bottom
    newBottom.top = Some(parent)
    parent.bottom.foreach(panel => panel.top = Some(newBottom))
    parent.bottom = Some(newBottom)
    newBottom
  }

  var idCounter = 0
}

case class FramePanel(parent: Component)(implicit screen: Scurses) extends Component(Some(parent)) {

  var width  = 0
  var height = 0

  var title = ""

  var top: Option[FramePanel]    = None
  var bottom: Option[FramePanel] = None
  var left: Option[FramePanel]   = None
  var right: Option[FramePanel]  = None

  var focus = false

  var redrawBorders = true

  var currentTab = 0
  var tabs =
    mutable.ArrayDeque[(ListBuffer[Widget], Int, ListBuffer[Int])]((ListBuffer[Widget](), 0, ListBuffer[Int]()))

  def widgets     = tabs(currentTab)._1
  def widgetFocus = tabs(currentTab)._2
  def heights     = tabs(currentTab)._3

  val id = FramePanel.idCounter
  FramePanel.idCounter += 1

  def innerWidth  = width
  def innerHeight = height

  def addWidget(widget: Widget): Unit = {
    widgets += widget
    tabs(currentTab) = (widgets, widgetFocus, heights :+ 0)
  }

  def addTab(): Unit = {
    tabs += ((ListBuffer[Widget](), 0, ListBuffer[Int]()))
    currentTab = tabs.length - 1
    updateTab()
  }

  def nextTab(): Unit = {
    currentTab = (currentTab + 1) % tabs.length
    updateTab()
  }

  def previousTab(): Unit = {
    currentTab = (currentTab - 1 + tabs.length) % tabs.length
    updateTab()
  }

  def showTab(n: Int): Unit = {
    currentTab = (n + tabs.length) % tabs.length
    updateTab()
  }

  var needsClear = true

  def updateTab(): Unit = {
    widgets.foreach(_.needsRedraw = true)
    needsClear = true
  }

  private[components] def updateDimensions(newWidth: Int, newHeight: Int): Unit = {
    val (_, nHorizontal) = totalHorizontal
    val newColumnWidth   = newWidth / nHorizontal
    right match {
      case None => width = newWidth - (nHorizontal - 1) * newColumnWidth
      case _    => width = newColumnWidth
    }

    val (_, nVertical) = totalVertical
    val newRowHeight   = newHeight / nVertical
    bottom match {
      case None => height = newHeight - (nVertical - 1) * newRowHeight
      case _    => height = newRowHeight
    }

    bottom.foreach(_.updateDimensions(if (left.isEmpty) newWidth else width, newHeight))
    right.foreach(_.updateDimensions(newWidth, if (top.isEmpty) newHeight else height))
  }

  def getTreeWalk: Seq[FramePanel] = {
    val b = bottom match {
      case None        => Seq()
      case Some(panel) => panel.getTreeWalk
    }
    val r = right match {
      case None        => Seq()
      case Some(panel) => panel.getTreeWalk
    }
    this +: (b ++ r)
  }

  def getFocusedWidget: Option[Widget] =
    if (widgetFocus < widgets.length)
      Some(widgets(widgetFocus))
    else
      None

  def previousFocusableWidget: Option[Int] = {
    val l = widgets.length
    if (l == 0 || widgetFocus == 0)
      None
    else {
      for (i <- widgetFocus - 1 to 0 by -1)
        if (widgets(i).focusable)
          return Some(i)
      None
    }
  }

  def nextFocusableWidget: Option[Int] = {
    val l = widgets.length
    if (l == 0 || widgetFocus >= l - 1)
      None
    else {
      for (i <- widgetFocus + 1 until l)
        if (widgets(i).focusable)
          return Some(i)
      None
    }
  }

  def focusSomeWidget(which: Option[Int]): Boolean =
    which match {
      case None => false
      case Some(i) =>
        widgets(widgetFocus).needsRedraw = true
        widgets(i).needsRedraw = true
        tabs(currentTab) = (widgets, i, heights)
        true
    }

  def focusPreviousWidget = focusSomeWidget(previousFocusableWidget)

  def focusNextWidget = focusSomeWidget(nextFocusableWidget)

  def markAllForRedraw(): Unit =
    getTreeWalk.foreach { panel =>
      panel.redrawBorders = true
      panel.widgets.foreach(_.needsRedraw = true)
    }

  def getNextDirection(direction: (FramePanel) => Option[FramePanel],
                       fallback: (FramePanel) => Option[FramePanel]
  ): Option[FramePanel] =
    direction(this) match {
      case Some(panel) => Some(panel)
      case None =>
        fallback(this) match {
          case Some(panel) => panel.getNextDirection(direction, fallback)
          case None        => None
        }
    }

  private[FramePanel] def totalHorizontal: (Int, Int) = {
    val l = total(_.left, _.width)
    val r = total(_.right, _.width)
    (l._1 + width + r._1, l._2 + 1 + r._2)
  }

  private[FramePanel] def totalVertical: (Int, Int) = {
    val t = total(_.top, _.height)
    val b = total(_.bottom, _.height)
    (t._1 + height + b._1, t._2 + 1 + b._2)
  }

  private[FramePanel] def total(direction: FramePanel => Option[FramePanel], attribute: FramePanel => Int): (Int, Int) =
    direction(this) match {
      case None => (0, 0)
      case Some(panel) =>
        val l = panel.total(direction, attribute)
        (l._1 + attribute(panel), l._2 + 1)
    }

  private[FramePanel] def resizeHorizontal(columnWidth: Int): Unit = {
    resize(_.left, _.width = columnWidth)
    resize(_.right, _.width = columnWidth)
  }

  private[FramePanel] def resizeVertical(rowHeight: Int): Unit = {
    resize(_.top, _.height = rowHeight)
    resize(_.bottom, _.height = rowHeight)
  }

  private[FramePanel] def resize(direction: FramePanel => Option[FramePanel], setter: FramePanel => Unit): Unit =
    direction(this) foreach { panel =>
      setter(panel)
      panel.resize(direction, setter)
    }

  /** Splits this panel vertically.
    * @return Right panel resulting from the split
    */
  def splitRight: FramePanel =
    FramePanel.expandRight(this)

  /** Splits this panel horizontally.
    * @return Bottom panel resulting from the slice
    */
  def splitDown: FramePanel =
    FramePanel.expandDown(this)

  private[FramePanel] def hasAnyLeft: Boolean = left match {
    case None =>
      top match {
        case None        => false
        case Some(panel) => panel.hasAnyLeft
      }
    case Some(panel) => true
  }

  private[FramePanel] def hasAnyRight: Boolean = right match {
    case None =>
      top match {
        case None        => false
        case Some(panel) => panel.hasAnyRight
      }
    case Some(panel) => true
  }

  private[FramePanel] def drawEdges(theme: ColorScheme): Unit = {
    propagateDraw(_.drawEdges(theme))

    // Vertical edges
    for (y <- 1 to height - 1) {
      screen.put(width, y, Symbols.SV, foreground = theme.foreground, background = theme.background)
      if (left.isEmpty)
        screen.put(0, y, Symbols.SV, foreground = theme.foreground, background = theme.background)
    }
    // Horizontal edges
    screen.put(1, 0, Symbols.SH * (width - 1), foreground = theme.foreground, background = theme.background)
    if (bottom.isEmpty)
      screen.put(1, height, Symbols.SH * (width - 1), foreground = theme.foreground, background = theme.background)
  }

  private[FramePanel] def drawCorners(theme: ColorScheme): Unit = {
    propagateDraw(_.drawCorners(theme))

    // Top-left corner
    val tlc =
      if (left.isEmpty)
        if (top.isEmpty)
          Symbols.TLC_S_TO_S
        else
          Symbols.SV_TO_SR
      else if (top.isEmpty)
        Symbols.SH_TO_SD
      else
        Symbols.SH_X_SV
    // Bottom-left corner
    // ┼ not supported
    val blc =
      if (hasAnyLeft)
        if (bottom.isEmpty)
          Symbols.SH_TO_SU
        else
          Symbols.SV_TO_SR
      else
        Symbols.BLC_S_TO_S
    // Bottom-right corner
    val brc =
      if (hasAnyRight)
        if (bottom.isEmpty)
          Symbols.SH_TO_SU
        else
          Symbols.SV_TO_SL
      else if (bottom.isEmpty)
        Symbols.BRC_S_TO_S
      else
        Symbols.SV_TO_SL
    // Top-right corner
    val trc =
      if (top.isEmpty)
        if (right.isEmpty)
          Symbols.TRC_S_TO_S
        else
          Symbols.SH_TO_SD
      else if (right.isEmpty)
        Symbols.SV_TO_SL
      else
        Symbols.SH_TO_SD
    // Draw
    screen.put(0, 0, tlc, foreground = theme.foreground, background = theme.background)
    screen.put(0, height, blc, foreground = theme.foreground, background = theme.background)
    screen.put(width, height, brc, foreground = theme.foreground, background = theme.background)
    screen.put(width, 0, trc, foreground = theme.foreground, background = theme.background)

  }

  private[FramePanel] def drawTitles(theme: ColorScheme): Unit = {
    propagateDraw(_.drawTitles(theme))
    val ts = tabs.zipWithIndex
      .map { case (t, i) =>
        val s = if (i == currentTab) "[r]" else ""
        val e = if (i == currentTab) "[/r]" else ""
        s"$s#${i + 1}$e"
      }
      .mkString("|")

    val tabText =
      if (tabs.length == 1) ""
      else " " + ts

    if (title != "") {
      screen.putRichText(2, 0, r"[[$title$tabText]", theme.foreground, theme.background)
    } else if (tabText != "") {
      screen.putRichText(2, 0, r"[[$tabText]", theme.foreground, theme.background)
    }
  }

  private[components] def drawDebug(theme: ColorScheme): Unit = {
    propagateDraw(_.drawDebug(theme))

    // format: off
    val neighbours = "%s%s%s%s".format(if (top.isDefined)    "↑" else "",
                                       if (bottom.isDefined) "↓" else "",
                                       if (left.isDefined)   "←" else "",
                                       if (right.isDefined)  "→" else "")
    // format: on

    val line = s"(#$id|${width}x$height|$neighbours)"
    screen.put(width - 1 - line.length, 0, line, foreground = theme.accent2, background = theme.background)
  }

  private[FramePanel] def fillBoxes(theme: ColorScheme): Unit = {
    propagateDraw(_.fillBoxes(theme))

    if (needsClear) {
      for (y <- 1 to height - 1)
        screen.put(1, y, " " * (width - 1), background = theme.background)
      needsClear = false
    }
  }

  private[FramePanel] def drawFocusIndicator(theme: ColorScheme): Unit = {
    propagateDraw(_.drawFocusIndicator(theme))

    val indicator = theme.accent1
    screen.put(1,
               1,
               if (focus) Symbols.BLOCK + Symbols.BLOCK_UPPER else "  ",
               foreground = indicator,
               background = theme.background
    )
    screen.put(1,
               height - 1,
               if (focus) Symbols.BLOCK + Symbols.BLOCK_LOWER else "  ",
               foreground = indicator,
               background = theme.background
    )
    screen.put(width - 2,
               1,
               if (focus) Symbols.BLOCK_UPPER + Symbols.BLOCK else "  ",
               foreground = indicator,
               background = theme.background
    )
    screen.put(width - 2,
               height - 1,
               if (focus) Symbols.BLOCK_LOWER + Symbols.BLOCK else "  ",
               foreground = indicator,
               background = theme.background
    )

  }

  def redraw(theme: ColorScheme): Unit = {
    updateDimensions(parent.innerWidth, parent.innerHeight)

    fillBoxes(theme)
    drawFocusIndicator(theme)
    if (redrawBorders) {
      drawEdges(theme)
      drawCorners(theme)
      redrawBorders = false
    }
    drawTitles(theme)
    drawWidgets(theme)
  }

  def drawWidgets(theme: ColorScheme): Unit = {
    propagateDraw(_.drawWidgets(theme))

    screen.clip(width, height - 1)
    var y             = 2
    var heightChanged = false
    for ((widget, i) <- widgets.zipWithIndex) {
      if (widget.needsRedraw) {
        screen.translateOffset(x = 2, y = y)
        // clear widget's space first
        for (y <- 0 until widget.innerHeight)
          screen.put(0, y, " " * widget.innerWidth, background = theme.background)
        widget.draw(focus && widgetFocus == i, theme)
        screen.translateOffset(x = -2, y = -y)
      }
      val newHeight = widget.innerHeight
      y += newHeight
      // If the height changed, redraw the rest
      if (heights(i) != newHeight) {
        heightChanged = true
        for (j <- i + 1 until widgets.length)
          widgets(j).needsRedraw = true
      }
      heights(i) = newHeight
    }
    // Clear the rest of the panel if heights have changed
    for (y <- y to height - 1)
      screen.put(1, y, " " * (width - 1), background = theme.background)
    screen.unclip()
  }

  def propagateDraw(drawMethod: (FramePanel) => Unit): Unit = {
    screen.translateOffset(y = height)
    bottom.foreach(b => drawMethod(b))
    screen.translateOffset(x = width, y = -height)
    right.foreach(b => drawMethod(b))
    screen.translateOffset(x = -width)
  }

  private[components] def horizontalDepth: Int = right match {
    case None        => 1
    case Some(panel) => 1 + panel.horizontalDepth
  }

  private[components] def verticalDepth: Int = bottom match {
    case None        => 1
    case Some(panel) => 1 + panel.verticalDepth
  }

  private[components] def maxVerticalDepth: Int = right match {
    case None        => verticalDepth
    case Some(panel) => math.max(verticalDepth, panel.maxVerticalDepth)
  }

  private[components] def verticalDepths: Seq[Int] = right match {
    case None        => Seq(verticalDepth)
    case Some(panel) => Seq(verticalDepth) ++ panel.verticalDepths
  }

  override def toString: String = s"FramePanel #$id"
}
