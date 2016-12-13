package net.team2xh.scurses

import fastparse.all._

/**
 * RichText string interpolator.
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
 *   [/*]       Stop all
 *
 * Tags don't have to be closed
 */*/
object RichText {

  implicit class RichTextHelper(val sc: StringContext) extends AnyVal {
    def r(args: Any*): RichText = {
      val input = sc.s(args: _*)
      val result = richText.parse(input)
      result match {
        case Parsed.Success(rt, _) => rt
        case _: Parsed.Failure => RichText(Text(input))
      }
    }
  }

  case class RichText(instructions: Instruction*)

  sealed trait Instruction
  case class Text(text: String) extends Instruction
  case class StartAttribute(attribute: Attribute) extends Instruction
  case class StopAttribute(attribute: Attribute) extends Instruction
  case object ResetAttributes extends Instruction

  sealed trait Attribute
  case object Bold extends Attribute
  case object Underline extends Attribute
  case object Blink extends Attribute
  case object Reverse extends Attribute
  case object Foreground extends Attribute
  case object Background extends Attribute
  case class Foreground(color: Color) extends Attribute
  case class Background(color: Color) extends Attribute

  sealed trait Color
  case class NamedColor(name: String) extends Color
  case class IndexedColor(code: Int) extends Color
  case class HexColor(hex: String) extends Color

  private val letter   = P( CharIn('a' to 'z') )
  private val digit    = P( CharIn('0' to '9') )
  private val hexDigit = P( CharIn('0' to '9', 'a' to 'f', 'A' to 'F') )

  private val name  = P( letter.rep(1).! )
  private val index = P( digit.rep(1).! ) map (_.toInt)
  private val hex   = P( ("#" ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit ~/ hexDigit).! )

  private val bold       = P( "b" )  map (_ => Bold)
  private val underline  = P( "u" )  map (_ => Underline)
  private val blink      = P( "bl" ) map (_ => Blink)
  private val reverse    = P( "r" )  map (_ => Reverse)
  private val foreground = P( "fg" ) map (_ => Foreground)
  private val background = P( "bg" ) map (_ => Background)

  private val attribute  = P( underline | blink | bold | reverse | foreground | background )

  private val namedColor   = P( name ) map NamedColor
  private val indexedColor = P( index ) map IndexedColor
  private val hexColor     = P( hex ) map HexColor

  private val color = P( namedColor | indexedColor | hexColor )

  private val startAttribute = P( attribute ) map StartAttribute
  private val beginColor = P( ("fg" | "bg").! ~ ":" ~/ color ) map {
    case ("fg", aColor) => StartAttribute(Foreground(aColor))
    case (_, aColor)    => StartAttribute(Background(aColor))
  }
  private val stop = P( "/" ~/ ("*".! | attribute) ) map {
    case "*" => ResetAttributes
    case attr: Attribute => StopAttribute(attr)
  }

  private val block = P( "[" ~/ (beginColor | startAttribute | stop) ~/ "]" )
  private val escape = P( "[[".! ) map (_ => Text("["))
  private val text = P( CharsWhile(c => c != '[').! ) map Text

  private val richText = P( (text | escape | block).rep ) map (RichText(_: _*))

}