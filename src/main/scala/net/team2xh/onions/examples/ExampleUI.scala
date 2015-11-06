package net.team2xh.onions.examples

import net.team2xh.onions.components.{FramePanel, Frame}
import net.team2xh.onions.components.widgets.{Spacer, Separator, Label}
import net.team2xh.onions.utils.TextWrap
import net.team2xh.scurses.Scurses

object ExampleUI extends App {
  
  Scurses { implicit screen =>
    val frame = Frame("Example Onions UI - Powered by Scurses")
    frame.debug = true

    val colA = frame.panel
    val colB = colA.splitRight
    val colC = colB.splitRight

    val colB2 = colB.splitDown
    val colB3 = colB2.splitDown
    val colB3B = colB3.splitRight
    val colC2 = colC.splitDown

    colA.title = "Labels"

    Label(colA, "Left-aligned text: ")
    Separator(colA)
    Label(colA, "Etiam sit amet lacinia quam, sed efficitur lorem. Integer sit amet diam at tortor molestie pellentesque eget euismod lorem. Vivamus varius purus sed ex aliquam lacinia.")
    Separator(colA)
    Label(colA, "Justified text:")
    Separator(colA)
    Label(colA, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque enim erat, sollicitudin vitae lacus nec, consectetur porta velit. Donec porta placerat lectus, in porttitor elit.", TextWrap.JUSTIFY)
    Separator(colA)
    Label(colA, "Right-aligned text: ")
    Separator(colA)
    Label(colA, "Nam ut odio ex. Donec faucibus in odio in blandit. In mattis sodales mi, quis rhoncus lorem tincidunt sed. Ut varius gravida augue at tristique. Suspendisse lacinia mi nunc, ac convallis elit consequat a.", TextWrap.ALIGN_RIGHT)
    Separator(colA)
    Label(colA, "Centered text: ")
    Separator(colA)
    Label(colA, "Etiam sit amet lacinia quam, sed efficitur lorem. Integer sit amet diam at tortor molestie pellentesque eget euismod lorem. Vivamus varius purus sed ex aliquam lacinia.", TextWrap.CENTER)

    frame.show()
  }
}
