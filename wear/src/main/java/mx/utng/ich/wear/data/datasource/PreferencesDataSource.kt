package mx.utng.ich.wear.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "utng_runner_prefs"
)

class PreferencesDataSource(
    private val context: Context
) {

    private object Keys {
        val HIGH_SCORE = intPreferencesKey("high_score")
    }

    suspend fun getHighScore(): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[Keys.HIGH_SCORE] ?: 0
            }
            .first()
    }

    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.HIGH_SCORE] = score
        }
    }
}