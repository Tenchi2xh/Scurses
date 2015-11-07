package net.team2xh.onions

import net.team2xh.scurses.Colors

object Themes {

  sealed trait ColorScheme {
    def foreground: Int
    def background: Int
    def focusForeground: Int
    def focusBackground: Int
    val accent1: Int
    val accent2: Int
    val accent3: Int
    def foreground(focus: Boolean): Int = if (focus) focusForeground else foreground
    def background(focus: Boolean): Int = if (focus) focusBackground else background
  }

  val default = new ColorScheme {
    override val foreground = 15
    override val background = 0
    override val focusForeground = background
    override val focusBackground = foreground
    override val accent1 = 237
    override val accent2 = 248
    override val accent3 = 250
  }

  val light = new ColorScheme {
    override val foreground: Int = 242
    override val background: Int = 255
    override val focusForeground: Int = 0
    override val focusBackground: Int = 251
    override val accent1: Int = 253
    override val accent3: Int = 252
    override val accent2: Int = 249
  }

  val DOS = new ColorScheme {
    override val foreground: Int = 248
    override val background: Int = 4
    override val focusForeground: Int = 0
    override val focusBackground: Int = foreground
    override val accent1: Int = 6
    override val accent3: Int = 8
    override val accent2: Int = 11
  }

}
