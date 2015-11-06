package net.team2xh.onions.examples

import net.team2xh.onions.components.{FramePanel, Frame}
import net.team2xh.onions.components.widgets.{Separator, Label}
import net.team2xh.onions.utils.TextWrap
import net.team2xh.scurses.Scurses

object ExampleUI extends App {
  
  Scurses { implicit screen =>
    val frame = Frame("Example UI")
    frame.debug = true

    val p1 = frame.panel
    val p2 = p1.splitRight
    val p3 = p2.splitRight

    val p22 = p2.splitDown
    val p23 = p22.splitDown

    val p232 = p23.splitRight

    val p32 = p3.splitDown

    p1.widgets += Label(p1, "Left-aligned text: ")
    p1.widgets += Separator(p1)
    p1.widgets += Label(p1, "Etiam sit amet lacinia quam, sed efficitur lorem. Integer sit amet diam at tortor molestie pellentesque eget euismod lorem. Vivamus varius purus sed ex aliquam lacinia.")
    p1.widgets += Separator(p1)
    p1.widgets += Label(p1, "Justified text:")
    p1.widgets += Separator(p1)
    p1.widgets += Label(p1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque enim erat, sollicitudin vitae lacus nec, consectetur porta velit. Donec porta placerat lectus, in porttitor elit.", TextWrap.JUSTIFY)
    p1.widgets += Separator(p1)
    p1.widgets += Label(p1, "Right-aligned text: ")
    p1.widgets += Separator(p1)
    p1.widgets += Label(p1, "Nam ut odio ex. Donec faucibus in odio in blandit. In mattis sodales mi, quis rhoncus lorem tincidunt sed. Ut varius gravida augue at tristique. Suspendisse lacinia mi nunc, ac convallis elit consequat a.", TextWrap.ALIGN_RIGHT)
//    addLabels(p22)
//    addLabels(p23)
//    addLabels(p232)
//    addLabels(p32)

    frame.show()
  }
}
