package net.team2xh.scurses

import scala.collection.mutable.ArrayBuffer

object CellBuffer {

  def apply(width: Int, height: Int)(implicit screen: Scurses): CellBuffer = {
    val buffer = ArrayBuffer.fill[Cell](height, width)(Cell.empty)
    new CellBuffer(width, height, buffer)
  }

  def apply(cellBuffer: CellBuffer, rect: Rect)(implicit screen: Scurses): CellBuffer = {
    assert(rect.x0 < cellBuffer.width &&
           rect.y0 < cellBuffer.height &&
           rect.x0 + rect.width < cellBuffer.width &&
           rect.y0 + rect.height < cellBuffer.height,
           s"$rect has to be contained in Buffer")

    val subBuffer = cellBuffer.buffer
      .slice(rect.y0, rect.y0 + rect.height)
      .map { line =>
        line.slice(rect.x0, rect.x0 + rect.width)
      }
    new CellBuffer(rect.width, rect.height, subBuffer)
  }
}

class CellBuffer(val width: Int, val height: Int, private val buffer: ArrayBuffer[ArrayBuffer[Cell]])
                (implicit screen: Scurses) {

  def view(rect: Rect): CellBuffer = CellBuffer(this, rect)
  def resize(width: Int, height: Int): CellBuffer = view(Rect(0, 0, width, height))
  def apply(x: Int, y: Int): Cell = buffer(y)(x)
  def update(x: Int, y: Int, cell: Cell): Unit = buffer(y)(x) = cell

  def put(x: Int, y: Int, string: String,
          foreground: Int = -1,
          background: Int = -1): Unit = {
    val cells = string.map(Cell(_, Style(foreground = foreground, background = background)))
    cells.foreach(cell => buffer(y)(x) = cell)
  }

  def draw(x0: Int, y0: Int): Unit = {

    def matchNewStyle(oldStyle: Style, newStyle: Style): Unit = {
      if (oldStyle.foreground != newStyle.foreground) screen.ec.setForeground(newStyle.foreground)
      if (oldStyle.background != newStyle.background) screen.ec.setBackground(newStyle.foreground)
      if (!oldStyle.bold && newStyle.bold) screen.ec.startBold()
      else if (oldStyle.bold && !newStyle.bold) screen.ec.stopBold()
      if (!oldStyle.underlined && newStyle.underlined) screen.ec.startUnderline()
      else if (oldStyle.underlined && !newStyle.underlined) screen.ec.stopUnderline()
      if (!oldStyle.blinking && newStyle.blinking) screen.ec.startBlink()
      else if (oldStyle.blinking && !newStyle.blinking) screen.ec.stopBlink()
      if (!oldStyle.reversed && newStyle.reversed) screen.ec.startReverse()
      else if (oldStyle.reversed && !newStyle.reversed) screen.ec.stopReverse()
    }

    for ((line, y) <- buffer.zipWithIndex) {

      screen.ec.move(x0, y0 + y)

      var lastStyle = Style()
      var current = ""

      def flush(): Unit = {
        screen.out.write(current.getBytes)
        current = ""
      }

      line.foreach { cell =>
        if (cell.style != lastStyle) {
          flush()
          matchNewStyle(lastStyle, cell.style)
          lastStyle = cell.style
        }
        current += cell.symbol
      }
      flush()
      screen.ec.resetColors()
    }
  }
}

case class Style(foreground: Int = Colors.BRIGHT_WHITE,
                 background: Int = Colors.DIM_BLACK,
                 bold: Boolean = false,
                 underlined: Boolean = false,
                 blinking: Boolean = false,
                 reversed: Boolean = false)

object Cell {
  val empty = Cell(' ', Style())
}

case class Cell(symbol: Char, style: Style)

case class Rect(x0: Int, y0: Int, width: Int, height: Int)
