package com.chessforge.app.data

import com.chessforge.core.Move

enum class OpeningCategory { OPENING, TRAP }

/**
 * A curated, famous opening or trap line. [moves] is the full forced sequence
 * the learner will drill move-by-move: at each ply only the piece on
 * `moves[i].from` is active, and the only accepted destination is `moves[i].to`.
 */
data class Opening(
    val id: String,
    val name: String,
    val category: OpeningCategory,
    val ecoCode: String?,
    val summary: String,
    val moves: List<Move>,
)
