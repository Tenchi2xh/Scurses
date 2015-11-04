package net.team2xh.onions.drawing

import net.team2xh.scurses.Scurses

object Box {

  def drawEndVertical(x0: Int, y0: Int, height: Int, rows: Int, position: String)
              (implicit screen: Scurses): Unit = {
    val tc = if (position == "left") Symbols.TLC_S_TO_S else Symbols.TRC_S_TO_S
    val bc = if (position == "left") Symbols.BLC_S_TO_S else Symbols.BRC_S_TO_S
    screen.put(x0, y0, tc)
    screen.put(x0, y0 + height - 1, bc)

    val m = if (position == "left") Symbols.SV_TO_SR else Symbols.SV_TO_SL
    val spacing = (height - 2 - (rows - 1)) / rows  // Available whitespace between horizontals
    val horizontals = for (y <- 0 until rows) yield y0 + (spacing + 1) * y
    for (y <- y0 + 1 until y0 + height - 1) {
      if (horizontals.contains(y))
        screen.put(x0, y, m)
      else
        screen.put(x0, y, Symbols.SV)
    }
  }

  def drawMiddleVertical(x0: Int, y0: Int, height: Int, rowsLeft: Int, rowsRight: Int)
              (implicit screen: Scurses): Unit = {
    screen.put(x0, y0, Symbols.SH_TO_SD)
    screen.put(x0, y0 + height - 1, Symbols.SH_TO_SU)

    val spacingLeft = (height - 2 - (rowsLeft - 1)) / rowsLeft
    val horizontalsLeft = for (y <- 0 until rowsLeft) yield y0 + (spacingLeft + 1) * y
    val spacingRight = (height - 2 - (rowsRight - 1)) / rowsRight
    val horizontalsRight = for (y <- 0 until rowsRight) yield y0 + (spacingRight + 1) * y
    for (y <- y0 + 1 until y0 + height - 1) {
      if (horizontalsLeft.contains(y))
        if (horizontalsRight.contains(y))
          screen.put(x0, y, Symbols.SH_X_SV)
        else
          screen.put(x0, y, Symbols.SV_TO_SL)
      else if (horizontalsRight.contains(y))
        screen.put(x0, y, Symbols.SV_TO_SR)
      else
        screen.put(x0, y, Symbols.SV)
    }
  }

  def drawHorizontals(x0: Int, y0: Int, width: Int, height: Int, rows: Int)
                     (implicit screen: Scurses): Unit = {
    val spacing = (height - 2 - (rows - 1)) / rows
    val rest = (height - 2 - (rows - 1)) - (rows * spacing)
    for (r <- 0 to rows) {
      screen.put(x0, y0 + r * (spacing + 1) + (if (r == rows) rest else 0), Symbols.SH * width)
    }
  }

  def drawColumns(x0: Int, y0: Int, width: Int, height: Int,
                  columns: Int, rows: Seq[Int])
                (implicit screen: Scurses): Unit = {

    assert(rows.length == columns)

    drawEndVertical(x0, y0, height, rows.head, position = "left")
    drawEndVertical(x0 + width - 1, y0, height, rows.last, position = "right")
    val length = (width - 2 - (columns - 1)) / columns
    val rest = (width - 2 - (columns - 1)) - (columns * length)
    for (c <- 0 until columns) {
      drawHorizontals(x0 + 1 + c * (length + 1), y0,
                      length + (if (c == columns - 1) rest else 0), height, rows(c))
    }
    for (c <- 1 to columns - 1) {
      drawMiddleVertical(x0 + c * (length + 1), y0, height, rows(c-1), rows(c))
    }
  }
}
