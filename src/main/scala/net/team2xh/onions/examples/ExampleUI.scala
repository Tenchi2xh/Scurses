package net.team2xh.onions.examples

import java.util.{TimerTask, Timer}

import net.team2xh.onions.{Palettes, Themes}
import net.team2xh.onions.components.Frame
import net.team2xh.onions.components.widgets._
import net.team2xh.onions.utils.{Varying, Lorem, TextWrap}
import net.team2xh.scurses.{Colors, Scurses}
import net.team2xh.scurses.RichText._

import scala.util.Random

object ExampleUI extends App {

  val clockTimer = new Timer()
  val r = Random

  Scurses { implicit screen =>
    implicit val debug = true
    val frame = Frame(title = Some("Example Onions UI - Powered by Scurses"),
                      debug = true, theme = Themes.default)

    val colA = frame.panel
    val colB = colA.splitRight
    val colC = colB.splitRight

    val colB2 = colB.splitDown
    val colB3 = colB2.splitDown
    val colB3B = colB3.splitRight
    val colB3B2 = colB3B.splitDown
    val colC2 = colC.splitDown

    colA.title = "Labels"
    Label(colA, "Left-aligned text: ")
    Separator(colA)
    Label(colA, Lorem.Ipsum)
    Separator(colA)
    Label(colA, "Justified text:")
    Separator(colA)
    Label(colA, Lorem.Ipsum, TextWrap.JUSTIFY)
    Separator(colA)
    Label(colA, "Right-aligned text: ")
    Separator(colA)
    Label(colA, Lorem.Ipsum, TextWrap.ALIGN_RIGHT)
    Separator(colA)
    Label(colA, "Centered text: ")
    Separator(colA)
    Label(colA, Lorem.Ipsum, TextWrap.CENTER)
    Separator(colA)
    RichLabel(colA, r"[b]Supports[/b] [u]rich[/u] [fg:#3366cc]text[/fg]! [bl]nice[/bl] [b]Supports[/b] [u]rich[/u] [fg:#3366cc]text[/fg]! [bl]nice[/bl] [b]Supports[/b] [u]rich[/u] [fg:#3366cc]text[/fg]! [bl]nice[/bl] [b]Supports[/b] [u]rich[/u] [fg:#3366cc]text[/fg]! [bl]nice[/bl] [b]Supports[/b] [u]rich[/u] [fg:#3366cc]text[/fg]! [bl]nice[/bl] ")
//    RichLabel(colA, r"${Lorem.Ipsum}")

    colB.title = "Misc. widgets"
    Label(colB, "Enter your name here:")
    val input = Input(colB, "Name")
    val big = BigText(colB, Varying.from(input.text, "Your name", s => s"$s"))
    Separator(colB)
    val radio = Radio(colB, Seq("Default", "Red", "Green", "Blue"))
    radio.subscribe(() => {
      val color = radio.value match {
        case 0 => frame.theme.foreground
        case 1 => Colors.BRIGHT_RED
        case 2 => Colors.BRIGHT_GREEN
        case 3 => Colors.BRIGHT_BLUE
      }
      big.color := color
    })
    colB.addTab()
    BitMap(colB, "/src/main/scala/net/team2xh/onions/examples/logo.png", relative = true)
    colB.showTab(0)

    Label(colB3, "Choose a theme:")
    val radio2 = Radio(colB3, Seq("Default", "Light", "MS-DOS"))
    radio2.subscribe(() => {
      frame.theme = radio2.value match {
        case 0 => Themes.default
        case 1 => Themes.light
        case 2 => Themes.MSDOS
      }
    })

    colB3B.title = "7-segment"
    val ss = SevenSegment(colB3B, "00:00")

    colC.title = "Graphs A"
    val barValues = Seq(15, 11, 2, 20, 8, 7, 4)
    val bars = BarGraph(colC, barValues, labels = Lorem.Ipsum.split(' '),
                        palette = Palettes.rainbow, min = 0, max = 24)
    colC.addTab()
    val barValues2 = Seq(15, -11, -2, 20, -8, 7, 4,
                         10, 2, 13, -5, -8, 3, -15, 0)
    val bars2 = BarGraph(colC, barValues2, labels = Lorem.Ipsum.split(' '),
      palette = Palettes.default, min = -24, max = 24)
    colC.showTab(0)

    colB2.title = "Histogram"

    colC2.title = "Graphs B"
    val scatterValues = (1 to 50) map (i => {
      val x = r.nextInt(40)
      val y = 50 - x + (r.nextGaussian() * 5).toInt - 2
      (x, y max 0)
    })
    ScatterPlot(colC2, scatterValues, "Time", "Sales")
    colC2.addTab()
    val scatterValues2 = (-50 to 50) map (x => {
      val y = math.round(0.04*math.pow(x, 2)).toInt
      (x, y)
    })
    ScatterPlot(colC2, scatterValues2, "Price", "Popularity")
    colC2.showTab(0)

    clockTimer.scheduleAtFixedRate(new TimerTask {
      var s = 1
      override def run(): Unit = {
        val column = if (s % 2 == 0) ":" else " "
        ss.text := "%02d%s%02d".format(s / 60, column, s % 60)
        bars.values := barValues.map(n => n + r.nextInt(5) - 2)
        bars2.values := barValues2.map(n => n + r.nextInt(5) - 2)
        s += 1
      }
    }, 1000, 1000)
    frame.show()
  }

  clockTimer.cancel()
}
