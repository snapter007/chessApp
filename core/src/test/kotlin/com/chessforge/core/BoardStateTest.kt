package com.chessforge.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BoardStateTest {

    @Test
    fun `initial position matches standard FEN`() {
        val board = BoardState.initial()
        assertEquals(
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            board.toFen(),
        )
    }

    @Test
    fun `pawn double push sets en passant target`() {
        val board = BoardState.initial()
        val next = board.applyMove(Move.of("e2", "e4", "e4", MoveFlag.DOUBLE_PAWN_PUSH))
        assertEquals(Square.of("e3"), next.enPassantTarget)
        assertEquals(PieceColor.BLACK, next.sideToMove)
        assertEquals(Piece(PieceType.PAWN, PieceColor.WHITE), next.pieceAt(Square.of("e4")))
        assertNull(next.pieceAt(Square.of("e2")))
    }

    @Test
    fun `en passant capture removes the pawn behind the target square`() {
        // 1. e4 c5 2. e5 d5 3. exd6 e.p.
        var board = BoardState.initial()
        board = board.applyMove(Move.of("e2", "e4", "e4", MoveFlag.DOUBLE_PAWN_PUSH))
        board = board.applyMove(Move.of("c7", "c5", "c5", MoveFlag.DOUBLE_PAWN_PUSH))
        board = board.applyMove(Move.of("e4", "e5", "e5"))
        board = board.applyMove(Move.of("d7", "d5", "d5", MoveFlag.DOUBLE_PAWN_PUSH))
        board = board.applyMove(Move.of("e5", "d6", "exd6", MoveFlag.EN_PASSANT))

        assertEquals(Piece(PieceType.PAWN, PieceColor.WHITE), board.pieceAt(Square.of("d6")))
        assertNull(board.pieceAt(Square.of("d5")))
        assertNull(board.pieceAt(Square.of("e5")))
    }

    @Test
    fun `kingside castling moves both king and rook`() {
        val fen = "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/2N2N2/PPPP1PPP/R1BQK2R w KQkq - 4 4"
        val board = BoardState.fromFen(fen)
        val next = board.applyMove(Move.of("e1", "g1", "O-O", MoveFlag.CASTLE_KINGSIDE))

        assertEquals(Piece(PieceType.KING, PieceColor.WHITE), next.pieceAt(Square.of("g1")))
        assertEquals(Piece(PieceType.ROOK, PieceColor.WHITE), next.pieceAt(Square.of("f1")))
        assertNull(next.pieceAt(Square.of("e1")))
        assertNull(next.pieceAt(Square.of("h1")))
        assertEquals(false, next.castlingRights.whiteKingside)
        assertEquals(false, next.castlingRights.whiteQueenside)
    }

    @Test
    fun `promotion replaces the pawn with the chosen piece`() {
        val board = BoardState.fromFen("8/4P3/8/8/8/8/4k3/4K3 w - - 0 1")
        val next = board.applyMove(Move.of("e7", "e8", "e8=Q", MoveFlag.PROMOTION, PieceType.QUEEN))
        assertEquals(Piece(PieceType.QUEEN, PieceColor.WHITE), next.pieceAt(Square.of("e8")))
        assertNull(next.pieceAt(Square.of("e7")))
    }

    @Test
    fun `capture replaces the piece on the destination square`() {
        val board = BoardState.fromFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2")
        val next = board.applyMove(Move.of("e4", "d5", "exd5"))
        assertEquals(Piece(PieceType.PAWN, PieceColor.WHITE), next.pieceAt(Square.of("d5")))
        assertNull(next.pieceAt(Square.of("e4")))
    }

    @Test
    fun `fen round trips through parsing and serialization`() {
        val fen = "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"
        assertEquals(fen, BoardState.fromFen(fen).toFen())
    }
}
