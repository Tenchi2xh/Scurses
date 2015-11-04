package net.team2xh.scurses

import java.io.OutputStream

import scala.tools.jline.console.ConsoleReader

/**
 * ANSI Escape Codes manager
 * Reference: http://www.vt100.net/docs/vt510-rm/contents
 *            http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
 *
 * @param out Terminal output stream
 */
class EscapeCodes(out: OutputStream) {

  val console = new ConsoleReader()

  // Output an Escape Sequence
  private def ESC(command: Char) { out.write(s"\033$command".getBytes) }
  // Output a Control Sequence Inroducer
  private def CSI(sequence: String) { out.write(s"\033[$sequence".getBytes) }
  // Execute commands
  private def CSI(command: Char) { CSI(s"$command") }
  private def CSI(n: Int, command: Char) { CSI(s"$n;$command") }
  private def CSI(n: Int, m: Int, command: Char) { CSI(s"$n;$m$command") }
  private def CSI(n: Int, m: Int, o: Int, command: Char) { CSI(s"$n;$m;$o$command") }
  // Execute commands in private modes
  private def CSI(mode: Char, command: Char) { CSI(s"$mode$command") }
  private def CSI(mode: Char, n: Int, command: Char) { CSI(s"$mode$n;$command") }

  // Cursor movement
  /* CUU */ def moveUp   (n: Int = 1) { CSI(n, 'A') }
  /* CUD */ def moveDown (n: Int = 1) { CSI(n, 'B') }
  /* CUF */ def moveRight(n: Int = 1) { CSI(n, 'C') }
  /* CUB */ def moveLeft (n: Int = 1) { CSI(n, 'D') }
  /* CUP */ def move(y: Int, x: Int)  { CSI(x + 1, y + 1, 'H') }

  // Cursor management
  /* DECTCEM */ def hideCursor()    { CSI('?', 25, 'l') }
  /* DECTCEM */ def showCursor()    { CSI('?', 25, 'h') }
  /*  DECSC  */ def saveCursor()    { ESC('7') }
  /*  DECRC  */ def restoreCursor() { ESC('8') }
  // Somehow this fails when the window has a height of 30-39:
  /*   CPR   */ def cursorPosition() = { val r = getReport(() => CSI(6, 'n'), 2, 'R'); (r(1), r(0)) }

  // Screen management
  /*   ED   */ def clear()                      { CSI(2, 'J') }
  /* DECSET */ def alternateBuffer()            { CSI('?', 47, 'h') }
  /* DECRST */ def normalBuffer()               { CSI('?', 47, 'l') }
  /*   RIS  */ def fullReset()                  { ESC('c') }
  /* dtterm */ def resizeScreen(w: Int, h: Int) { CSI(8, w, h, 't') }
  /* dtterm */ def screenSize() = { val r = getReport(() => CSI(18, 't'), 3, 't'); (r(2), r(1)) }

  // Window management
  /* dtterm */ def unminimizeWindow()           { CSI(1, 't')}
  /* dtterm */ def minimizeWindow()             { CSI(2, 't')}
  /* dtterm */ def moveWindow(x: Int, y: Int)   { CSI(3, x, y, 't') }
  /* dtterm */ def resizeWindow(w: Int, h: Int) { CSI(4, w, h, 't') }
  /* dtterm */ def moveToTop()                  { CSI(5, 't') }
  /* dtterm */ def moveToBottom()               { CSI(6, 't') }
  /* dtterm */ def restoreWindow()              { CSI(9, 0, 't')}
  /* dtterm */ def maximizeWindow()             { CSI(9, 1, 't')}
  /* dtterm */ def windowPosition() = { val r = getReport(() => CSI(13, 't'), 3, 't'); (r(2), r(1)) }
  /* dtterm */ def windowSize()     = { val r = getReport(() => CSI(14, 't'), 3, 't'); (r(2), r(1)) }

  // Color management
  /* ISO-8613-3 */ def setForeground(color: Int) { CSI(38, 5, color, 'm') }
  /* ISO-8613-3 */ def setBackground(color: Int) { CSI(48, 5, color, 'm') }
  /*     SGR    */ def resetColors() { CSI(0, 'm') }

  /**
   * Executes a request and parses the response report.
   * Usually, they would start with a CSI but JLine seems to ignore them.
   * @param csi        CSI to execute
   * @param args       How many arguments are expected
   * @param terminator Terminator character of the report
   * @return           Sequence of parsed integers
   */
  def getReport(csi: () => Unit, args: Int, terminator: Char): Array[Int] = {
    // Send the CSI
    csi()
    out.flush()

    val results = Array.fill(args)("")
    val separators = Array.fill(args - 1)(';') :+ terminator
    // Parse each Ps
    for (i <- 0 until args) {
      var n = console.readVirtualKey()
      while (n != separators(i).toInt) {
        results(i) += n.toChar
        n = console.readVirtualKey()
      }
    }
    results.map(_.toInt)
  }
}

object Colors {
  // Color codes
  val DIM_BLACK      =  0
  val DIM_RED        =  1
  val DIM_GREEN      =  2
  val DIM_YELLOW     =  3
  val DIM_BLUE       =  4
  val DIM_MAGENTA    =  5
  val DIM_CYAN       =  6
  val DIM_WHITE      =  7
  val BRIGHT_BLACK   =  8
  val BRIGHT_RED     =  9
  val BRIGHT_GREEN   = 10
  val BRIGHT_YELLOW  = 11
  val BRIGHT_BLUE    = 12
  val BRIGHT_MAGENTA = 13
  val BRIGHT_CYAN    = 14
  val BRIGHT_WHITE   = 15
}
