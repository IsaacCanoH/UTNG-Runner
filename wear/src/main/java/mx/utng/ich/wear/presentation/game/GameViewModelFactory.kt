package mx.utng.ich.wear.presentation.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.utng.ich.wear.data.datasource.PreferencesDataSource
import mx.utng.ich.wear.data.repository.ScoreRepositoryImpl
import mx.utng.ich.wear.domain.usecase.GetHighScoreUseCase
import mx.utng.ich.wear.domain.usecase.SaveHighScoreUseCase

class GameViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {

            val dataSource = PreferencesDataSource(appContext)
            val repository = ScoreRepositoryImpl(dataSource)

            return GameViewModel(
                getHighScore = GetHighScoreUseCase(repository),
                saveHighScore = SaveHighScoreUseCase(repository)
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel no soportado: ${modelClass.name}"
        )
    }
}