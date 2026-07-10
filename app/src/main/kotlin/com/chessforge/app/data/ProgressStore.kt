package com.chessforge.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "chessforge_progress")

private val COMPLETED_KEY = stringSetPreferencesKey("completed_ids")
private val MASTERED_KEY = stringSetPreferencesKey("mastered_ids")

/** Tracks which lines/puzzles the learner has practiced and fully mastered. */
class ProgressStore(private val context: Context) {

    val completedIds: Flow<Set<String>> =
        context.dataStore.data.map { it[COMPLETED_KEY] ?: emptySet() }

    val masteredIds: Flow<Set<String>> =
        context.dataStore.data.map { it[MASTERED_KEY] ?: emptySet() }

    suspend fun markCompleted(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[COMPLETED_KEY] ?: emptySet()
            prefs[COMPLETED_KEY] = current + id
        }
    }

    /** Mastered = completed at least once with zero mistakes. */
    suspend fun markMastered(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[MASTERED_KEY] ?: emptySet()
            prefs[MASTERED_KEY] = current + id
        }
    }
}
