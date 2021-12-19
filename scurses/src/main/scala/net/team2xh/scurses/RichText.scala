package net.team2xh.scurses

import fastparse._
import NoWhitespace._

/** RichText string interpolator.
  *
  * Example usage:
  *   val color = "blue"
  *   val greet = "Hello"
  *   val richText = r"[b][fg:$color]$greet[/b], World!"
  *
  * Format:
  *   [b]        Start using bold text
  *   [u]        Start underlining text
  *   [bl]       Start blinking text
  *   [r]        Start reversing foreground and background
  *   [fg:color] Start coloring the foreground with a named color (e.g. "red")
  *   [fg:id]    Same with an xterm 256-color code (0 - 256)
  *   [fg:hex]   Same with an arbitrary RGB hexadecimal color code
  *   [bg:color] Start coloring the background with a named color (e.g. "red")
  *   [bg:id]    Same with an xterm 256-color code (0 - 256)
  *   [bg:hex]   Same with an arbitrary RGB hexadecimal color code
  *
  *   [/b]       Stop using bold text
  *   [/u]       Stop underlining text
  *   [/bl]      Stop blinking text
  *   [/r]       Stop reversing foreground and background
  *   [/fg]      Stop coloring the foreground
  *   [/bg]      Stop coloring the background
  *   [&#47*]    Stop all
  *
  * Tags don't have to be closed
  */
object RichText {

  implicit class RichTextHelper(val sc: StringContext) extends AnyVal {
    def r(args: Any*): RichText = {
      val input  = sc.s(args: _*)
      val result = parse(input, richText(_))
      result match {
        case Parsed.Success(rt, _) => rt
        case _: Parsed.Failure     => RichText(Text(input))
      }
    }
  }

  final case class RichText(instructions: Instruction*)

  sealed trait Instruction
  final case class Text(text: String)                   extends Instruction
  final case class StartAttribute(attribute: Attribute) extends Instruction
  final case class StopAttribute(attribute: Attribute)  extends Instruction
  case object ResetAttributes                           extends Instruction

  sealed trait Attribute
  case object Bold                          extends Attribute
  case object Underline                     extends Attribute
  case object Blink                         extends Attribute
  case object Reverse                       extends Attribute
  case object Foreground                    extends Attribute
  case object Background                    extends Attribute
  final case class Foreground(color: Color) extends Attribute
  final case class Background(color: Color) extends Attribute

  sealed trait Color
  final case class NamedColor(name: String) extends Color
  final case class IndexedColor(code: Int)  extends Color
  final case class HexColor(hex: String)    extends Color

  private def letter[_: P]   = P(CharIn("a-z"))
  private def digit[_: P]    = P(CharIn("0-9"))
  private def hexDigit[_: P] = P(CharIn("0-9", "a-f", "A-F"))

  private def name[_: P]  = P(letter.rep(1).!)
  private def index[_: P] = P(digit.rep(1).!) map (_.toInt)
  private def hex[_: P]   = P(("#" ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit).!)

  private def bold[_: P]       = P("b") map (_ => Bold)
  private def underline[_: P]  = P("u") map (_ => Underline)
  private def blink[_: P]      = P("bl") map (_ => Blink)
  private def reverse[_: P]    = P("r") map (_ => Reverse)
  private def foreground[_: P] = P("fg") map (_ => Foreground)
  private def background[_: P] = P("bg") map (_ => Background)

  private def attribute[_: P] = P(underline | blink | bold | reverse | foreground | background)

  private def namedColor[_: P]   = P(name) map NamedColor
  private def indexedColor[_: P] = P(index) map IndexedColor
  private def hexColor[_: P]     = P(hex) map HexColor

  private def color[_: P] = P(namedColor | indexedColor | hexColor)

  private def startAttribute[_: P] = P(attribute) map StartAttribute
  private def beginColor[_: P] = P(("fg" | "bg").! ~ ":" ~/ color) map {
    case ("fg", aColor) => StartAttribute(Foreground(aColor))
    case (_, aColor)    => StartAttribute(Background(aColor))
  }
  private def stop[_: P] = P("/" ~/ ("*".! | attribute)) map {
    case "*"             => ResetAttributes
    case attr: Attribute => StopAttribute(attr)
  }

  private def block[_: P]  = P("[" ~/ (beginColor | startAttribute | stop) ~/ "]")
  private def escape[_: P] = P("[[".!) map (_ => Text("["))
  private def text[_: P]   = P(CharsWhile(c => c != '[').!) map Text

  private def richText[_: P] = P((text | escape | block).rep) map (RichText(_: _*))

}
