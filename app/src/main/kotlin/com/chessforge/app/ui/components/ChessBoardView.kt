package com.chessforge.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessforge.app.ui.theme.BoardDark
import com.chessforge.app.ui.theme.BoardLight
import com.chessforge.app.ui.theme.CorrectGreen
import com.chessforge.app.ui.theme.Gold
import com.chessforge.app.ui.theme.WrongRed
import com.chessforge.core.BoardState
import com.chessforge.core.Square

enum class SquareFeedback { NONE, CORRECT, WRONG }

/**
 * Renders an 8x8 board (White at the bottom). Only [activeSquare] is ever
 * meant to be interactive per the trainer's rules - every other piece is
 * visually "frozen" (dimmed) so the learner's attention (and taps) are
 * locked onto the one piece they need to move this step.
 */
@Composable
fun ChessBoardView(
    board: BoardState,
    activeSquare: Square?,
    destinationSquare: Square?,
    selectedSquare: Square?,
    showPieceGlow: Boolean,
    showDestinationHint: Boolean,
    feedbackSquare: Square?,
    feedback: SquareFeedback,
    onTap: (Square) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val squareSize = maxWidth / 8

        Column(
            modifier = Modifier
                .aspectRatio(1f)
                .border(1.dp, Color(0x33FFFFFF)),
        ) {
            for (row in 0..7) {
                val rank = 7 - row
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (file in 0..7) {
                        val square = Square(file, rank)
                        val isLight = (file + rank) % 2 == 1
                        val piece = board.pieceAt(square)

                        val isActive = showPieceGlow && square == activeSquare
                        val isSelected = square == selectedSquare
                        val isDestinationMarker = square == destinationSquare && (showDestinationHint || selectedSquare == activeSquare)
                        val isFrozenPiece = piece != null && square != activeSquare && square != selectedSquare

                        BoardSquare(
                            size = squareSize,
                            isLight = isLight,
                            piece = piece,
                            isActive = isActive || isSelected,
                            isDestinationMarker = isDestinationMarker,
                            isFrozen = isFrozenPiece,
                            feedback = if (square == feedbackSquare) feedback else SquareFeedback.NONE,
                            onTap = { onTap(square) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardSquare(
    size: androidx.compose.ui.unit.Dp,
    isLight: Boolean,
    piece: com.chessforge.core.Piece?,
    isActive: Boolean,
    isDestinationMarker: Boolean,
    isFrozen: Boolean,
    feedback: SquareFeedback,
    onTap: () -> Unit,
) {
    val baseColor = if (isLight) BoardLight else BoardDark
    val overlay = when (feedback) {
        SquareFeedback.CORRECT -> CorrectGreen.copy(alpha = 0.55f)
        SquareFeedback.WRONG -> WrongRed.copy(alpha = 0.55f)
        SquareFeedback.NONE -> Color.Transparent
    }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "glowAlpha",
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(baseColor)
            .then(
                if (isActive) Modifier.background(Gold.copy(alpha = glowAlpha * 0.55f)) else Modifier,
            )
            .background(overlay)
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center,
    ) {
        if (isDestinationMarker && piece == null) {
            Box(
                modifier = Modifier
                    .size(size * 0.32f)
                    .background(Gold.copy(alpha = 0.7f), CircleShape),
            )
        }
        if (piece != null) {
            Text(
                text = piece.glyph(),
                color = piece.tint(),
                fontSize = with(androidx.compose.ui.platform.LocalDensity.current) { (size.toPx() * 0.62f).toSp() },
                fontWeight = FontWeight.Normal,
                modifier = Modifier.alpha(if (isFrozen) 0.45f else 1f),
            )
        }
        if (isDestinationMarker && piece != null && !isActive) {
            Box(
                modifier = Modifier
                    .size(size)
                    .border(3.dp, Gold.copy(alpha = 0.85f)),
            )
        }
    }
}
