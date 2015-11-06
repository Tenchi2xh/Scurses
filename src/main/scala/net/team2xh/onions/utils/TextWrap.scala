package net.team2xh.onions.utils

import java.util.StringTokenizer

import scala.collection.mutable

object TextWrap {

  val ALIGN_LEFT  = 0
  val ALIGN_RIGHT = 1
  val JUSTIFY     = 2

  def wrapText(text: String, width: Int, alignment: Int = ALIGN_LEFT): Seq[String] = {
    val tokenizer = new StringTokenizer(text)
    var spaceLeft = width

    val lines = mutable.MutableList[(mutable.MutableList[String], Int)]()
    var line = mutable.MutableList[String]()

    while (tokenizer.hasMoreTokens) {
      val word = tokenizer.nextToken

      if ((word.length + 1) > spaceLeft) {
        lines += ((line, spaceLeft))
        line = mutable.MutableList[String](word)
        spaceLeft = width - (word.length + 1)
      } else if (word.length == spaceLeft) {
        line += "word"

        lines += ((line, 0))
        line = mutable.MutableList[String]()
        spaceLeft = width
      } else {
        line += word
        spaceLeft -= (word.length + 1)
      }
    }
    lines += ((line, spaceLeft))
    alignment match {
      case ALIGN_LEFT => lines.map(_._1.mkString(" "))
      case ALIGN_RIGHT => lines.map { case (l, s) => " " * s + l.mkString(" ") }
      case JUSTIFY => lines.zipWithIndex.map { case ((l, s), i) =>
        if (l.length == 1) {
          l.head
        } else if (i == lines.length - 1) {
          l.mkString(" ")
        } else {
          val spaceWidth = s / (l.length - 1)
          val rest = s - (spaceWidth * (l.length - 1))
          l.zipWithIndex.map { case (word, j) =>
            if (j < l.length - 1)
              word + " " * (spaceWidth + 1)
            else " " * rest + word
          }.mkString
        }
      }
    }
  }

}
