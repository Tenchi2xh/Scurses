# Scurses / Onions

[![Build Status](https://img.shields.io/github/workflow/status/Tenchi2xh/Scurses/Continuous%20Integration.svg)](https://github.com/Tenchi2xh/Scurses/actions)
<img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.team2xh/scurses_2.12.svg?label=scurses%40maven-central"> <img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.team2xh/onions_2.12.svg?label=onions%40maven-central">

[Scurses](#scurses) and [Onions](#onions) are frameworks for drawing nice things in your terminal using simple, elegant Scala. [Scurses](#scurses) provides a low-level drawing and event handling API while [Onions](#onions) provides a high-level UI API with useful widgets.

**Contents**:

<!-- MarkdownTOC autolink="true" -->

- [Onions](#onions)
  - [Example application](#example-application)
  - [How to use](#how-to-use)
  - [Keyboard controls](#keyboard-controls)
- [Scurses](#scurses)
  - [Example applications](#example-applications)
  - [How to use](#how-to-use-1)

<!-- /MarkdownTOC -->

---

# Onions

**High-level Scurses framework for easy terminal UI**

<img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.team2xh/onions_2.12.svg?label=onions%40maven-central">

Dependency:

```scala
libraryDependencies += "net.team2xh" %% "onions" % "<version in the badge>"
```

<img width="1354" alt="screen shot 2015-11-12 at 10 58 51" src="https://cloud.githubusercontent.com/assets/4116708/11115697/b6168e20-892d-11e5-9eff-c1277db6256b.png">
<img width="1354" alt="screen shot 2015-11-12 at 10 59 11" src="https://cloud.githubusercontent.com/assets/4116708/11115699/b630847e-892d-11e5-8ef8-fb486f4e09c4.png">
<img width="1354" alt="screen shot 2015-11-12 at 10 59 40" src="https://cloud.githubusercontent.com/assets/4116708/11115700/b633fc58-892d-11e5-9a38-84772ab5141c.png">

## Example application

To see an example application showcasing all the widgets, run:

```R
$ sbt onions/run
```

Make sure that your terminal is sized big enough, scrolling is not supported yet.

Goal is to provide an API full of widgets to make it really easy for users to quickly set up a dashboard to monitor their services in the terminal (graphs, histograms, logs, etc.). It works great through SSH as well!

## How to use

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

For examples regarding all widgets, see `ExampleUI.scala`

## Keyboard controls

Keys | Action
:---:|:------
<kbd>↑</kbd> / <kbd>↓</kbd> | Focus next widget in direction / Focus next panel in direction
<kbd>←</kbd> / <kbd>→</kbd> | Focus next panel in direction
<kbd>⇥</kbd> / <kbd>⇧</kbd>+<kbd>⇥</kbd> | Focus next / previous panel
<kbd>CTRL</kbd>+<kbd>SPACE</kbd> | Switch to next tab (if panel has multiple tabs)
<kbd>SPACE</kbd> / <kbd>↵</kbd> | Activate label action, check radio or checkbox
<kbd>&lt;</kbd> / <kbd>&gt;</kbd> | Move slider left / right (also with <kbd>SPACE</kbd> / <kbd>↵</kbd>)
<kbd>ESC</kbd> / <kbd>CTRL</kbd>+<kbd>C</kbd> | Exit

---

# Scurses

**Low-level terminal drawing API for Scala**

<img alt="Maven Central" src="https://img.shields.io/maven-central/v/net.team2xh/scurses_2.12.svg?label=scurses%40maven-central">

Dependency:

```scala
libraryDependencies += "net.team2xh" %% "scurses" % "<version in the badge>"
```

<img width="1354" alt="screen shot 2015-11-12 at 11 07 32" src="https://cloud.githubusercontent.com/assets/4116708/11115698/b62abd28-892d-11e5-9be7-9286674e4add.png">

## Example applications

- Hello World:

```R
$ sbt "scurses/runMain net.team2xh.scurses.examples.HelloWorld"
```
  
- Game of life:

```R
$ sbt scurses/run
```
  
- Stress test:

```R
$ sbt "scurses/runMain net.team2xh.scurses.examples.StressTest"
```

## How to use

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
