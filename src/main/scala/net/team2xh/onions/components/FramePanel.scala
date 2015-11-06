package net.team2xh.onions.components

import net.team2xh.onions.{Symbols, Component}
import net.team2xh.scurses.Scurses

import scala.collection.mutable.ListBuffer

object FramePanel {
  def expandRight(parent: FramePanel, width: Int, height: Int)
                 (implicit screen: Scurses): FramePanel = {
    val newRight = new FramePanel(parent, width, height)
    newRight.right = parent.right
    newRight.left = Some(parent)
    parent.right.foreach(panel => panel.left = Some(newRight))
    parent.right = Some(newRight)
    newRight
  }

  def expandDown(parent: FramePanel, width: Int, height: Int)
                (implicit screen: Scurses): FramePanel = {
    val newBottom = new FramePanel(parent, width, height)
    newBottom.bottom = parent.bottom
    newBottom.top = Some(parent)
    parent.bottom.foreach(panel => panel.top = Some(newBottom))
    parent.bottom = Some(newBottom)
    newBottom
  }
}

case class FramePanel(parent: Component, var width: Int, var height: Int)
                     (implicit screen: Scurses) extends Component(Some(parent)) {

  var top:    Option[FramePanel] = None
  var bottom: Option[FramePanel] = None
  var left:   Option[FramePanel] = None
  var right:  Option[FramePanel] = None

  var focus = false
  var widgetFocus = 0

  val widgets = ListBuffer[Widget]()

  def innerWidth = width
  def innerHeight = height

  // TODO: Call this on resize
  // TODO: Go deeper in the tree
  private[components] def updateDimensions(newWidth: Int, newHeight: Int): Unit = {
    val (_, nHorizontal) = totalHorizontal
    val newColumnWidth = newWidth / nHorizontal
    width = newWidth - (nHorizontal - 1) * newColumnWidth
    resizeHorizontal(newColumnWidth)

    val (_, nVertical) = totalVertical
    val newRowHeight = newHeight / nVertical
    height = height - (nVertical - 1) * newRowHeight
    resizeVertical(newRowHeight)
  }

  def getTreeWalk: Seq[FramePanel] = {
    val b = bottom match {
      case None => Seq()
      case Some(panel) => panel.getTreeWalk
    }
    val r = right match {
      case None => Seq()
      case Some(panel) => panel.getTreeWalk
    }
    this +: (b ++ r)
  }

  def getFocusedWidget: Option[Widget] = {
    if (widgetFocus < widgets.length)
      Some(widgets(widgetFocus))
    else
      None
  }

  def focusPreviousWidget: Boolean = {
    val l = widgets.length
    if (widgetFocus == 0)
      false
    else {
      widgetFocus -= 1
      if (widgets(widgetFocus).isFocusable)
        true
      else
        focusPreviousWidget
    }
  }

  def focusNextWidget: Boolean = {
    val l = widgets.length
    if (widgetFocus == l - 1)
      false
    else {
      widgetFocus += 1
      if (widgets(widgetFocus).isFocusable)
        true
      else
        focusNextWidget
    }
  }

  def getNextDirection(direction: (FramePanel) => Option[FramePanel],
                   fallback: (FramePanel) => Option[FramePanel]): Option[FramePanel] = {
    direction(this) match {
      case Some(panel) => Some(panel)
      case None =>
        fallback(this) match {
          case Some(panel) => panel.getNextDirection(direction, fallback)
          case None => None
        }
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

  private[FramePanel] def total(direction: (FramePanel) => Option[FramePanel],
                                attribute: (FramePanel) => Int): (Int, Int) = {
    direction(this) match {
      case None => (0, 0)
      case Some(panel) =>
        val l = panel.total(direction, attribute)
        (l._1 + attribute(panel), l._2 + 1)
    }
  }

  private[FramePanel] def resizeHorizontal(columnWidth: Int): Unit = {
    resize(_.left, _.width = columnWidth)
    resize(_.right, _.width = columnWidth)
  }

  private[FramePanel] def resizeVertical(rowHeight: Int): Unit = {
    resize(_.top, _.height = rowHeight)
    resize(_.bottom, _.height = rowHeight)
  }

  private[FramePanel] def resize(direction: (FramePanel) => Option[FramePanel],
                                 setter: (FramePanel) => Unit): Unit = {
    direction(this) foreach {
      panel =>
        setter(panel)
        panel.resize(direction, setter)
    }
  }

  /**
   * Splits this panel vertically.
   * @return Right panel resulting from the split
   */
  def splitRight: FramePanel = {
    val (totalWidth, nHorizontal) = totalHorizontal
    val newColumnWidth = totalWidth / (nHorizontal + 1)
    width = totalWidth - nHorizontal * newColumnWidth
    resizeHorizontal(newColumnWidth)

    FramePanel.expandRight(this, newColumnWidth, height)
  }

  /**
   * Splits this panel horizontally.
   * @return Bottom panel resulting from the slice
   */
  def splitDown: FramePanel = {
    val (totalHeight, nVertical) = totalVertical
    val newRowHeight = totalHeight / (nVertical + 1)
    height = totalHeight - nVertical * newRowHeight
    resizeVertical(newRowHeight)

    FramePanel.expandDown(this, width, newRowHeight)
  }

  private[FramePanel] def hasAnyLeft: Boolean = left match {
    case None =>
      top match {
        case None => false
        case Some(panel) => panel.hasAnyLeft
      }
    case Some(panel) => true
  }

  private[FramePanel] def hasAnyRight: Boolean = right match {
    case None =>
      top match {
        case None => false
        case Some(panel) => panel.hasAnyRight
      }
    case Some(panel) => true
  }

  private[FramePanel] def drawEdges(): Unit = {
    screen.translateOffset(y = height)
    bottom.foreach(_.drawEdges())
    screen.translateOffset(x = width, y = -height)
    right.foreach(_.drawEdges())
    screen.translateOffset(x = -width)

    // Vertical edges
    for (y <- 1 to height - 1) {
      screen.put(width, y, Symbols.SV)
      if (left.isEmpty)
        screen.put(0, y, Symbols.SV)
    }
    // Horizontal edges
    screen.put(1, 0, Symbols.SH * (width - 1))
    if (bottom.isEmpty)
      screen.put(1, height, Symbols.SH * (width - 1))
  }

  private[FramePanel] def drawCorners(): Unit = {
    screen.translateOffset(y = height)
    bottom.foreach(_.drawCorners())
    screen.translateOffset(x = width, y = -height)
    right.foreach(_.drawCorners())
    screen.translateOffset(x = -width)

    // Top-left corner
    val tlc = if (left.isEmpty)
      if (top.isEmpty)
        Symbols.TLC_S_TO_S
      else
        Symbols.SV_TO_SR
    else if (top.isEmpty)
      Symbols.SH_TO_SD
    else
      Symbols.SH_X_SV
    screen.put(0, 0, tlc)
    // Bottom-left corner
    // ┼ not supported
    val blc = if (hasAnyLeft)
      if (bottom.isEmpty)
        Symbols.SH_TO_SU
      else
        Symbols.SV_TO_SR
    else
      Symbols.BLC_S_TO_S
    screen.put(0, height, blc)
    // Bottom-right corner
    val brc = if (hasAnyRight)
      if (bottom.isEmpty)
        Symbols.SH_TO_SU
      else
        Symbols.SV_TO_SL
    else if (bottom.isEmpty)
      Symbols.BRC_S_TO_S
    else
      Symbols.SV_TO_SL
    screen.put(width, height, brc)
    // Top-right corner
    val trc = if (top.isEmpty)
      if (right.isEmpty)
        Symbols.TRC_S_TO_S
      else
        Symbols.SH_TO_SD
    else if (right.isEmpty)
      Symbols.SV_TO_SL
    else
      Symbols.SH_TO_SD
    screen.put(width, 0, trc)

  }

  private[FramePanel] def drawDebug(): Unit = {
    screen.translateOffset(y = height)
    bottom.foreach(_.drawDebug())
    screen.translateOffset(x = width, y = -height)
    right.foreach(_.drawDebug())
    screen.translateOffset(x = -width)

    screen.put(2, 1, s"$width x $height")
    screen.put(2, 2, "[%s] top".format(if (top.isDefined) "X" else " "))
    screen.put(2, 3, "[%s] bottom".format(if (bottom.isDefined) "X" else " "))
    screen.put(2, 4, "[%s] left".format(if (left.isDefined) "X" else " "))
    screen.put(2, 5, "[%s] right".format(if (right.isDefined) "X" else " "))
  }

  override def redraw(): Unit = {
    drawEdges()
    drawCorners()
//    drawDebug()
    drawWidgets()
  }

  def drawWidgets(): Unit = {
    screen.translateOffset(y = height)
    bottom.foreach(_.drawWidgets())
    screen.translateOffset(x = width, y = -height)
    right.foreach(_.drawWidgets())
    screen.translateOffset(x = -width)

    var y = 1
    for ((widget, i) <- widgets.zipWithIndex) {
      screen.translateOffset(x = 2, y = y)
      widget.draw(focus && widgetFocus == i)
      screen.translateOffset(x = -2, y = -y)
      y += widget.innerHeight
    }
  }

  private[components] def horizontalDepth: Int = right match {
    case None => 1
    case Some(panel) => 1 + panel.horizontalDepth
  }

  private[components] def verticalDepth: Int = bottom match {
    case None => 1
    case Some(panel) => 1 + panel.verticalDepth
  }

  private[components] def maxVerticalDepth: Int = right match {
    case None => verticalDepth
    case Some(panel) => math.max(verticalDepth, panel.maxVerticalDepth)
  }

  private[components] def verticalDepths: Seq[Int] = right match {
    case None => Seq(verticalDepth)
    case Some(panel) => Seq(verticalDepth) ++ panel.verticalDepths
  }

}
