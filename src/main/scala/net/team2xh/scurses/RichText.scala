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
      result.get.value
    }
  }

  case class RichText(elements: Instruction*)

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

  val letter   = P( CharIn('a' to 'z') )
  val digit    = P( CharIn('0' to '9') )
  val hexDigit = P( CharIn('0' to '9', 'a' to 'f', 'A' to 'F') )

  val name  = P( letter.rep(1).! )
  val index = P( digit.rep(1).! ) map (_.toInt)
  val hex   = P( ("#" ~! hexDigit ~! hexDigit ~! hexDigit ~! hexDigit ~! hexDigit ~! hexDigit).! )

  val bold       = P( "b" )  map (_ => Bold)
  val underline  = P( "u" )  map (_ => Underline)
  val blink      = P( "bl" ) map (_ => Blink)
  val reverse    = P( "r" )  map (_ => Reverse)
  val foreground = P( "fg" ) map (_ => Foreground)
  val background = P( "bg" ) map (_ => Background)

  val attribute  = P( bold | underline | blink | reverse | foreground | background )

  val namedColor   = P( name ) map NamedColor
  val indexedColor = P( index ) map IndexedColor
  val hexColor     = P( hex ) map HexColor

  val color = P( namedColor | indexedColor | hexColor )

  val startAttribute = P( attribute ) map StartAttribute
  val beginColor = P( ("fg" | "bg").! ~ ":" ~! color ) map {
    case ("fg", aColor) => StartAttribute(Foreground(aColor))
    case (_, aColor)    => StartAttribute(Background(aColor))
  }
  val stop = P( "/" ~! ("*".! | attribute) ) map {
    case "*" => ResetAttributes
    case attr: Attribute => StopAttribute(attr)
  }

  val block = P( "[" ~! (beginColor | startAttribute | stop) ~! "]" )
  val escape = P( "[[".! ) map (_ => Text("["))
  val text = P( CharsWhile(c => c != '[').! ) map Text

  val richText = P( (text | escape | block).rep ) map (RichText(_: _*))

}