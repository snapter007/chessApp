package com.chessforge.core

import org.junit.Assert.assertEquals
import org.junit.Test

class MoveSequenceTest {

    @Test
    fun `italian game reaches expected position after four plies`() {
        // 1. e4 e5 2. Nf3 Nc6 3. Bc4
        val sequence = MoveSequence(
            startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            moves = listOf(
                Move.of("e2", "e4", "e4", MoveFlag.DOUBLE_PAWN_PUSH),
                Move.of("e7", "e5", "e5", MoveFlag.DOUBLE_PAWN_PUSH),
                Move.of("g1", "f3", "Nf3"),
                Move.of("b8", "c6", "Nc6"),
                Move.of("f1", "c4", "Bc4"),
            ),
        )
        val finalBoard = sequence.boardBeforeStep(sequence.moves.size)
        assertEquals(
            "r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3",
            finalBoard.toFen(),
        )
    }

    @Test
    fun `tactics puzzle starts from custom fen`() {
        val sequence = MoveSequence(
            startFen = "6k1/5ppp/8/8/8/8/5PPP/3R2K1 w - - 0 1",
            moves = listOf(Move.of("d1", "d8", "Rd8#")),
        )
        val before = sequence.boardBeforeStep(0)
        assertEquals(Piece(PieceType.KING, PieceColor.BLACK), before.pieceAt(Square.of("g8")))
        val after = sequence.boardBeforeStep(1)
        assertEquals(Piece(PieceType.ROOK, PieceColor.WHITE), after.pieceAt(Square.of("d8")))
    }
}
