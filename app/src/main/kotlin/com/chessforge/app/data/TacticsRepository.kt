package com.chessforge.app.data

import com.chessforge.core.Move
import com.chessforge.core.MoveFlag
import com.chessforge.app.data.TacticTheme.*
import com.chessforge.app.data.Difficulty.*

private fun m(from: String, to: String, san: String, note: String? = null) =
    Move.of(from, to, san, MoveFlag.NORMAL, annotation = note)

/**
 * Constructed training puzzles (not claimed to be from specific master games)
 * that isolate one tactical motif each, verified to replay correctly move by
 * move against the core chess engine.
 */
object TacticsRepository {

    val all: List<TacticPuzzle> = listOf(
        TacticPuzzle(
            id = "fork-royal-family",
            title = "Royal Fork",
            theme = FORK,
            difficulty = CLUB,
            startFen = "3q3k/8/8/4N3/8/8/8/6K1 w - - 0 1",
            moves = listOf(
                m("e5", "f7", "Nf7+", "Forks the king on h8 and the queen on d8 at the same time - and it's check!"),
                m("h8", "g8", "Kg8", "Forced - the king has to step out of check."),
                m("f7", "d8", "Nxd8", "The fork pays off - White wins the queen."),
            ),
            explanation = "A knight fork is devastating because the knight attacks two squares that no other piece can defend simultaneously. Always scan for knight jumps that hit two targets at once.",
        ),
        TacticPuzzle(
            id = "pin-and-win-pawn",
            title = "The Pin That Isn't a Defense",
            theme = PIN,
            difficulty = CLUB,
            startFen = "4k3/8/2n5/1B2p3/8/5N2/8/6K1 w - - 0 1",
            moves = listOf(
                m("f3", "e5", "Nxe5", "The knight on c6 is absolutely pinned to the king by the bishop on b5 - it can't legally recapture. The e5 pawn is only defended on paper."),
            ),
            explanation = "An absolute pin (to the king) means the pinned piece cannot move at all, no matter what it appears to defend. Always check whether a 'defender' is actually pinned before assuming it can recapture.",
        ),
        TacticPuzzle(
            id = "rank-skewer",
            title = "Rank Skewer",
            theme = SKEWER,
            difficulty = ADVANCED,
            startFen = "8/8/8/8/q3k3/8/8/6KR w - - 0 1",
            moves = listOf(
                m("h1", "h4", "Rh4+", "Skewers the king in front of the queen along the 4th rank - the king must move, then the queen falls."),
                m("e4", "e5", "Ke5", "The king has to step off the rank."),
                m("h4", "a4", "Rxa4", "The skewer cashes in - White wins the queen."),
            ),
            explanation = "Unlike a pin, in a skewer the more valuable piece is in front and forced to move, exposing the less valuable piece behind it to capture.",
        ),
        TacticPuzzle(
            id = "discovered-check-deflection",
            title = "Discovered Check Wins the Queen",
            theme = DISCOVERED_ATTACK,
            difficulty = MASTER,
            startFen = "3q2k1/8/8/3N2P1/8/8/8/3R2K1 w - - 0 1",
            moves = listOf(
                m("d5", "f6", "Nf6+", "Discovered check! Moving the knight uncovers the rook's attack on d8 while giving check itself - Black cannot deal with both threats."),
                m("g8", "h8", "Kh8", "Forced - the king must step out of check (capturing on f6 loses the queen to gxf6)."),
                m("d1", "d8", "Rxd8", "The discovered attack cashes in - White wins the queen for free."),
            ),
            explanation = "A discovered attack unleashed by a check is one of the most powerful tactical devices in chess: the opponent must answer the check immediately, giving you a free tempo to collect the piece behind it.",
        ),
        TacticPuzzle(
            id = "queen-fork-rank",
            title = "Double Attack Along the Rank",
            theme = DOUBLE_ATTACK,
            difficulty = ADVANCED,
            startFen = "6k1/8/r3n3/8/8/8/8/1Q4K1 w - - 0 1",
            moves = listOf(
                m("b1", "b6", "Qb6", "Forks the rook on a6 and the knight on e6 along the same rank - both are undefended and Black can only save one."),
                m("a6", "a8", "Ra8", "Saves the rook, but abandons the knight."),
                m("b6", "e6", "Qxe6", "Collects the knight - the point of the double attack."),
            ),
            explanation = "A queen's power comes from combining rook and bishop moves - always check whether a central square lets your queen hit two undefended targets at once.",
        ),
        TacticPuzzle(
            id = "back-rank-basic",
            title = "Back-Rank Mate",
            theme = BACK_RANK,
            difficulty = CLUB,
            startFen = "6k1/5ppp/8/8/8/8/8/3R2K1 w - - 0 1",
            moves = listOf(
                m("d1", "d8", "Rd8#", "Checkmate! Black's own pawns block every escape square on the back rank - a textbook back-rank mate."),
            ),
            explanation = "Castled kings are often permanently weak on the back rank because their own pawns block every escape square. Always ask: if I check on the back rank, can the king actually run?",
        ),
        TacticPuzzle(
            id = "philidors-legacy",
            title = "Philidor's Legacy (Smothered Mate)",
            theme = SMOTHERED_MATE,
            difficulty = MASTER,
            startFen = "5r1k/6pp/7N/3Q4/8/8/8/K7 w - - 0 1",
            moves = listOf(
                m("d5", "g8", "Qg8+", "The queen sacrifice - Rxg8 is forced since the king is smothered by its own pawns and can't capture (the queen is guarded by the knight)."),
                m("f8", "g8", "Rxg8", "Black must recapture with the rook - it's the only legal response."),
                m("h6", "f7", "Nf7#", "Checkmate! The classic smothered mate - the king is completely boxed in by its own pieces."),
            ),
            explanation = "One of the oldest known combinations, named for 18th-century master François-André Danican Philidor: sacrifice the queen to force the king into a mating net a lone knight can finish.",
        ),
        TacticPuzzle(
            id = "overloaded-rook",
            title = "The Overloaded Defender",
            theme = DEFLECTION,
            difficulty = MASTER,
            startFen = "3r2k1/5ppp/8/3q4/8/8/8/K2RR3 w - - 0 1",
            moves = listOf(
                m("d1", "d5", "Rxd5", "Attacks the queen, which is defended by the rook on d8 - but that rook is overloaded: it's also the only thing guarding the back rank."),
                m("d8", "d5", "Rxd5", "Black must recapture, but now the back rank is completely undefended."),
                m("e1", "e8", "Re8#", "Checkmate! Deflecting the rook to win the queen also cleared the way for mate."),
            ),
            explanation = "When a single piece is defending two things at once, it's overloaded - attack the thing it's least willing to give up, and the other one falls.",
        ),
        TacticPuzzle(
            id = "decoy-to-g8",
            title = "Decoy Sacrifice",
            theme = DECOY,
            difficulty = MASTER,
            startFen = "2r4k/6pp/2N5/3Q4/8/8/8/K7 w - - 0 1",
            moves = listOf(
                m("d5", "g8", "Qg8+", "The decoy sacrifice - the queen isn't even defended, but the king has no other legal move since g7 and h7 are blocked by its own pawns."),
                m("h8", "g8", "Kxg8", "Forced - this is the only legal reply, luring the king onto g8."),
                m("c6", "e7", "Ne7+", "The point of the decoy! The knight forks the king on its new square and the rook on c8, winning it back with interest."),
            ),
            explanation = "A decoy lures a piece (often the king) onto a specific square where it becomes vulnerable to a follow-up tactic. The sacrifice itself doesn't need to look dangerous - it's the square that matters.",
        ),
        TacticPuzzle(
            id = "remove-the-guard",
            title = "Removing the Defender",
            theme = REMOVING_THE_DEFENDER,
            difficulty = ADVANCED,
            startFen = "4k3/1p6/2n5/1B2p3/8/5N2/8/6K1 w - - 0 1",
            moves = listOf(
                m("b5", "c6", "Bxc6", "Removes the only defender of the e5 pawn."),
                m("b7", "c6", "bxc6", "Black recaptures to restore material balance - but e5 is now undefended."),
                m("f3", "e5", "Nxe5", "Removing the defender pays off - White nets an extra pawn."),
            ),
            explanation = "Before attacking a well-defended target directly, ask whether you can eliminate its defender first - even at the cost of some material, if what's left behind is worth more.",
        ),
        TacticPuzzle(
            id = "zwischenzug-check",
            title = "Zwischenzug: The In-Between Move",
            theme = ZWISCHENZUG,
            difficulty = MASTER,
            startFen = "3r4/6k1/5n2/4P3/8/8/8/K2R4 w - - 0 1",
            moves = listOf(
                m("e5", "f6", "exf6+", "Instead of simply trading rooks on the d-file, White inserts a zwischenzug - an in-between capture that comes with check and wins a whole knight."),
                m("g7", "f6", "Kxf6", "Forced - the king must deal with the check before anything else can happen."),
            ),
            explanation = "A zwischenzug ('in-between move') interrupts an expected sequence of moves with a forcing check or threat, banking a gain before returning to the original business.",
        ),
        TacticPuzzle(
            id = "knight-fork-from-distance",
            title = "Fork From Afar",
            theme = FORK,
            difficulty = ADVANCED,
            startFen = "6k1/3q4/8/3N4/8/8/8/K7 w - - 0 1",
            moves = listOf(
                m("d5", "f6", "Nf6+", "Forks the king and queen from a distance - Black must move the king, then White wins the queen."),
                m("g8", "h8", "Kh8", "Forced - the king steps aside."),
                m("f6", "d7", "Nxd7", "Collects the queen - the point of the fork."),
            ),
            explanation = "Knight forks are the hardest to spot because the knight's move pattern is the least intuitive - practice visualizing its jump from several squares away, not just adjacent ones.",
        ),
        TacticPuzzle(
            id = "diagonal-skewer",
            title = "Diagonal Skewer",
            theme = SKEWER,
            difficulty = MASTER,
            startFen = "7q/8/8/8/3k1BN1/8/8/K7 w - - 0 1",
            moves = listOf(
                m("f4", "e5", "Be5+", "Skewers the king and queen along the same diagonal - the king can't capture the bishop since it's guarded by the knight."),
                m("d4", "c5", "Kc5", "The king must step off the diagonal."),
                m("e5", "h8", "Bxh8", "The skewer cashes in - White wins the queen."),
            ),
            explanation = "Skewers work on any line - rank, file, or diagonal. Always check whether a checking piece has a bigger prize lined up directly behind the king.",
        ),
    )

    fun byId(id: String): TacticPuzzle? = all.find { it.id == id }
}
