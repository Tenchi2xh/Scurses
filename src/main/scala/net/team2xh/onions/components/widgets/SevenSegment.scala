package net.team2xh.onions.components.widgets

import net.team2xh.onions.components.FramePanel
import net.team2xh.onions.components.widgets.SevenSegment.{empty, symbols}
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.{Colors, Scurses}

case class SevenSegment(parent: FramePanel, text: Varying[String],
                        color: Varying[Int] = Colors.BRIGHT_GREEN)
                  (implicit screen: Scurses) extends FontMapper(parent, empty, symbols, text, color)

object SevenSegment {
  var empty = Seq("", "", "")
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
    's' -> Seq(
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