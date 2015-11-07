package net.team2xh.onions.components.widgets

import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{Widget, FramePanel}
import net.team2xh.onions.utils.Varying
import net.team2xh.scurses.{Scurses, Colors}

abstract class FontMapper(parent: FramePanel, empty: Seq[String],
                          symbols: Map[Char, Seq[String]], text: Varying[String],
                          color: Varying[Int] = Colors.BRIGHT_WHITE)
                         (implicit screen: Scurses) extends Widget(parent, text, color) {

  override def focusable: Boolean = false

  override def draw(focus: Boolean, theme: ColorScheme): Unit = {
    val t = text.value
    val h = empty.length
    if (!t.isEmpty) {
      val x1 = t.toLowerCase.map(c => (c, symbols.getOrElse(c, empty).head.length))
      val x2 = x1.foldLeft(Seq[(String, Int)](("", 0))) { case (accu, (c, l)) =>
        val current = accu.last
        if (current._2 + l > innerWidth) {
          accu :+ (s"$c", l)
        } else {
          accu.init :+ (current._1 + c, current._2 + l)
        }
      }
      val wrapped = x2.map(_._1)
      val width = x2.maxBy(_._2)._2
      val c = if (color.value < 0) theme.foreground else color.value
      for ((chunk, i) <- wrapped.zipWithIndex) {
        val chars = chunk.toLowerCase.map(symbols.getOrElse(_, empty) ++ Seq("    "))
        for (y <- 0 until h) {
          screen.put((innerWidth - width) / 2, y + i * 3, ("" /: chars)((line, char) => line + char(y)),
            foreground = c, background = theme.background)
        }
      }
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def innerHeight: Int = 3
}
