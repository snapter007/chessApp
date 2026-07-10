package com.chessforge.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.chessforge.app.data.OpeningCategory
import com.chessforge.app.data.OpeningsRepository
import com.chessforge.app.data.ProgressStore
import com.chessforge.app.data.TacticsRepository
import com.chessforge.app.ui.theme.Gold
import com.chessforge.app.ui.theme.OnSurfaceMuted
import com.chessforge.app.ui.theme.Surface
import kotlinx.coroutines.flow.map

@Composable
fun HomeScreen(
    onOpenOpenings: () -> Unit,
    onOpenTraps: () -> Unit,
    onOpenTactics: () -> Unit,
) {
    val context = LocalContext.current
    val progressStore = remember(context) { ProgressStore(context) }

    val openings = remember { OpeningsRepository.all.filter { it.category == OpeningCategory.OPENING } }
    val traps = remember { OpeningsRepository.all.filter { it.category == OpeningCategory.TRAP } }
    val tactics = remember { TacticsRepository.all }

    val masteredOpenings by progressStore.masteredIds
        .map { ids -> openings.count { ids.contains("opening:${it.id}") } }
        .collectAsState(initial = 0)
    val masteredTraps by progressStore.masteredIds
        .map { ids -> traps.count { ids.contains("opening:${it.id}") } }
        .collectAsState(initial = 0)
    val masteredTactics by progressStore.masteredIds
        .map { ids -> tactics.count { ids.contains("tactic:${it.id}") } }
        .collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "ChessForge",
            style = MaterialTheme.typography.headlineMedium,
            color = Gold,
        )
        Text(
            text = "Memorize famous openings, traps, and professional-level tactics - one move at a time.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceMuted,
        )

        HomeCategoryCard(
            title = "Openings",
            description = "${openings.size} main-line openings - repertoire building blocks.",
            progressText = "$masteredOpenings / ${openings.size} mastered",
            onClick = onOpenOpenings,
        )
        HomeCategoryCard(
            title = "Traps",
            description = "${traps.size} famous traps - punish careless play instantly.",
            progressText = "$masteredTraps / ${traps.size} mastered",
            onClick = onOpenTraps,
        )
        HomeCategoryCard(
            title = "Tactics",
            description = "${tactics.size} professional-level puzzles across every major motif.",
            progressText = "$masteredTactics / ${tactics.size} mastered",
            onClick = onOpenTactics,
        )
    }
}

@Composable
private fun HomeCategoryCard(
    title: String,
    description: String,
    progressText: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
            Text(text = progressText, style = MaterialTheme.typography.labelLarge, color = Gold)
        }
    }
}
