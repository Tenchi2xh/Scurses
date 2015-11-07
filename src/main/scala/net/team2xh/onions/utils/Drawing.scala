package net.team2xh.onions.utils

import net.team2xh.onions.Symbols
import net.team2xh.scurses.Scurses

object Drawing {

  def drawGrid(x0: Int, y0: Int, w: Int, h: Int, gridWidth: Int,
               fg: Int, bg: Int)(implicit screen: Scurses): Unit = {
    // Corners
    screen.put(x0, y0, Symbols.TLC_S_TO_S, fg, bg)
    screen.put(x0 + w, y0, Symbols.TRC_S_TO_S, fg, bg)
    screen.put(x0 + w, y0 + h, Symbols.BRC_S_TO_S, fg, bg)
    screen.put(x0, y0 + h, Symbols.BLC_S_TO_S, fg, bg)
    // Edges
    for (x <- x0 + 1 until x0 + w) {
      val symbol = if (x % gridWidth == 0) Symbols.SH_TO_SD else Symbols.SH
      val symbol2 = if (x % gridWidth == 0) Symbols.SH_TO_SU else Symbols.SH
      screen.put(x, y0, symbol, fg, bg)
      screen.put(x, y0 + h, symbol2, fg, bg)
    }
    for (y <- y0 + 1 until y0 + h; x <- (x0 to x0 + w by gridWidth) :+ (x0 + w)) {
      screen.put(x, y, Symbols.SV, fg, bg)
    }
  }
}
