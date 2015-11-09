package net.team2xh.onions.components.widgets

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import net.team2xh.onions.Symbols
import net.team2xh.onions.Themes.ColorScheme
import net.team2xh.onions.components.{FramePanel, Widget}
import net.team2xh.scurses.{Colors, Scurses}

object BitMap {

  System.setProperty("java.awt.headless", "true")

  def apply(parent: FramePanel, path: String, relative: Boolean = false)(implicit screen: Scurses): BitMap = {
    val fullPath = if (relative) new File("").getAbsolutePath + path else path
    val image = ImageIO.read(new File(fullPath))
    new BitMap(parent, image)
  }

  def apply(parent: FramePanel, image: BufferedImage)(implicit screen: Scurses): BitMap = {
    new BitMap(parent, image)
  }

}

class BitMap(parent: FramePanel, image: BufferedImage)
                 (implicit screen: Scurses) extends Widget(parent) {

  override def draw(focus: Boolean, theme: ColorScheme): Unit = {
    val width = image.getWidth min innerWidth
    val height = innerHeight * 2
    val x0 = (innerWidth - width) / 2
    for (x <- 0 until width) {
      for (y <- 0 until innerHeight) {
        // Read two rows at a time
        val upper = Colors.fromRGBInt(image.getRGB(x, y * 2))
        val lower = if (height % 2 == 1) -1 else Colors.fromRGBInt(image.getRGB(x, y * 2 + 1))
        screen.put(x0 + x, y, Symbols.BLOCK_UPPER, upper, lower)
      }
    }
  }

  override def handleKeypress(keypress: Int): Unit = { }

  override def focusable: Boolean = false
  override def innerHeight: Int = image.getHeight / 2
}
