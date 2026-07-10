package com.chessforge.app.ui.components

import androidx.compose.ui.graphics.Color
import com.chessforge.core.Piece
import com.chessforge.core.PieceColor
import com.chessforge.core.PieceType

/** Unicode chess glyph for a piece - avoids needing bundled image assets. */
fun Piece.glyph(): String = when (color) {
    PieceColor.WHITE -> when (type) {
        PieceType.KING -> "♔"
        PieceType.QUEEN -> "♕"
        PieceType.ROOK -> "♖"
        PieceType.BISHOP -> "♗"
        PieceType.KNIGHT -> "♘"
        PieceType.PAWN -> "♙"
    }
    PieceColor.BLACK -> when (type) {
        PieceType.KING -> "♚"
        PieceType.QUEEN -> "♛"
        PieceType.ROOK -> "♜"
        PieceType.BISHOP -> "♝"
        PieceType.KNIGHT -> "♞"
        PieceType.PAWN -> "♟"
    }
}

fun Piece.tint(): Color = if (color == PieceColor.WHITE) Color(0xFFF7F5F0) else Color(0xFF1A1A1A)
