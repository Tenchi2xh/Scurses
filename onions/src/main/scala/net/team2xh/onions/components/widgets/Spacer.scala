package net.team2xh.onions.components.widgets

import net.team2xh.onions.components.FramePanel
import net.team2xh.scurses.Scurses

case class Spacer(parent: FramePanel)(implicit screen: Scurses) extends Separator(parent, " ") { }
