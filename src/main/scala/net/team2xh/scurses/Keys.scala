package net.team2xh.scurses

object Keys {

  val PUA = 0xE000 // Unicode private use area, safe for our own codes

  // Normal keys
  val CTRL_C    = 3
  val TAB       = 9
  val ENTER     = 13
  val ESC       = 27
  val SPACE     = 32
  val BACKSPACE = 127

  // CSI keys
  val UP        = PUA + 0
  val DOWN      = PUA + 1
  val LEFT      = PUA + 2
  val RIGHT     = PUA + 3
  val SHIFT_TAB = PUA + 4

  def repr(key: Int): String = key match {
    case CTRL_C    => "CTRL+C"
    case TAB       => "⇥ "
    case ENTER     => "↵ "
    case ESC       => "ESC"
    case SPACE     => " "
    case BACKSPACE => "⇤ "
    case UP        => "↑"
    case DOWN      => "↓"
    case LEFT      => "←"
    case RIGHT     => "→"
    case SHIFT_TAB => "⇧ +⇥ "
    case _ => key.toChar.toString
  }
}