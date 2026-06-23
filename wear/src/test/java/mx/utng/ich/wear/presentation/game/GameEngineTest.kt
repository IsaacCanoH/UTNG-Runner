package mx.utng.ich.wear.presentation.game

import mx.utng.ich.wear.domain.model.GamePhase
import mx.utng.ich.wear.domain.model.GameState
import mx.utng.ich.wear.domain.model.Obstacle
import mx.utng.ich.wear.domain.model.ObstacleType
import mx.utng.ich.wear.domain.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun `player falls due to gravity`() {
        val state = GameState(
            phase = GamePhase.PLAYING,
            player = Player(
                y = 100f,
                velocityY = 0f
            )
        )

        val next = GameEngine.update(
            state = state,
            frame = 1L,
            randomValue = 1.0
        )

        assertTrue(next.player.y > 100f)
    }

    @Test
    fun `score increases every frame`() {
        val state = GameState(
            phase = GamePhase.PLAYING,
            score = 0
        )

        val next = GameEngine.update(
            state = state,
            frame = 1L,
            randomValue = 1.0
        )

        assertEquals(1, next.score)
    }

    @Test
    fun `level increases at score 300`() {
        val state = GameState(
            phase = GamePhase.PLAYING,
            score = 299
        )

        val next = GameEngine.update(
            state = state,
            frame = 1L,
            randomValue = 1.0
        )

        assertEquals(2, next.level)
    }

    @Test
    fun `lives decrease on obstacle collision`() {
        val obstacle = Obstacle(
            x = Player().x - 5f,
            width = 20,
            height = 35,
            type = ObstacleType.TAREA
        )

        val state = GameState(
            phase = GamePhase.PLAYING,
            player = Player(
                y = Player.FLOOR_Y,
                isInvincible = false
            ),
            obstacles = listOf(obstacle),
            lives = 3
        )

        val next = GameEngine.update(
            state = state,
            frame = 1L,
            randomValue = 1.0
        )

        assertTrue(next.lives < 3)
    }

    @Test
    fun `game over when lives reach zero`() {
        val state = GameState(
            phase = GamePhase.PLAYING,
            lives = 0
        )

        val next = GameEngine.update(
            state = state,
            frame = 1L,
            randomValue = 1.0
        )

        assertEquals(GamePhase.DEAD, next.phase)
    }
}