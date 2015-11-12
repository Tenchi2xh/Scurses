package net.team2xh.scurses.examples

import net.team2xh.scurses.{Scurses, Colors}

object HelloWorld extends App {
  Scurses { screen =>
    val (w, h) = screen.size
    val greeting = "Hello, world!"
    val prompt = "Press a key to continue..."
    screen.put(w/2 - greeting.length/2, h/2, greeting)
    screen.put(w/2 - prompt.length/2, h/2 + 1, prompt, Colors.BRIGHT_BLACK)
    screen.refresh()
    screen.keypress()
  }
}
