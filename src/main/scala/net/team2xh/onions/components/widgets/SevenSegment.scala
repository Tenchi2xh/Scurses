package net.team2xh.onions.components.widgets

import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.components.widgets.SevenSegment.{symbols, empty}
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.{Colors, Scurses}

case class SevenSegment(parent: FramePanel, var text: Varying[String],
                        var foreground: Varying[Int] = Colors.BRIGHT_GREEN,
                        var background: Varying[Int] = Colors.DIM_BLACK)
                  (implicit screen: Scurses) extends Widget(parent, text, foreground, background) {

  override def focusable: Boolean = false

  override def draw(focus: Boolean): Unit = {
    val t = text.value
    if (!t.isEmpty) {
      val wrapped = t.grouped(innerWidth / 4).toList
      val width = wrapped.head.length * 4
      for ((chunk, i) <- wrapped.zipWithIndex) {
        val chars = chunk.toLowerCase.map(symbols.getOrElse(_, empty) ++ Seq("    "))
        for (y <- 0 until 4) {
          screen.put((innerWidth - width) / 2, y + i * 3, ("" /: chars)((line, char) => line + char(y)),
            foreground = foreground.value, background = background.value)
        }
      }
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def innerHeight: Int = 3
}

object SevenSegment {
  var empty = Seq("", "", "", "")
  val symbols = Map(
    'a' -> Seq(
      " _  ",
      "|_| ",
      "| | "),
    'b' -> Seq(
      " _  ",
      "|_| ",
      "|_| "),
    'c' -> Seq(
      " _  ",
      "|   ",
      "|_  "),
    'd' -> Seq(
      "    ",
      " _| ",
      "|_| "),
    'e' -> Seq(
      " _  ",
      "|_  ",
      "|_  "),
    'f' -> Seq(
      " _  ",
      "|_  ",
      "|   "),
    'g' -> Seq(
      " _  ",
      "|_| ",
      " _| "),
    'h' -> Seq(
      "    ",
      "|_  ",
      "| | "),
    'i' -> Seq(
      "    ",
      "|   ",
      "|   "),
    'j' -> Seq(
      "    ",
      "  | ",
      "|_| "),
    'k' -> Seq(
      "    ",
      "|_| ",
      "| | "),
    'l' -> Seq(
      "    ",
      "|   ",
      "|_  "),
    'm' -> Seq(
      " _  ",
      "    ",
      "| | "),
    'n' -> Seq(
      "    ",
      " _  ",
      "| | "),
    'o' -> Seq(
      "    ",
      " _  ",
      "|_| "),
    'p' -> Seq(
      " _  ",
      "|_| ",
      "|   "),
    'q' -> Seq(
      " _  ",
      "|_| ",
      "  | "),
    'r' -> Seq(
      "    ",
      " _  ",
      "|   "),
    '0' -> Seq(
      " _  ",
      "|_  ",
      " _| "),
    't' -> Seq(
      "    ",
      "|_  ",
      "|_  "),
    'u' -> Seq(
      "    ",
      "| | ",
      "|_| "),
    'v' -> Seq(
      "    ",
      "    ",
      "|_| "),
    'w' -> Seq(
      "    ",
      "| | ",
      " _  "),
    'x' -> Seq(
      "    ",
      "|_| ",
      "| | "),
    'y' -> Seq(
      "    ",
      "|_| ",
      " _| "),
    'z' -> Seq(
      " _  ",
      " _| ",
      "|_  "),
    '0' -> Seq(
      " _  ",
      "| | ",
      "|_| "),
    '1' -> Seq(
      "    ",
      "  | ",
      "  | "),
    '2' -> Seq(
      " _  ",
      " _| ",
      "|_  "),
    '3' -> Seq(
      " _  ",
      " _| ",
      " _| "),
    '4' -> Seq(
      "    ",
      "|_| ",
      "  | "),
    '5' -> Seq(
      " _  ",
      "|_  ",
      " _| "),
    '6' -> Seq(
      " _  ",
      "|_  ",
      "|_| "),
    '7' -> Seq(
      " _  ",
      "  | ",
      "  | "),
    '8' -> Seq(
      " _  ",
      "|_| ",
      "|_| "),
    '9' -> Seq(
      " _  ",
      "|_| ",
      " _| "),
    ':' -> Seq(
      "    ",
      " .  ",
      " .  "),
    '.' -> Seq(
      "    ",
      "    ",
      " .  "),
    ' ' -> Seq(
      "    ",
      "    ",
      "    "),
    '-' -> Seq(
      "    ",
      " -  ",
      "    ")
  )
}