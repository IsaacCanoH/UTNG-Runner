package mx.utng.ich.wear.domain.repository

/**
 * Contrato para consultar y guardar el puntaje máximo.
 *
 * La capa domain conoce solo esta interfaz.
 * La implementación real se hará después en la capa data.
 */
interface ScoreRepository {
    suspend fun getHighScore(): Int
    suspend fun saveHighScore(score: Int)
}