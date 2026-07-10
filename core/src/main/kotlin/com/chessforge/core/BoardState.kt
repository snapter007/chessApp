package com.chessforge.core

/**
 * Immutable snapshot of a chess position. This is intentionally *not* a full
 * legal-move engine: the app only ever needs to display a position and apply
 * moves that are already known (from curated openings/traps/tactics), so
 * there is no move generation or check detection here - only enough state
 * to render a board and apply a given [Move] correctly (captures, castling,
 * en passant, promotion).
 */
class BoardState private constructor(
    private val squares: Array<Piece?>,
    val sideToMove: PieceColor,
    val castlingRights: CastlingRights,
    val enPassantTarget: Square?,
    val fullmoveNumber: Int,
    val halfmoveClock: Int = 0,
) {
    fun pieceAt(square: Square): Piece? = squares[index(square)]

    fun applyMove(move: Move): BoardState {
        val next = squares.copyOf()
        val moving = next[index(move.from)]
            ?: throw IllegalStateException("No piece on ${move.from} to move")
        val isCapture = next[index(move.to)] != null || move.flag == MoveFlag.EN_PASSANT

        // En passant capture removes the pawn behind the destination square.
        if (move.flag == MoveFlag.EN_PASSANT) {
            val capturedPawnSquare = Square(move.to.file, move.from.rank)
            next[index(capturedPawnSquare)] = null
        }

        next[index(move.from)] = null
        next[index(move.to)] = if (move.flag == MoveFlag.PROMOTION && move.promotion != null) {
            Piece(move.promotion, moving.color)
        } else {
            moving
        }

        // Castling also relocates the rook.
        when (move.flag) {
            MoveFlag.CASTLE_KINGSIDE -> {
                val rank = move.from.rank
                val rookFrom = Square(7, rank)
                val rookTo = Square(5, rank)
                next[index(rookTo)] = next[index(rookFrom)]
                next[index(rookFrom)] = null
            }
            MoveFlag.CASTLE_QUEENSIDE -> {
                val rank = move.from.rank
                val rookFrom = Square(0, rank)
                val rookTo = Square(3, rank)
                next[index(rookTo)] = next[index(rookFrom)]
                next[index(rookFrom)] = null
            }
            else -> Unit
        }

        val nextEnPassant = if (move.flag == MoveFlag.DOUBLE_PAWN_PUSH) {
            Square(move.from.file, (move.from.rank + move.to.rank) / 2)
        } else {
            null
        }

        val nextCastlingRights = castlingRights.after(move, moving)
        val nextFullmove = if (sideToMove == PieceColor.BLACK) fullmoveNumber + 1 else fullmoveNumber
        val nextHalfmove = if (isCapture || moving.type == PieceType.PAWN) 0 else halfmoveClock + 1

        return BoardState(next, sideToMove.opposite(), nextCastlingRights, nextEnPassant, nextFullmove, nextHalfmove)
    }

    fun toFen(): String {
        val rows = (7 downTo 0).joinToString("/") { rank ->
            val sb = StringBuilder()
            var empty = 0
            for (file in 0..7) {
                val p = squares[index(Square(file, rank))]
                if (p == null) {
                    empty++
                } else {
                    if (empty > 0) {
                        sb.append(empty)
                        empty = 0
                    }
                    sb.append(p.fenChar)
                }
            }
            if (empty > 0) sb.append(empty)
            sb.toString()
        }
        val active = if (sideToMove == PieceColor.WHITE) "w" else "b"
        val castle = castlingRights.toFenField()
        val ep = enPassantTarget?.algebraic ?: "-"
        return "$rows $active $castle $ep $halfmoveClock $fullmoveNumber"
    }

    companion object {
        private fun index(square: Square) = square.rank * 8 + square.file

        fun initial(): BoardState = fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")

        fun fromFen(fen: String): BoardState {
            val parts = fen.trim().split(" ")
            require(parts.size >= 4) { "Invalid FEN: $fen" }
            val squares = arrayOfNulls<Piece>(64)
            val ranks = parts[0].split("/")
            require(ranks.size == 8) { "Invalid FEN board: ${parts[0]}" }
            for (r in 0..7) {
                val rank = 7 - r
                var file = 0
                for (c in ranks[r]) {
                    if (c.isDigit()) {
                        file += c - '0'
                    } else {
                        squares[rank * 8 + file] = Piece.fromFenChar(c)
                        file++
                    }
                }
            }
            val sideToMove = if (parts[1] == "w") PieceColor.WHITE else PieceColor.BLACK
            val castling = CastlingRights.fromFenField(parts[2])
            val enPassant = if (parts[3] == "-") null else Square.of(parts[3])
            val halfmove = parts.getOrNull(4)?.toIntOrNull() ?: 0
            val fullmove = parts.getOrNull(5)?.toIntOrNull() ?: 1
            return BoardState(squares, sideToMove, castling, enPassant, fullmove, halfmove)
        }
    }
}

data class CastlingRights(
    val whiteKingside: Boolean,
    val whiteQueenside: Boolean,
    val blackKingside: Boolean,
    val blackQueenside: Boolean,
) {
    fun toFenField(): String {
        val sb = StringBuilder()
        if (whiteKingside) sb.append('K')
        if (whiteQueenside) sb.append('Q')
        if (blackKingside) sb.append('k')
        if (blackQueenside) sb.append('q')
        return if (sb.isEmpty()) "-" else sb.toString()
    }

    fun after(move: Move, moving: Piece): CastlingRights {
        var wk = whiteKingside
        var wq = whiteQueenside
        var bk = blackKingside
        var bq = blackQueenside
        if (moving.type == PieceType.KING) {
            if (moving.color == PieceColor.WHITE) { wk = false; wq = false } else { bk = false; bq = false }
        }
        fun clearIfInvolved(square: Square) {
            when (square) {
                Square(0, 0) -> wq = false
                Square(7, 0) -> wk = false
                Square(0, 7) -> bq = false
                Square(7, 7) -> bk = false
                else -> Unit
            }
        }
        clearIfInvolved(move.from)
        clearIfInvolved(move.to)
        return CastlingRights(wk, wq, bk, bq)
    }

    companion object {
        fun fromFenField(field: String): CastlingRights = CastlingRights(
            whiteKingside = field.contains('K'),
            whiteQueenside = field.contains('Q'),
            blackKingside = field.contains('k'),
            blackQueenside = field.contains('q'),
        )
    }
}
