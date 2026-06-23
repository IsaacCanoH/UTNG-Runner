package mx.utng.ich.wear.presentation.game

import mx.utng.ich.wear.domain.model.Coin
import mx.utng.ich.wear.domain.model.GamePhase
import mx.utng.ich.wear.domain.model.GameState
import mx.utng.ich.wear.domain.model.Obstacle
import mx.utng.ich.wear.domain.model.ObstacleType
import mx.utng.ich.wear.domain.model.Player
import kotlin.random.Random

/**
 * Motor de lógica del juego.
 *
 * No depende de Android, Canvas ni Compose.
 * Recibe un estado y devuelve un nuevo estado.
 */
object GameEngine {

    fun update(
        state: GameState,
        frame: Long,
        randomValue: Double = Random.nextDouble()
    ): GameState {
        if (state.phase != GamePhase.PLAYING) return state

        val updatedPlayer = updatePlayer(state.player)
        val newScore = state.score + 1
        val newLevel = calculateLevel(newScore)
        val newSpeed = 3f + newLevel * 0.6f

        val updatedObstacles = updateObstacles(
            obstacles = state.obstacles,
            speed = newSpeed,
            frame = frame,
            randomValue = randomValue
        )

        val updatedCoins = updateCoins(
            coins = state.coins,
            speed = newSpeed
        )

        val afterCollision = checkCollisions(
            player = updatedPlayer,
            obstacles = updatedObstacles,
            coins = updatedCoins,
            currentLives = state.lives
        )

        return state.copy(
            player = afterCollision.player,
            score = newScore,
            level = newLevel,
            lives = afterCollision.lives,
            gameSpeed = newSpeed,
            obstacles = afterCollision.obstacles,
            coins = afterCollision.coins,
            phase = if (afterCollision.lives <= 0) {
                GamePhase.DEAD
            } else {
                GamePhase.PLAYING
            }
        )
    }

    private fun updatePlayer(player: Player): Player {
        val newVelocityY = player.velocityY + Player.GRAVITY
        val newY = (player.y + newVelocityY).coerceAtMost(Player.FLOOR_Y)
        val landed = newY >= Player.FLOOR_Y

        return player.copy(
            y = newY,
            velocityY = if (landed) 0f else newVelocityY,
            isJumping = !landed && player.isJumping,
            isSliding = player.slideFrames > 0,
            slideFrames = (player.slideFrames - 1).coerceAtLeast(0),
            isInvincible = player.invincibleFrames > 0,
            invincibleFrames = (player.invincibleFrames - 1).coerceAtLeast(0)
        )
    }

    private fun calculateLevel(score: Int): Int {
        return (1 + score / 300).coerceAtMost(5)
    }

    private fun updateObstacles(
        obstacles: List<Obstacle>,
        speed: Float,
        frame: Long,
        randomValue: Double
    ): List<Obstacle> {
        val movedObstacles = obstacles
            .map { obstacle ->
                obstacle.copy(x = obstacle.x - speed)
            }
            .filter { obstacle ->
                obstacle.x > -50f
            }

        val shouldSpawnObstacle = frame % 60L == 0L && randomValue < 0.6

        return if (shouldSpawnObstacle) {
            val type = ObstacleType.entries.random()

            movedObstacles + Obstacle(
                x = 240f,
                width = type.w,
                height = type.h,
                type = type
            )
        } else {
            movedObstacles
        }
    }

    private fun updateCoins(
        coins: List<Coin>,
        speed: Float
    ): List<Coin> {
        return coins
            .map { coin ->
                coin.copy(
                    x = coin.x - speed,
                    phase = coin.phase + 0.15f
                )
            }
            .filter { coin ->
                coin.x > -30f && !coin.collected
            }
    }

    /**
     * Detección de colisiones AABB.
     */
    private fun checkCollisions(
        player: Player,
        obstacles: List<Obstacle>,
        coins: List<Coin>,
        currentLives: Int
    ): CollisionResult {
        val floor = Player.FLOOR_Y + 20f

        val playerLeft = player.x - 10f
        val playerRight = player.x + 18f
        val playerTop = player.y - if (player.isSliding) 8f else 30f
        val playerBottom = player.y + 20f

        val hitObstacles = obstacles.filter { obstacle ->
            !player.isInvincible &&
                    playerRight > obstacle.x + 4f &&
                    playerLeft < obstacle.x + obstacle.width - 4f &&
                    playerBottom > floor - obstacle.height &&
                    playerTop < floor
        }

        val updatedCoins = coins.map { coin ->
            val distance = Math.hypot(
                (player.x - coin.x).toDouble(),
                (player.y - coin.y).toDouble()
            )

            if (!coin.collected && distance < 22.0) {
                coin.copy(collected = true)
            } else {
                coin
            }
        }

        val newLives = if (hitObstacles.isNotEmpty()) {
            (currentLives - hitObstacles.size).coerceAtLeast(0)
        } else {
            currentLives
        }

        return CollisionResult(
            player = if (hitObstacles.isNotEmpty()) {
                player.copy(
                    isInvincible = true,
                    invincibleFrames = Player.INVINCIBLE_FRAMES
                )
            } else {
                player
            },
            lives = newLives,
            obstacles = obstacles.map { obstacle ->
                if (obstacle in hitObstacles) {
                    obstacle.copy(x = -999f)
                } else {
                    obstacle
                }
            },
            coins = updatedCoins
        )
    }
}

data class CollisionResult(
    val player: Player,
    val lives: Int,
    val obstacles: List<Obstacle>,
    val coins: List<Coin>
)