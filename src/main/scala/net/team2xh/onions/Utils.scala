package net.team2xh.onions

import java.util.StringTokenizer

object Utils {

  def wrapText(text: String, width: Int): Seq[String] = {
    val tokenizer = new StringTokenizer(text)
    var spaceLeft = width
    val result = new StringBuilder()
    while (tokenizer.hasMoreTokens) {
      val word = tokenizer.nextToken

      if ((word.length + 1) > spaceLeft) {
        result.append(" " * spaceLeft + "\n" + word + " ")
        spaceLeft = width - (word.length + 1)
      } else if (word.length == spaceLeft) {
        result.append(word + "\n")
        spaceLeft = width
      } else {
        result.append(word + " ")
        spaceLeft -= (word.length + 1)
      }
    }
    result.append(" " * spaceLeft)
    result.mkString.split('\n')
  }
}
