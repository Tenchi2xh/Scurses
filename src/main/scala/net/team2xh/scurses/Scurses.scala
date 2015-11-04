package net.team2xh.scurses

import java.io.BufferedOutputStream

import scala.tools.jline.console.ConsoleReader

object Scurses {

  /**
   * Provides an safe context for Scurses use.
   * The terminal screen is prepared, then reset when out of the block.
   *
   * Example usage:
   *
   * Scurses { screen =>
   *   screen.put(1, 1, "Hello, Scurses!", Colors.BRIGHT_CYAN)
   *   screen.put(1, 2, "Press any key to exit, Colors.BRIGHT_BLACK)
   *   screen.keypress()
   * }
   *
   * @param block Block of code to execute in the Scurses context
   */
  def apply(block: Scurses => Unit): Unit = {
    val scurses = new Scurses
    block(scurses)
    scurses.close()
  }

}

class Scurses {

  val out = new BufferedOutputStream(System.out, 1048576)
  val csi = new EscapeCodes(out)
  val console = new ConsoleReader()

  var offsetX = 0
  var offsetY = 0

  init()

  /**
   * Puts a string on the terminal screen with the desired colors.
   * @param x X coordinate
   * @param y Y coordinate
   * @param string String to output
   * @param foreground Foreground color code
   * @param background Background color code
   */
  def put(x: Int, y: Int, string: String,
          foreground: Int = Colors.BRIGHT_WHITE,
          background: Int = Colors.DIM_BLACK) {
    csi.move(x + offsetX, y + offsetY)
    csi.setForeground(foreground)
    csi.setBackground(background)
    out.write(string.getBytes)
    csi.resetColors()
  }

  def setOffset(x: Int, y: Int) {
    offsetX = x
    offsetY = y
  }

  def resetOffset() {
    offsetX = 0
    offsetY = 0
  }

  /**
   * Refreshes the terminal screen.
   */
  def refresh() {
    out.flush()
  }

  /**
   * Moves the cursor to the desired position.
   * If outside of terminal screen range, will stick to a border.
   * @param x X coordinate of the desired cursor position (0 indexed)
   * @param y Y coordinate of the desired cursor position (0 indexed)
   */
  def move(x: Int, y: Int) {
    csi.move(x, y)
  }

  /**
   * Polls the terminal for a keypress (does not echo the keypress on the terminal)
   * @return Character number of the pressed key
   */
  def keypress(): Int = {
    console.readVirtualKey()
  }

  /**
   * Returns the width and height of the terminal screen in characters
   * @return Tuple containing the width and height of the terminal screen in characters
   */
  def size: (Int, Int) = {
    val (width, height) = csi.screenSize()
    (width, height)
  }

  /**
   * Returns the width and height of the terminal window in pixels
   * @return Tuple containing the width and height of the terminal window in pixels
   */
  def dimensions: (Int, Int) = {
    val (width, height) = csi.windowSize()
    (width, height)
  }

  /**
   * Prepares the terminal screen for Scurses
   */
  def init(): Unit = {
    csi.alternateBuffer()
    csi.clear()
    csi.hideCursor()
    refresh()
  }

  /**
   * Resets the terminal screen
   */
  def close(): Unit = {
    csi.clear()
    csi.normalBuffer()
    csi.showCursor()
    refresh()
  }
}
