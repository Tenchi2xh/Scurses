# Scurses

Terminal drawing API for Scala

---

### Examples

- Hello World:

  ```R
  $ sbt
  > run net.team2xh.scurses.examples.HelloWorld
  ```
- Game of life:

  ```R
  $ sbt
  > run net.teamx2h.scurses.examples.GameOfLife
  ```
- Stress test:

  ```R
  $ sbt
  > run net.teamx2h.scurses.examples.StressTest
  ```

### How to use

```scala
import net.team2xh.scurses.Scurses

Scurses { screen =>
  // The screen will only be in Scurses mode inside this block
  // Scurses will reset the terminal buffer and everything when outside
  
  // Get the current terminal size
  val (w, h) = screen.size
  
  val greeting = "Hello, world!"
  val prompt = "Press a key to continue..."
  // Put some strings in the middle of the screen
  screen.put(w/2 - greeting.length/2, h/2, greeting)
  screen.put(w/2 - prompt.length/2, h/2 + 1, prompt, Colors.BRIGHT_BLACK)
  // Flush the buffer
  screen.refresh()
  // Wait for an input without storing it
  screen.keypress()
}
```

---

# Onions

Scurses framework for easy terminal UI

---

### Examples

What's been done so far:

```R
$ sbt
> run net.teamx2h.onions.examples.ExampleUI
```

Goal is to provide an API full of widgets to make it really easy for users to quickly set up a dashboard to monitor their services in the terminal (graphs, histograms, logs, etc.)

Currently taking "raw" input from keyboard with JLine, need to find something more powerful to grab inputs like ESC and key combinations, and also detect terminal resize.

### How to use

```scala
Scurses { implicit screen =>
  val frame = Frame("Example UI")
  // Three columns
  val p1 = frame.panel
  val p2 = p1.splitRight
  val p3 = p2.splitRight
  // Split second column in three rows (p2 will point to first row of second column)
  val p22 = p2.splitDown
  val p23 = p22.splitDown
  // Split third row of second column into two columns (p23 will point to the first column of the two)
  val p232 = p23.splitRight
  // Split last column into two rows
  val p32 = p3.splitDown

  // Display and launch event loop
  frame.show()
}
```
