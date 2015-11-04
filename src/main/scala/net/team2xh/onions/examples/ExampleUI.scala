package net.team2xh.onions.examples

import net.team2xh.onions.components.{FramePanel, Frame}
import net.team2xh.onions.components.widgets.Label
import net.team2xh.scurses.Scurses

object ExampleUI extends App {
  
  def addLabels(fp: FramePanel)(implicit screen: Scurses): Unit = {
    fp.widgets += Label(fp, "Hello, world! This is a label which can contain many words that will be wrapped smartly.")
    fp.widgets += Label(fp, "Just another widget.")
    fp.widgets += Label(fp, "Last widget I promise.")
  }
  
  Scurses { implicit screen =>
    val frame = Frame("Example UI")

    val p1 = frame.panel
    val p2 = p1.splitRight
    val p3 = p2.splitRight

    val p22 = p2.splitDown
    val p23 = p22.splitDown

    val p232 = p23.splitRight

    val p32 = p3.splitDown

    addLabels(p1)
    addLabels(p2)
    addLabels(p3)
    addLabels(p22)
    addLabels(p23)
    addLabels(p232)
    addLabels(p32)

    frame.show()
  }
}
