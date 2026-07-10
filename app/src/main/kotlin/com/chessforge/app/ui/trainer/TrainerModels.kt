package com.chessforge.app.ui.trainer

import com.chessforge.app.ui.components.SquareFeedback
import com.chessforge.core.BoardState
import com.chessforge.core.Square

/** Controls how much the trainer helps the learner find each move. */
enum class HintLevel(val label: String) {
    /** Glows the active piece and shows its destination from the start of every step - best for first memorizing a line. */
    GUIDED("Guided"),

    /** Glows the active piece so you know *what* to move, but you must recall *where* yourself. */
    PIECE_ONLY("Piece Only"),

    /** No highlighting at all - full recall, the way a professional would drill a line or solve a puzzle. */
    CHALLENGE("Master"),
}

/** Immutable snapshot the trainer screen renders every recomposition. */
data class TrainerUiState(
    val title: String,
    val subtitle: String,
    val board: BoardState,
    val stepIndex: Int,
    val totalSteps: Int,
    val activeSquare: Square?,
    val destinationSquare: Square?,
    val selectedSquare: Square?,
    val showPieceGlow: Boolean,
    val showDestinationHint: Boolean,
    val feedbackSquare: Square?,
    val feedback: SquareFeedback,
    val annotation: String?,
    val currentMoveSan: String?,
    val isAutoPlaying: Boolean,
    val isComplete: Boolean,
    val mistakeMade: Boolean,
)
