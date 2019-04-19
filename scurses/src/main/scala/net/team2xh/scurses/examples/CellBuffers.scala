package net.team2xh.scurses.examples

import net.team2xh.scurses._

object CellBuffers extends App {
  Scurses { implicit screen =>

    val buffer = CellBuffer(10, 10)
    buffer(0, 0) = Cell('a', Style())
    buffer(1, 1) = Cell('b', Style(underlined = true, foreground = 2))
    buffer(2, 2) = Cell('c', Style(bold = true, foreground = 3))
    buffer(3, 3) = Cell('d', Style(blinking = true, foreground = 4))

    buffer.draw(2, 2)

    screen.refresh()
    screen.keypress()
  }
}
