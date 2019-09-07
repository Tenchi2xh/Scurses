package net.team2xh.onions.utils

import java.util.StringTokenizer

import net.team2xh.scurses.RichText._

import scala.collection.mutable

object TextWrap {

  val ALIGN_LEFT  = 0
  val ALIGN_RIGHT = 1
  val CENTER      = 2
  val JUSTIFY     = 3

  def wrapText(richText: RichText, width: Int): mutable.Seq[RichText] = {
    wrapText(richText, width, ALIGN_LEFT)
  }

  def wrapText(richText: RichText, width: Int, alignment: Int): mutable.Seq[RichText] = {
    val instructions = richText.instructions.iterator
    var spaceLeft = width

    var activeAttributes = mutable.Map[Attribute, Instruction]()
    var lines = mutable.ArrayDeque[(List[Instruction], Int)]()
    var line = mutable.ArrayDeque[Instruction]()
    var chunk = ""

    while (instructions.hasNext) {
      val instruction = instructions.next()
      instruction match {
        case StartAttribute(attribute) =>
          activeAttributes += attribute -> instruction
          line += instruction
        case StopAttribute(attribute) =>
          attribute match {
            case Foreground => activeAttributes.filterNot { case (k, v) => k.isInstanceOf[Foreground] }
            case Background => activeAttributes.filterNot { case (k, v) => k.isInstanceOf[Background] }
            case _ => activeAttributes -= attribute
          }
          line += instruction
        case ResetAttributes =>
          activeAttributes.clear()
          line += instruction
        case Text(text) =>
          val tokenizer = new StringTokenizer(text)

          while (tokenizer.hasMoreTokens) {
            val word = tokenizer.nextToken

            if ((word.length + 1) > spaceLeft) {
              line += Text(chunk)
              lines += ((line.toList, spaceLeft))
              line = mutable.ArrayDeque[Instruction]()
              activeAttributes.foreach { case (k, v) => line += v }
              chunk = word + " "
              spaceLeft = width - (word.length + 1)
            } else {
              chunk += word + " "
              spaceLeft -= (word.length + 1)
            }
          }
          line += Text(chunk)
          chunk = ""
      }
    }
    lines += ((line.toList, spaceLeft))
    lines.map(t => RichText(t._1: _*))
  }

  def wrapText(text: String, width: Int, alignment: Int = ALIGN_LEFT): Seq[String] = {
    val tokenizer = new StringTokenizer(text)
    var spaceLeft = width

    val lines = mutable.ArrayDeque[(List[String], Int)]()
    var line = mutable.ArrayDeque[String]()

    while (tokenizer.hasMoreTokens) {
      val word = tokenizer.nextToken

      if ((word.length + 1) > spaceLeft) {
        lines += ((line.toList, spaceLeft))
        line = mutable.ArrayDeque[String](word)
        spaceLeft = width - (word.length + 1)
      } else {
        line += word
        spaceLeft -= (word.length + 1)
      }
    }
    lines += ((line.toList, spaceLeft))
    alignment match {
      case ALIGN_LEFT => lines.map(_._1.mkString(" ")).toSeq
      case ALIGN_RIGHT => lines.map { case (l, s) => " " * s + l.mkString(" ") }.toSeq
      case CENTER => lines.map { case (l, s) =>
        val s1 = s / 2
        val s2 = s - s1
        " " * s1 + l.mkString(" ") + " " * s2
      }.toSeq
      case JUSTIFY => lines.zipWithIndex.map { case ((l, s), i) =>
        if (l.length == 1) {
          l.head
        } else if (i == lines.length - 1) {
          l.mkString(" ")
        } else {
          val spaceWidth = s / (l.length - 1)
          var rest = s - (spaceWidth * (l.length - 1))
          l.zipWithIndex.map { case (word, j) =>
            if (rest > 0) {
              rest -= 1
              word + " " * (spaceWidth + 2)
            } else if (j == l.length - 1) {
              word
            } else word + " " * (spaceWidth + 1)
          }.mkString
        }
      }.toSeq
    }
  }

}
