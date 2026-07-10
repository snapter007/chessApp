package com.chessforge.app.data

import com.chessforge.core.Move

enum class TacticTheme(val displayName: String) {
    FORK("Fork"),
    PIN("Pin"),
    SKEWER("Skewer"),
    DISCOVERED_ATTACK("Discovered Attack"),
    DOUBLE_ATTACK("Double Attack"),
    BACK_RANK("Back-Rank Mate"),
    SMOTHERED_MATE("Smothered Mate"),
    DEFLECTION("Deflection"),
    DECOY("Decoy"),
    REMOVING_THE_DEFENDER("Removing the Defender"),
    ZWISCHENZUG("Zwischenzug"),
}

enum class Difficulty { CLUB, ADVANCED, MASTER }

/**
 * A tactics puzzle starting from [startFen]. [moves] contains the full
 * winning combination (both sides' plies); the learner only ever plays the
 * side to move, and the opponent's replies are auto-played by the trainer.
 */
data class TacticPuzzle(
    val id: String,
    val title: String,
    val theme: TacticTheme,
    val difficulty: Difficulty,
    val startFen: String,
    val moves: List<Move>,
    val explanation: String,
)
