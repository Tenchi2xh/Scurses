package net.team2xh.onions.components

import com.sun.org.apache.xpath.internal.functions.FuncFalse
import net.team2xh.onions.Component
import net.team2xh.onions.drawing.Symbols
import net.team2xh.scurses.Scurses

import scala.collection.mutable.ListBuffer


case class FramePanel(parent: Frame, var width: Int, var height: Int)
                     (implicit screen: Scurses) extends Component(Some(parent)) {

  var top:    Option[FramePanel] = None
  var bottom: Option[FramePanel] = None
  var left:   Option[FramePanel] = None
  var right:  Option[FramePanel] = None

  val widgets = ListBuffer[Widget]()

  def innerWidth = width
  def innerHeight = height

  def position: (Int, Int) = {
    (x0, y0)
  }
  def x0: Int = left match {
    case None =>
      top match {
        case None => 0
        case Some(panel) => panel.x0
      }
    case Some(panel) => panel.x0 + panel.width
  }
  def y0: Int = top match {
    case None =>
      left match {
        case None => 0
        case Some(panel) => panel.y0
      }
    case Some(panel) => panel.y0 + panel.height
  }

  // TODO: Call this on resize
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

  private[FramePanel] def totalHorizontal: (Int, Int) = {
    val l = totalLeft
    val r = totalRight
    (l._1 + width + r._1, l._2 + 1 + r._2)
  }
  private[FramePanel] def totalLeft: (Int, Int) = left match {
    case None => (0, 0)
    case Some(panel) =>
      val l = panel.totalLeft
      (l._1 + panel.width, l._2 + 1)
  }
  private[FramePanel] def totalRight: (Int, Int) = right match {
    case None => (0, 0)
    case Some(panel) =>
      val r = panel.totalRight
      (panel.width + r._1, 1 + r._2)
  }
  private[FramePanel] def totalVertical: (Int, Int) = {
    val t = totalTop
    val b = totalBottom
    (t._1 + height + b._1, t._2 + 1 + b._2)
  }
  private[FramePanel] def totalTop: (Int, Int) = top match {
    case None => (0, 0)
    case Some(panel) =>
      val t = panel.totalTop
      (t._1 + panel.height, t._2 + 1)
  }
  private[FramePanel] def totalBottom: (Int, Int) = bottom match {
    case None => (0, 0)
    case Some(panel) =>
      val b = panel.totalBottom
      (panel.height + b._1, 1 + b._2)
  }
  private[FramePanel] def resizeHorizontal(columnWidth: Int): Unit = {
    resizeLeft(columnWidth)
    resizeRight(columnWidth)
  }
  private[FramePanel] def resizeLeft(columnWidth: Int): Unit = left foreach {
    panel =>
      panel.width = columnWidth
      panel.resizeLeft(columnWidth)
  }
  private[FramePanel] def resizeRight(columnWidth: Int): Unit = right foreach {
    panel =>
      panel.width = columnWidth
      panel.resizeRight(columnWidth)
  }
  private[FramePanel] def resizeVertical(rowHeight: Int): Unit = {
    resizeTop(rowHeight)
    resizeBottom(rowHeight)
  }
  private[FramePanel] def resizeTop(rowHeight: Int): Unit = top foreach {
    panel =>
      panel.height = rowHeight
      panel.resizeTop(rowHeight)
  }
  private[FramePanel] def resizeBottom(rowHeight: Int): Unit = bottom foreach {
    panel =>
      panel.height = rowHeight
      panel.resizeBottom(rowHeight)
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

    val newRight = FramePanel(parent, newColumnWidth, height)
    // Update neighbours
    newRight.right = right
    newRight.left = Some(this)
    right.foreach(panel => panel.left = Some(newRight))
    right = Some(newRight)

    newRight
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

    val newBottom = FramePanel(parent, width, newRowHeight)
    // Update neighbours
    newBottom.bottom = bottom
    newBottom.top = Some(this)
    bottom.foreach(panel => panel.top = Some(newBottom))
    bottom = Some(newBottom)

    newBottom
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
    bottom.foreach(_.drawEdges())
    right.foreach(_.drawEdges())

    val (x0, y0) = position

    // Vertical edges
    for (y <- y0 + 1 to y0 + height - 1) {
      screen.put(x0 + width, y, Symbols.SV)
      if (left.isEmpty)
        screen.put(x0, y, Symbols.SV)
    }
    // Horizontal edges
    for (x <- x0 + 1 until x0 + width) {
      screen.put(x, y0, Symbols.SH)
      if (bottom.isEmpty)
        screen.put(x, y0 + height, Symbols.SH)
    }
  }

  private[FramePanel] def drawCorners(): Unit = {
    bottom.foreach(_.drawCorners())
    right.foreach(_.drawCorners())

    val (x0, y0) = position

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
    screen.put(x0, y0, tlc)
    // Bottom-left corner
    // ┼ not supported
    val blc = if (hasAnyLeft)
      if (bottom.isEmpty)
        Symbols.SH_TO_SU
      else
        Symbols.SV_TO_SR
    else
      Symbols.BLC_S_TO_S
    screen.put(x0, y0 + height, blc)
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
    screen.put(x0 + width, y0 + height, brc)
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
    screen.put(x0 + width, y0, trc)

  }

  private[FramePanel] def drawDebug(): Unit = {
    bottom.foreach(_.drawDebug())
    right.foreach(_.drawDebug())

    val (x0, y0) = position
    screen.put(x0 + 2, y0 + 1, s"$width x $height")
    screen.put(x0 + 2, y0 + 2, "[%s] top".format(if (top.isDefined) "X" else " "))
    screen.put(x0 + 2, y0 + 3, "[%s] bottom".format(if (bottom.isDefined) "X" else " "))
    screen.put(x0 + 2, y0 + 4, "[%s] left".format(if (left.isDefined) "X" else " "))
    screen.put(x0 + 2, y0 + 5, "[%s] right".format(if (right.isDefined) "X" else " "))
  }

  override def redraw(): Unit = {
    drawEdges()
    drawCorners()
    drawDebug()
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
