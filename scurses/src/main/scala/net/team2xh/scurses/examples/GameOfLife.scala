package net.team2xh.scurses.examples

import java.util.{Timer, TimerTask}

import net.team2xh.scurses.Scurses

import scala.util.Random

case class GameOfLife(width: Int, height: Int, wrapAround: Boolean = false) {

  var cells = for (y <- 0 until height) yield for (x <- 0 until width) yield 0

  def field = cells

  def set(x0: Int, y0: Int, value: Int = 1): Unit = {
    cells = for (y <- 0 until height) yield for (x <- 0 until width) yield {
      if (x == x0 && y == y0) value
      else cells(y)(x)
    }
  }

  def glider(x0: Int, y0: Int): Unit = {
    set(x0, y0)
    set(x0+1, y0+1)
    set(x0-1, y0+2)
    set(x0, y0+2)
    set(x0+1, y0+2)
  }

  def cell(x: Int, y: Int): Int = {
    if (wrapAround) {
      cells((y + height) % height)((x + width) % width)
    } else {
      if (x < 0 || y < 0 || x >= width || y >= height) 0
      else cells(y)(x)
    }
  }

  def neighbours(x: Int, y: Int): Int = {
    cell(x-1, y-1) + cell(x, y-1) + cell(x+1, y-1) +
      cell(x-1, y)                  + cell(x+1, y)   +
      cell(x-1, y+1) + cell(x, y+1) + cell(x+1, y+1)
  }

  def alive(x: Int, y: Int): Int = {
    val n = neighbours(x, y)
    if (cell(x, y) == 0 && n == 3) 1
    else if (cell(x, y) == 1 && (n == 2 || n == 3)) 1
    else 0
  }

  def step() = {
    cells =
      for (y <- 0 until height) yield {
        for (x <- 0 until width) yield {
          alive(x, y)
        }
      }
  }

  def randomize(n: Int): Unit = {
    val r = Random
    for (i <- 0 until n) {
      set(r.nextInt(width), r.nextInt(height))
    }
  }

  override def toString: String = cells map (line => ("" /: line)(_+_)) mkString "\n"

}

object GameOfLife extends App {

  Scurses { screen =>

    val (w, h) = screen.size
    val gol = GameOfLife(w / 2, h, wrapAround = true)
    gol.glider(4, 4)
    gol.glider(16, 6)
    gol.glider(28, 2)
    gol.randomize(w * h / 10)

    val palette = (34 until 39) ++ (39 until 219 by 36) ++ (219 until 214 by -1) ++ (214 until 24 by -36)
    var step = 0

    val timer = new Timer()

    timer.scheduleAtFixedRate(new TimerTask {
      override def run(): Unit = {
          gol.step()
          for (x <- 0 until w / 2; y <- 0 until h) {
            val c = palette((step + x / 8 + y / 4) % palette.length)
            if (gol.field(y)(x) == 1) {
              screen.put(x * 2, y, "(", c, 0)
              screen.put(x * 2 + 1, y, ")", c + 12, 0)
            } else {
              screen.put(x * 2, y, " ", 0, 0)
              screen.put(x * 2 + 1, y, " ", 0, 0)
            }
          }
          step = (step + 1) % palette.length
          screen.refresh()
      }
    }, 33, 33)

    screen.keypress()
    timer.cancel()
    Thread.sleep(250)
  }

}
