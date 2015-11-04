package net.team2xh.onions.drawing

object Symbols {
  // 1. Box-drawing
  // 1.1 Edges
  // 1.1.1 Vertical
  val SV = "│"         // Single
  val DV = "║"         // Double
  val SV_TO_SL = "┤"   // Single to single left
  val SV_TO_DL = "╡"   // Single to double left
  val DV_TO_SL = "╢"   // Double to single left
  val DV_TO_DL = "╣"   // Double to double left
  val SV_TO_SR = "├"   // Single to single right
  val SV_TO_DR = "╞"   // Single to double right
  val DV_TO_SR = "╟"   // Double to double right
  val DV_TO_DR = "╠"   // Double to double right
  // 1.1.2 Horizontal
  val SH = "─"         // Single
  val DH = "═"         // Double
  val SH_TO_SU = "┴"   // Single to single up
  val SH_TO_DU = "╨"   // Single to double up
  val DH_TO_SU = "╧"   // Double to single up
  val DH_TO_DU = "╩"   // Double to double up
  val SH_TO_SD = "┬"   // Single to single down
  val SH_TO_DD = "╥"   // Single to double down
  val DH_TO_SD = "╤"   // Double to single down
  val DH_TO_DD = "╦"   // Double to double down
  // 1.2 Corners (parity is told clockwise)
  // 1.2.1 Top right corner
  val TRC_S_TO_S = "┐" // Single to single
  val TRC_S_TO_D = "╖" // Single to double
  val TRC_D_TO_S = "╕" // Double to single
  val TRC_D_TO_D = "╗" // Double to double
  // 1.2.2 Bottom right corner
  val BRC_S_TO_S = "┘" // Single to single
  val BRC_S_TO_D = "╛" // Single to double
  val BRC_D_TO_S = "╜" // Double to single
  val BRC_D_TO_D = "╝" // Double to double
  // 1.2.3 Bottom left corner
  val BLC_S_TO_S = "└" // Single to single
  val BLC_S_TO_D = "╙" // Single to double
  val BLC_D_TO_S = "╘" // Double to single
  val BLC_D_TO_D = "╚" // Double to double
  // 1.2.4 Top left corner
  val TLC_S_TO_S = "┌" // Single to single
  val TLC_S_TO_D = "╒" // Single to double
  val TLC_D_TO_S = "╓" // Double to single
  val TLC_D_TO_D = "╔" // Double to double
  // 1.3 Intersections
  val SH_X_SV = "┼"    // Single horizontal cross single vertical
  val SH_X_DV = "╫"    // Single horizontal cross double vertical
  val DH_X_SV = "╪"    // Double horizontal cross single vertical
  val DH_X_DV = "╬"    // Double horizontal cross double vertical
}
