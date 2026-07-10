package com.chessforge.core

enum class PieceColor {
    WHITE, BLACK;

    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

enum class PieceType(val fenChar: Char) {
    PAWN('p'), KNIGHT('n'), BISHOP('b'), ROOK('r'), QUEEN('q'), KING('k')
}

data class Piece(val type: PieceType, val color: PieceColor) {
    /** FEN character: uppercase for white, lowercase for black. */
    val fenChar: Char
        get() = if (color == PieceColor.WHITE) type.fenChar.uppercaseChar() else type.fenChar

    companion object {
        fun fromFenChar(c: Char): Piece {
            val color = if (c.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
            val type = when (c.lowercaseChar()) {
                'p' -> PieceType.PAWN
                'n' -> PieceType.KNIGHT
                'b' -> PieceType.BISHOP
                'r' -> PieceType.ROOK
                'q' -> PieceType.QUEEN
                'k' -> PieceType.KING
                else -> throw IllegalArgumentException("Unknown FEN piece char: $c")
            }
            return Piece(type, color)
        }
    }
}
