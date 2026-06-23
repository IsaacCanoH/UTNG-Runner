package mx.utng.ich.wear.data.repository

import mx.utng.ich.wear.data.datasource.PreferencesDataSource
import mx.utng.ich.wear.domain.repository.ScoreRepository

/**
 * Implementación concreta del repositorio.
 * La capa data implementa el contrato definido en domain.
 */
class ScoreRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : ScoreRepository {

    override suspend fun getHighScore(): Int {
        return dataSource.getHighScore()
    }

    override suspend fun saveHighScore(score: Int) {
        dataSource.saveHighScore(score)
    }
}