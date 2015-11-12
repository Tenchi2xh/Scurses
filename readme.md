# Scurses

Terminal drawing API for Scala

---

### Examples

- Hello World:

```R
$ sbt "scurses/run-main net.team2xh.scurses.examples.HelloWorld"
```
  
- Game of life:

```R
$ sbt scurses/run
```
  
- Stress test:

```R
$ sbt "scurses/run-main net.team2xh.scurses.examples.StressTest"
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
$ sbt onions/run
```

Goal is to provide an API full of widgets to make it really easy for users to quickly set up a dashboard to monitor their services in the terminal (graphs, histograms, logs, etc.)

### How to use

```scala
Scurses { implicit screen =>
  val frame = Frame("Example UI")
  // Three columns
  val colA = frame.panel
  val colB = colA.splitRight
  val colC = colB.splitRight
  // Split second column in three rows
  val colB2 = colB.splitDown
  val colB3 = colB2.splitDown
  // Split third row of second column into two columns
  val colB3B = colB3.splitRight
  val colB3B2 = colB3B.splitDown
  // Split last column into two rows
  val colC2 = colC.splitDown

  // Add a label in the first column
  Label(colA, Lorem.Ipsum, TextWrap.JUSTIFY)
  
  val r = Random
  val points = (1 to 50) map (i => {
    val x = r.nextInt(40)
    val y = 50 - x + (r.nextGaussian() * 5).toInt - 2
    (x, y max 0)
  })
  
  // Add a scatter plot in the first row of third column
  ScatterPlot(colC, points, "Time", "Sales")
  
  // Show a heat map of the same values in the second row
  HeatMap(colC2, points, "Time", "Sales")

  // Display and launch event loop
  frame.show()
}
```

For examples about all widgets, see `ExampleUI.scala`

### Keyboard controls

Keys | Action
:---:|:------
<kbd>↑</kbd> / <kbd>↓</kbd> | Focus next widget in direction / Focus next panel in direction
<kbd>←</kbd> / <kbd>→</kbd> | Focus next panel in direction
<kbd>⇥</kbd> / <kbd>⇧</kbd>+<kbd>⇥</kbd> | Focus next / previous panel
<kbd>CTRL</kbd>+<kbd>SPACE</kbd> | Switch to next tab (if panel has multiple tabs)
<kbd>SPACE</kbd> / <kbd>↵</kbd> | Activate label action, check radio or checkbox
<kbd>&lt;</kbd> / <kbd>&gt;</kbd> | Move slider left / right (also with <kbd>SPACE</kbd> / <kbd>↵</kbd>)
<kbd>ESC</kbd> / <kbd>CTRL</kbd>+<kbd>C</kbd> | Exit

