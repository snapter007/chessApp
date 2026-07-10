package com.chessforge.app.ui.trainer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessforge.app.data.ProgressStore
import com.chessforge.app.ui.components.SquareFeedback
import com.chessforge.core.BoardState
import com.chessforge.core.Move
import com.chessforge.core.PieceColor
import com.chessforge.core.Square
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Drives a single scripted move sequence (an opening/trap line, or a tactics
 * puzzle). At every step exactly one piece is "active": the only square the
 * learner may interact with productively. Every other piece on the board is
 * frozen - tapping it is a no-op (beyond a brief "wrong" flash).
 *
 * @param autoPlayOpponent when true (tactics puzzles), plies played by the
 * side that is *not* to move in [startFen] are auto-played by the trainer so
 * the learner only ever has to find their own side's moves. When false
 * (openings/traps), every ply in the line requires the learner's input -
 * the whole point is to rehearse the complete sequence move by move.
 */
class TrainerViewModel(
    private val storageKey: String,
    private val title: String,
    private val subtitle: String,
    startFen: String,
    private val moves: List<Move>,
    initialHintLevel: HintLevel,
    private val autoPlayOpponent: Boolean,
    private val progressStore: ProgressStore?,
) : ViewModel() {

    private val solverColor: PieceColor = BoardState.fromFen(startFen).sideToMove
    private var board: BoardState = BoardState.fromFen(startFen)
    private var stepIndex = 0
    private var selectedSquare: Square? = null
    private var mistakeMade = false
    private var hintRevealedThisStep = false
    private var feedbackSquare: Square? = null
    private var feedback: SquareFeedback = SquareFeedback.NONE
    private var isAutoPlaying = false

    var hintLevel by mutableStateOf(initialHintLevel)
        private set

    var uiState by mutableStateOf(buildState())
        private set

    init {
        continueAutoPlayIfNeeded()
    }

    fun setHintLevel(level: HintLevel) {
        hintLevel = level
        hintRevealedThisStep = false
        refresh()
    }

    fun requestHint() {
        hintRevealedThisStep = true
        refresh()
    }

    fun restart() {
        board = BoardState.fromFen(boardStartFenCache)
        stepIndex = 0
        selectedSquare = null
        mistakeMade = false
        hintRevealedThisStep = false
        feedbackSquare = null
        feedback = SquareFeedback.NONE
        refresh()
        continueAutoPlayIfNeeded()
    }

    private val boardStartFenCache: String = startFen

    fun onSquareTap(square: Square) {
        if (uiState.isComplete || isAutoPlaying || stepIndex >= moves.size) return
        val move = moves[stepIndex]
        val selected = selectedSquare

        if (selected == null) {
            if (square == move.from) {
                selectedSquare = square
                refresh()
            } else {
                flashWrong(square)
            }
        } else {
            when (square) {
                move.to -> commitMove(move)
                selected -> {
                    selectedSquare = null
                    refresh()
                }
                else -> {
                    mistakeMade = true
                    selectedSquare = null
                    flashWrong(square)
                }
            }
        }
    }

    private fun flashWrong(square: Square) {
        mistakeMade = true
        feedbackSquare = square
        feedback = SquareFeedback.WRONG
        refresh()
        viewModelScope.launch {
            delay(450)
            feedbackSquare = null
            feedback = SquareFeedback.NONE
            refresh()
        }
    }

    private fun commitMove(move: Move) {
        board = board.applyMove(move)
        stepIndex += 1
        selectedSquare = null
        hintRevealedThisStep = false
        feedbackSquare = move.to
        feedback = SquareFeedback.CORRECT
        refresh()
        viewModelScope.launch {
            delay(500)
            feedbackSquare = null
            feedback = SquareFeedback.NONE
            refresh()
            continueAutoPlayIfNeeded()
        }
    }

    /** Auto-plays any consecutive plies belonging to the non-learner side (tactics mode only). */
    private fun continueAutoPlayIfNeeded() {
        if (!autoPlayOpponent) {
            checkCompletion()
            return
        }
        if (stepIndex >= moves.size || board.sideToMove == solverColor) {
            checkCompletion()
            return
        }
        isAutoPlaying = true
        refresh()
        viewModelScope.launch {
            delay(650)
            val move = moves[stepIndex]
            board = board.applyMove(move)
            stepIndex += 1
            isAutoPlaying = false
            refresh()
            continueAutoPlayIfNeeded()
        }
    }

    private fun checkCompletion() {
        if (stepIndex >= moves.size) {
            progressStore?.let { store ->
                viewModelScope.launch {
                    store.markCompleted(storageKey)
                    if (!mistakeMade) store.markMastered(storageKey)
                }
            }
        }
        refresh()
    }

    private fun refresh() {
        uiState = buildState()
    }

    private fun buildState(): TrainerUiState {
        val isComplete = stepIndex >= moves.size
        val currentMove = moves.getOrNull(stepIndex)
        val showGlow = when (hintLevel) {
            HintLevel.GUIDED, HintLevel.PIECE_ONLY -> true
            HintLevel.CHALLENGE -> hintRevealedThisStep
        }
        val showDestinationImmediately = when (hintLevel) {
            HintLevel.GUIDED -> true
            HintLevel.PIECE_ONLY -> hintRevealedThisStep
            HintLevel.CHALLENGE -> hintRevealedThisStep
        }
        val annotation = when {
            isComplete -> "Line complete! Every move played correctly is a step closer to instinct."
            stepIndex == 0 -> subtitle
            else -> moves.getOrNull(stepIndex - 1)?.annotation
        }
        return TrainerUiState(
            title = title,
            subtitle = subtitle,
            board = board,
            stepIndex = stepIndex,
            totalSteps = moves.size,
            activeSquare = if (isAutoPlaying) null else currentMove?.from,
            destinationSquare = currentMove?.to,
            selectedSquare = selectedSquare,
            showPieceGlow = showGlow && !isAutoPlaying,
            showDestinationHint = showDestinationImmediately && !isAutoPlaying,
            feedbackSquare = feedbackSquare,
            feedback = feedback,
            annotation = annotation,
            currentMoveSan = currentMove?.san,
            isAutoPlaying = isAutoPlaying,
            isComplete = isComplete,
            mistakeMade = mistakeMade,
        )
    }
}
