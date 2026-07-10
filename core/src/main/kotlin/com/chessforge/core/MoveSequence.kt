package com.chessforge.core

/**
 * A fixed sequence of plies starting from [startFen], used to drive the
 * step-by-step trainer: at each index the "active" piece the learner must
 * move is the piece on `moves[index].from`, and the only legal destination
 * for that piece is `moves[index].to`. All other pieces are frozen for the
 * purposes of the trainer UI.
 */
data class MoveSequence(
    val startFen: String,
    val moves: List<Move>,
) {
    fun startingBoard(): BoardState = BoardState.fromFen(startFen)

    /** The board position after applying moves[0..index) (i.e. before playing moves[index]). */
    fun boardBeforeStep(index: Int): BoardState {
        require(index in 0..moves.size) { "Step index out of range: $index" }
        var board = startingBoard()
        for (i in 0 until index) {
            board = board.applyMove(moves[i])
        }
        return board
    }
}
