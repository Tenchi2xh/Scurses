package net.team2xh.scurses.examples

import net.team2xh.scurses.Scurses

object StressTest extends App {
  Scurses { screen =>

    var running = true
    val (w, h) = screen.size

    val chars = ('#' to 'z').toArray

    val thread = new Thread() {
      override def run(): Unit = {
        var i = 0
        var j = 0
        while (running) {
          val start = System.currentTimeMillis
          for (x <- 0 until w; y <- 0 until h) {
            if (x > 11 || y > 2) {
              val c = (x + y + j) % 256
              screen.put(x, y, s"${chars((x + y + i) % chars.length)}", c, (c+128) % 256)
            }
          }
          i = (i+1) % chars.length
          j = (j+1) % 256
          screen.refresh()
          val fps = 1000.0 / (System.currentTimeMillis - start)
          val _fps = "%2.2fFPS".format(fps)
          screen.put(0, 1, "           ")
          screen.put(2, 1, "%s%s".format(if (_fps.length < 8) " " * (8 - _fps.length) else "", _fps))
        }
      }
    }
    thread.start()
    screen.keypress()
    running = false
    thread.join()
  }
}
