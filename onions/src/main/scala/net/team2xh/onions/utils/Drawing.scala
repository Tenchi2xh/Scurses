package net.team2xh.onions.utils

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.scurses.Scurses

object Drawing {

  def drawAxisLabels(x0: Int,
                     graphWidth: Int,
                     graphHeight: Int,
                     labelX: String = "",
                     labelY: String = "",
                     theme: ColorScheme
  )(implicit screen: Scurses): Unit = {

    if (labelX != "") {
      val lX =
        if (labelX.length > graphWidth) labelX.substring(0, graphWidth - 3) + "..."
        else labelX
      screen.put(x0 + (graphWidth - lX.length) / 2, graphHeight + 2, lX, theme.accent3, theme.background)
    }
    if (labelY != "") {
      val lY =
        if (labelY.length > graphHeight) labelY.substring(0, graphHeight - 3) + "..."
        else labelY
      val y0 = (graphHeight - lY.length) / 2
      for ((char, y) <- lY.zipWithIndex)
        screen.put(0, y0 + y, "" + char, theme.accent3, theme.background)
    }
  }

  def drawAxisValues(x0: Int,
                     y0: Int,
                     length: Int,
                     gridSize: Int,
                     valueMin: Int,
                     valueMax: Int,
                     fg: Int,
                     bg: Int,
                     horizontal: Boolean = true
  )(implicit screen: Scurses): Unit = {

    val step      = if (horizontal) gridSize else gridSize / 2
    val start     = (n: Int) => if (horizontal) n else length - n
    val span      = valueMax - valueMin
    val lastIndex = if (horizontal) Seq() else Seq(length)
    for (i <- (0 until length by step) ++ lastIndex) {
      val index = math.floor((start(i) * span.toDouble) / length).toInt + valueMin
      val x1    = if (horizontal) x0 + i else x0
      val y1    = if (horizontal) y0 else y0 + i
      screen.put(x1, y1, index.toString, fg, bg)
    }
  }

  def drawGrid(x0: Int,
               y0: Int,
               w: Int,
               h: Int,
               gridWidth: Int,
               fg: Int,
               bg: Int,
               showVertical: Boolean = true,
               showHorizontal: Boolean = true,
               gridOffsetX: Int = 0,
               gridOffsetY: Int = 0
  )(implicit screen: Scurses): Unit = {

    val gridHeight          = gridWidth / 2
    val horizontalPositions = (x0 + (gridOffsetX + gridWidth)  % gridWidth until x0 + w by gridWidth).filter(_ != x0)
    val verticalPositions   = (y0 + (gridOffsetY + gridHeight) % gridHeight until y0 + h by gridHeight).filter(_ != y0)
    // Corners
    screen.put(x0, y0, Symbols.TLC_S_TO_S, fg, bg)
    screen.put(x0 + w, y0, Symbols.TRC_S_TO_S, fg, bg)
    screen.put(x0 + w, y0 + h, Symbols.BRC_S_TO_S, fg, bg)
    screen.put(x0, y0 + h, Symbols.BLC_S_TO_S, fg, bg)
    // Edges
    for (x <- x0 + 1 until x0 + w) {
      val symbol  = if (showVertical && horizontalPositions.contains(x)) Symbols.SH_TO_SD else Symbols.SH
      val symbol2 = if (showVertical && horizontalPositions.contains(x)) Symbols.SH_TO_SU else Symbols.SH
      screen.put(x, y0, symbol, fg, bg)
      screen.put(x, y0 + h, symbol2, fg, bg)
    }
    for (y <- y0 + 1 until y0 + h) {
      val symbol  = if (showHorizontal && verticalPositions.contains(y)) Symbols.SV_TO_SR else Symbols.SV
      val symbol2 = if (showHorizontal && verticalPositions.contains(y)) Symbols.SV_TO_SL else Symbols.SV
      screen.put(x0, y, symbol, fg, bg)
      screen.put(x0 + w, y, symbol2, fg, bg)
    }
    if (showVertical)
      for (y <- y0 + 1 until y0 + h; x <- horizontalPositions)
        screen.put(x, y, Symbols.SV, fg, bg)
    if (showHorizontal)
      for (x <- x0 + 1 until x0 + w; y <- verticalPositions)
        screen.put(x, y, Symbols.SH, fg, bg)
    if (showHorizontal && showVertical)
      for (
        x <- horizontalPositions;
        y <- verticalPositions
      )
        screen.put(x, y, Symbols.SH_X_SV, fg, bg)
  }

  def clipText(text: String, limit: Int, before: Boolean = false) =
    if (text.length > limit) {
      val clipped = text.substring(if (before) text.length - limit + 3 else 0, if (before) text.length else limit - 3)
      if (before)
        "..." + clipped
      else
        clipped + "..."
    } else text

}
