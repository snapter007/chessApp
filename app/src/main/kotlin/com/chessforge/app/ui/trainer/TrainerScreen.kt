package com.chessforge.app.ui.trainer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.chessforge.app.data.OpeningsRepository
import com.chessforge.app.data.ProgressStore
import com.chessforge.app.data.TacticsRepository
import com.chessforge.app.ui.components.ChessBoardView
import com.chessforge.app.ui.theme.CorrectGreen
import com.chessforge.app.ui.theme.Gold
import com.chessforge.app.ui.theme.OnSurfaceMuted
import com.chessforge.app.ui.theme.Surface

enum class TrainerContentType { OPENING, TACTIC }

@Composable
fun TrainerScreen(
    contentType: TrainerContentType,
    itemId: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val progressStore = remember(context) { ProgressStore(context) }

    val factory = remember(contentType, itemId) {
        viewModelFactory {
            initializer {
                when (contentType) {
                    TrainerContentType.OPENING -> {
                        val opening = OpeningsRepository.byId(itemId)!!
                        TrainerViewModel(
                            storageKey = "opening:${opening.id}",
                            title = opening.name,
                            subtitle = opening.summary,
                            startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                            moves = opening.moves,
                            initialHintLevel = HintLevel.GUIDED,
                            autoPlayOpponent = false,
                            progressStore = progressStore,
                        )
                    }
                    TrainerContentType.TACTIC -> {
                        val puzzle = TacticsRepository.byId(itemId)!!
                        TrainerViewModel(
                            storageKey = "tactic:${puzzle.id}",
                            title = puzzle.title,
                            subtitle = puzzle.explanation,
                            startFen = puzzle.startFen,
                            moves = puzzle.moves,
                            initialHintLevel = HintLevel.CHALLENGE,
                            autoPlayOpponent = true,
                            progressStore = progressStore,
                        )
                    }
                }
            }
        }
    }

    val viewModel: TrainerViewModel = viewModel(key = "$contentType:$itemId", factory = factory)
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = Gold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = state.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (state.isComplete) "Complete" else "Step ${state.stepIndex + 1} of ${state.totalSteps}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceMuted,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HintLevelSelector(current = viewModel.hintLevel, onSelect = viewModel::selectHintLevel)

        Spacer(modifier = Modifier.height(12.dp))

        ChessBoardView(
            board = state.board,
            activeSquare = state.activeSquare,
            destinationSquare = state.destinationSquare,
            selectedSquare = state.selectedSquare,
            showPieceGlow = state.showPieceGlow,
            showDestinationHint = state.showDestinationHint,
            feedbackSquare = state.feedbackSquare,
            feedback = state.feedback,
            onTap = viewModel::onSquareTap,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            if (!state.isComplete && !state.isAutoPlaying) {
                Text(
                    text = if (state.showDestinationHint) {
                        state.currentMoveSan?.let { "Find: $it" } ?: ""
                    } else {
                        "Find the best move for this position."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = state.annotation ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceMuted,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (viewModel.hintLevel == HintLevel.CHALLENGE && !state.isComplete && !state.showPieceGlow) {
                OutlinedButton(onClick = viewModel::requestHint) {
                    Text("Hint")
                }
            }
            OutlinedButton(onClick = viewModel::restart) {
                Text("Restart")
            }
            if (state.isComplete) {
                Button(onClick = onBack) {
                    Text(if (state.mistakeMade) "Done - Practice again later" else "Mastered! Back to list")
                }
            }
        }
    }
}

@Composable
private fun HintLevelSelector(current: HintLevel, onSelect: (HintLevel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(10.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        HintLevel.entries.forEach { level ->
            val selected = level == current
            Text(
                text = level.label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) androidx.compose.ui.graphics.Color.Black else OnSurfaceMuted,
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) Gold else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(8.dp),
                    )
                    .clickable { onSelect(level) }
                    .padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
