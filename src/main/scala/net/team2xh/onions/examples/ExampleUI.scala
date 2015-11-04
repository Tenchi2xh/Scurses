package net.team2xh.onions.examples

import net.team2xh.onions.components.Frame
import net.team2xh.scurses.Scurses

object ExampleUI extends App {
  Scurses { implicit screen =>
    val frame = Frame("Example UI")

    val p1 = frame.panel
    val p2 = p1.splitRight
    val p3 = p2.splitRight

    val p22 = p2.splitDown
    val p23 = p22.splitDown

    val p232 = p23.splitRight

    val p32 = p3.splitDown

    frame.show()
  }
}
