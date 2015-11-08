package net.team2xh.scurses

object Keys {

  val PUA = 0xE000 // Unicode private use area, safe for our own codes

  val ESC = 27
  val TAB = 9
  val SHIFT_TAB = 90
  val ENTER = 13
  val BACKSPACE = 127
  val SPACE = 32

  val CTRL_C = 3

  val UP    = PUA + 0
  val DOWN  = PUA + 1
  val LEFT  = PUA + 2
  val RIGHT = PUA + 3

  val A = 97
  val B = 98
  val C = 99
  val D = 100
  val E = 101
  val F = 102
  val G = 103
  val H = 104
  val I = 105
  val J = 106
  val K = 107
  val L = 108
  val M = 109
  val N = 110
  val O = 111
  val P = 112
  val Q = 113
  val R = 114
  val S = 115
  val T = 116
  val U = 117
  val V = 118
  val W = 119
  val X = 120
  val Y = 121
  val Z = 122

  def repr(key: Int): String = key match {
    case ESC       => "ESC"
    case TAB       => "⇥ "
    case SHIFT_TAB => "SHIFT+⇥ "
    case ENTER     => "↵ "
    case BACKSPACE => "⇤ "
    case SPACE     => " "
    case UP        => "↑"
    case DOWN      => "↓"
    case LEFT      => "←"
    case RIGHT     => "→"
    case CTRL_C    => "CTRL+C"
    case _ => key.toChar.toString
  }
}