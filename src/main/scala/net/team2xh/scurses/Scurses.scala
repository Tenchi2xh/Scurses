package net.team2xh.scurses

import java.io.BufferedOutputStream

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.scurses.RichText._

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
  val ec = new EscapeCodes(out)

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
          background: Int = Colors.DIM_BLACK): Unit = {
    ec.move(x + offsetX, y + offsetY)
    ec.setForeground(foreground)
    ec.setBackground(background)
    out.write(string.map(b => if (b >= 32) b else '?').getBytes)
  }
  
  def put(x: Int, y: Int, richText: RichText, theme: ColorScheme): Unit = {
    ec.move(x + offsetX, y + offsetY)
    ec.resetColors()
    ec.setForeground(theme.foreground)
    ec.setBackground(theme.background)
    for (instruction <- richText.instructions) {
      instruction match {
        case Text(text) => out.write(text.getBytes)
        case StartAttribute(attribute) => attribute match {
          case Bold      => ec.startBold()
          case Underline => ec.startUnderline()
          case Blink     => ec.startBlink()
          case Reverse   => ec.startReverse()
          case Foreground(color) => color match {
            case NamedColor(name)   => ec.setForeground(Colors.byName(name))
            case IndexedColor(code) => ec.setForeground(code)
            case HexColor(hex)      =>
          }
          case Background(color) => color match {
            case NamedColor(name)   => ec.setBackground(Colors.byName(name))
            case IndexedColor(code) => ec.setBackground(code)
            case HexColor(hex)      =>
          }
          case _ =>
        }
        case StopAttribute(attribute) => attribute match {
          case Bold       => ec.stopBold()
          case Underline  => ec.stopUnderline()
          case Blink      => ec.stopBlink()
          case Reverse    => ec.stopReverse()
          case Foreground => ec.setForeground(theme.foreground)
          case Background => ec.setBackground(theme.background)
          case _ =>
        }
        case ResetAttributes => ec.resetColors()
      }
    }
  }
  

  def translateOffset(x: Int = 0, y: Int = 0): Unit = {
    offsetX += x
    offsetY += y
  }

  def setOffset(x: Int, y: Int): Unit = {
    offsetX = x
    offsetY = y
  }

  def resetOffset(): Unit = {
    offsetX = 0
    offsetY = 0
  }

  /**
   * Refreshes the terminal screen.
   */
  def refresh(): Unit = {
    out.flush()
  }

  /**
   * Clears the terminal screen.
   */
  def clear(): Unit = {
    ec.clear()
  }

  /**
   * Moves the cursor to the desired position.
   * If outside of terminal screen range, will stick to a border.
   * @param x X coordinate of the desired cursor position (0 indexed)
   * @param y Y coordinate of the desired cursor position (0 indexed)
   */
  def move(x: Int, y: Int): Unit = {
    ec.showCursor()
    ec.move(x + offsetX, y + offsetY)
  }

  def hideCursor(): Unit = {
    ec.hideCursor()
  }

  def showCursor(): Unit = {
    ec.showCursor()
  }

  val delay = 20

  /**
   * Polls the terminal for a keypress (does not echo the keypress on the terminal)
   * @return Character number of the pressed key
   */
  def keypress(): Int = {
    val n = System.in.read()
    if (n == Keys.ESC) {
      Thread.sleep(delay)
      if (System.in.available() != 0) {
        val k = System.in.read()
        if (k == 91) {
          val o = System.in.read()
          o match {
            case 65 => Keys.UP
            case 66 => Keys.DOWN
            case 67 => Keys.RIGHT
            case 68 => Keys.LEFT
            case 90 => Keys.SHIFT_TAB
            case _ => 10000 + o
          }
        } else 20000 + k
      } else Keys.ESC
    } else n
  }

  /**
   * Returns the width and height of the terminal screen in characters
   * @return Tuple containing the width and height of the terminal screen in characters
   */
  def size: (Int, Int) = {
    val (width, height) = ec.screenSize()
    (width, height)
  }

  /**
   * Returns the width and height of the terminal window in pixels
   * @return Tuple containing the width and height of the terminal window in pixels
   */
  def dimensions: (Int, Int) = {
    val (width, height) = ec.windowSize()
    (width, height)
  }

  /**
   * Prepares the terminal screen for Scurses
   */
  def init(): Unit = {
    ec.alternateBuffer()
    ec.clear()
    ec.hideCursor()
    refresh()

    Runtime.getRuntime.exec(Array("sh", "-c", "stty raw -echo < /dev/tty"))
  }

  /**
   * Resets the terminal screen
   */
  def close(): Unit = {
    ec.clear()
    ec.normalBuffer()
    ec.showCursor()
    refresh()

    Runtime.getRuntime.exec(Array("sh", "-c", "stty sane < /dev/tty"))
  }
}
