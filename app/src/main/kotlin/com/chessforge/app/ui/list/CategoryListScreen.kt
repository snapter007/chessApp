package com.chessforge.app.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chessforge.app.data.OpeningCategory
import com.chessforge.app.data.OpeningsRepository
import com.chessforge.app.data.ProgressStore
import com.chessforge.app.data.TacticsRepository
import com.chessforge.app.ui.theme.CorrectGreen
import com.chessforge.app.ui.theme.Gold
import com.chessforge.app.ui.theme.OnSurfaceMuted
import com.chessforge.app.ui.theme.Surface

enum class ListCategory { OPENING, TRAP, TACTIC }

private data class ListRow(
    val id: String,
    val title: String,
    val badge: String,
    val summary: String,
    val storageKey: String,
)

@Composable
fun CategoryListScreen(
    category: ListCategory,
    onBack: () -> Unit,
    onOpenItem: (String) -> Unit,
) {
    val context = LocalContext.current
    val progressStore = remember(context) { ProgressStore(context) }

    val rows = remember(category) {
        when (category) {
            ListCategory.OPENING -> OpeningsRepository.all
                .filter { it.category == OpeningCategory.OPENING }
                .map { ListRow(it.id, it.name, it.ecoCode ?: "", it.summary, "opening:${it.id}") }
            ListCategory.TRAP -> OpeningsRepository.all
                .filter { it.category == OpeningCategory.TRAP }
                .map { ListRow(it.id, it.name, it.ecoCode ?: "Trap", it.summary, "opening:${it.id}") }
            ListCategory.TACTIC -> TacticsRepository.all
                .map { ListRow(it.id, it.title, "${it.theme.displayName} · ${it.difficulty.name.lowercase().replaceFirstChar(Char::uppercase)}", it.explanation, "tactic:${it.id}") }
        }
    }

    val masteredIds by progressStore.masteredIds.collectAsState(initial = emptySet())

    val screenTitle = when (category) {
        ListCategory.OPENING -> "Openings"
        ListCategory.TRAP -> "Traps"
        ListCategory.TACTIC -> "Tactics"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = Gold)
            }
            Text(text = screenTitle, style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(rows, key = { it.id }) { row ->
                val mastered = masteredIds.contains(row.storageKey)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface, RoundedCornerShape(14.dp))
                        .clickable { onOpenItem(row.id) }
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.padding(end = 12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = row.title, style = MaterialTheme.typography.titleMedium)
                                if (row.badge.isNotBlank()) {
                                    Text(
                                        text = "  ${row.badge}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = OnSurfaceMuted,
                                    )
                                }
                            }
                            Text(text = row.summary, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                        }
                        if (mastered) {
                            Text(text = "✓", style = MaterialTheme.typography.titleLarge, color = CorrectGreen)
                        }
                    }
                }
            }
        }
    }
}
