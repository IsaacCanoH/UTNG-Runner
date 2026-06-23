package mx.utng.ich.wear.presentation.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp
import mx.utng.ich.wear.domain.model.Coin
import mx.utng.ich.wear.domain.model.GameState
import mx.utng.ich.wear.domain.model.Obstacle
import mx.utng.ich.wear.domain.model.Player
import kotlin.math.sin

/**
 * GameRenderer: solo dibuja el estado recibido.
 *
 * No modifica GameState.
 * No ejecuta GameEngine.
 * No contiene lógica de colisiones.
 */
object GameRenderer {

    fun draw(
        drawScope: DrawScope,
        state: GameState,
        frame: Long,
        textMeasurer: TextMeasurer
    ) {
        with(drawScope) {
            drawBackground()
            drawGround()
            drawCoins(state.coins, frame)
            drawObstacles(state.obstacles)
            drawPlayer(state.player, frame)
            drawHud(state, textMeasurer)
        }
    }

    private fun DrawScope.drawBackground() {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1B4A),
                    Color(0xFF1A237E)
                )
            ),
            size = size
        )

        // Estrellas decorativas
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = 1.5f,
            center = Offset(size.width * 0.20f, size.height * 0.18f)
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = 1.2f,
            center = Offset(size.width * 0.75f, size.height * 0.25f)
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = 1.0f,
            center = Offset(size.width * 0.45f, size.height * 0.12f)
        )
    }

    private fun DrawScope.drawGround() {
        val groundTop = Player.FLOOR_Y + 20f

        drawRect(
            color = Color(0xFF263238),
            topLeft = Offset(0f, groundTop),
            size = Size(size.width, size.height - groundTop)
        )

        drawLine(
            color = Color(0xFF80CBC4),
            start = Offset(0f, groundTop),
            end = Offset(size.width, groundTop),
            strokeWidth = 2f
        )
    }

    private fun DrawScope.drawPlayer(
        player: Player,
        frame: Long
    ) {
        val alpha = if (
            player.isInvincible && (frame / 4) % 2L == 0L
        ) {
            0.3f
        } else {
            1f
        }

        val legSwing = if (player.isJumping) {
            0f
        } else {
            sin(frame * 0.3f).toFloat() * 5f
        }

        val yPos = player.y

        val bodyColor = Color(0xFFE65100).copy(alpha = alpha)
        val helmetColor = Color(0xFF1A237E).copy(alpha = alpha)
        val visorColor = Color(0xFFB3E5FC).copy(alpha = alpha)
        val legColor = Color(0xFF212121).copy(alpha = alpha)

        // Cuerpo
        drawRect(
            color = bodyColor,
            topLeft = Offset(player.x - 6f, yPos - 10f),
            size = Size(20f, 24f)
        )

        // Casco UTNG
        drawRect(
            color = helmetColor,
            topLeft = Offset(player.x - 5f, yPos - 24f),
            size = Size(18f, 10f)
        )

        // Visor
        drawRect(
            color = visorColor,
            topLeft = Offset(player.x + 2f, yPos - 21f),
            size = Size(8f, 4f)
        )

        // Piernas
        if (!player.isSliding) {
            drawLine(
                color = legColor,
                start = Offset(player.x, yPos + 14f),
                end = Offset(player.x - legSwing, yPos + 22f),
                strokeWidth = 3f
            )

            drawLine(
                color = legColor,
                start = Offset(player.x + 8f, yPos + 14f),
                end = Offset(player.x + 8f + legSwing, yPos + 22f),
                strokeWidth = 3f
            )
        }
    }

    private fun DrawScope.drawObstacles(
        obstacles: List<Obstacle>
    ) {
        val floor = Player.FLOOR_Y + 20f

        obstacles.forEach { obstacle ->
            val obstacleColor = when (obstacle.type.label) {
                "TAREA" -> Color(0xFFEF5350)
                "EXAMEN" -> Color(0xFFAB47BC)
                "BUG" -> Color(0xFFFFCA28)
                "REPO" -> Color(0xFF42A5F5)
                else -> Color.Red
            }

            drawRect(
                color = obstacleColor,
                topLeft = Offset(
                    obstacle.x,
                    floor - obstacle.height
                ),
                size = Size(
                    obstacle.width.toFloat(),
                    obstacle.height.toFloat()
                )
            )
        }
    }

    private fun DrawScope.drawCoins(
        coins: List<Coin>,
        frame: Long
    ) {
        coins.filterNot { it.collected }.forEach { coin ->
            val pulse = sin((frame * 0.15f) + coin.phase).toFloat()

            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 6f + pulse,
                center = Offset(coin.x, coin.y)
            )

            drawCircle(
                color = Color(0xFFFFF59D),
                radius = 2f,
                center = Offset(coin.x - 1f, coin.y - 1f)
            )
        }
    }

    private fun DrawScope.drawHud(
        state: GameState,
        textMeasurer: TextMeasurer
    ) {
        val scoreText = "${state.score} pts"
        val levelText = "Nivel ${state.level}"
        val heartRateText = "${state.heartRate} BPM"

        drawText(
            textMeasurer = textMeasurer,
            text = scoreText,
            topLeft = Offset(
                x = (size.width - measureTextWidth(
                    textMeasurer,
                    scoreText,
                    11.sp
                )) / 2f,
                y = size.height - 28f
            ),
            style = TextStyle(
                color = Color.White,
                fontSize = 11.sp
            )
        )

        drawText(
            textMeasurer = textMeasurer,
            text = levelText,
            topLeft = Offset(
                x = 8f,
                y = 8f
            ),
            style = TextStyle(
                color = Color.White,
                fontSize = 10.sp
            )
        )

        drawText(
            textMeasurer = textMeasurer,
            text = heartRateText,
            topLeft = Offset(
                x = size.width - 58f,
                y = 8f
            ),
            style = TextStyle(
                color = Color(0xFFFF8A80),
                fontSize = 9.sp
            )
        )

        repeat(state.lives) { index ->
            drawHeart(
                x = 10f + index * 15f,
                y = 32f
            )
        }
    }

    private fun DrawScope.measureTextWidth(
        textMeasurer: TextMeasurer,
        text: String,
        fontSize: androidx.compose.ui.unit.TextUnit
    ): Float {
        return textMeasurer.measure(
            text = text,
            style = TextStyle(fontSize = fontSize)
        ).size.width.toFloat()
    }

    private fun DrawScope.drawHeart(
        x: Float,
        y: Float
    ) {
        val heartColor = Color(0xFFFF5252)

        drawCircle(
            color = heartColor,
            radius = 4f,
            center = Offset(x, y)
        )

        drawCircle(
            color = heartColor,
            radius = 4f,
            center = Offset(x + 6f, y)
        )

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(x - 4f, y + 1f)
            lineTo(x + 10f, y + 1f)
            lineTo(x + 3f, y + 11f)
            close()
        }

        drawPath(
            path = path,
            color = heartColor
        )
    }
}