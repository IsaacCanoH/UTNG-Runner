package mx.utng.ich.wear.domain.usecase

import mx.utng.ich.wear.domain.repository.ScoreRepository

/**
 * Caso de uso: obtiene el puntaje máximo almacenado.
 */
class GetHighScoreUseCase(
    private val repository: ScoreRepository
) {
    suspend operator fun invoke(): Int = repository.getHighScore()
}

/**
 * Caso de uso: guarda el puntaje solo si supera el actual.
 */
class SaveHighScoreUseCase(
    private val repository: ScoreRepository
) {
    suspend operator fun invoke(score: Int) {
        val current = repository.getHighScore()

        if (score > current) {
            repository.saveHighScore(score)
        }
    }
}