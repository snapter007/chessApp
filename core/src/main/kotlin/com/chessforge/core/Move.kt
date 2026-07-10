package com.chessforge.core

enum class MoveFlag {
    NORMAL, DOUBLE_PAWN_PUSH, CASTLE_KINGSIDE, CASTLE_QUEENSIDE, EN_PASSANT, PROMOTION
}

/**
 * A single ply in a training sequence. [san] is the human-readable move that
 * is shown to the learner (pre-computed for the curated content so the core
 * module never needs a full SAN generator/legality engine).
 */
data class Move(
    val from: Square,
    val to: Square,
    val san: String,
    val flag: MoveFlag = MoveFlag.NORMAL,
    val promotion: PieceType? = null,
    val annotation: String? = null,
) {
    companion object {
        fun of(from: String, to: String, san: String, flag: MoveFlag = MoveFlag.NORMAL, promotion: PieceType? = null, annotation: String? = null): Move =
            Move(Square.of(from), Square.of(to), san, flag, promotion, annotation)
    }
}
