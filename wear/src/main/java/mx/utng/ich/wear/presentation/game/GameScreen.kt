package mx.utng.ich.wear.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.Text
import kotlinx.coroutines.delay
import mx.utng.ich.wear.domain.model.GamePhase

@Composable
fun GameScreen() {
    val context = LocalContext.current

    val factory = remember(context.applicationContext) {
        GameViewModelFactory(context.applicationContext)
    }

    val viewModel: GameViewModel = viewModel(
        factory = factory
    )

    GameScreenContent(
        viewModel = viewModel
    )
}

@Composable
private fun GameScreenContent(
    viewModel: GameViewModel
) {
    val state by viewModel.state.collectAsState()

    var frame by remember {
        mutableLongStateOf(0L)
    }

    val textMeasurer = rememberTextMeasurer()

    /*
     * Loop visual independiente.
     * El GameViewModel actualiza la lógica;
     * este frame solamente permite animar el dibujo.
     */
    LaunchedEffect(state.phase) {
        while (state.phase == GamePhase.PLAYING) {
            delay(16L)
            frame++
        }
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onRotaryScrollEvent { event ->
                if (event.verticalScrollPixels < 0f) {
                    viewModel.onJump()
                } else {
                    viewModel.onSlide()
                }

                true
            }
            .clickable {
                viewModel.onJump()
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            GameRenderer.draw(
                drawScope = this,
                state = state,
                frame = frame,
                textMeasurer = textMeasurer
            )
        }

        when (state.phase) {
            GamePhase.IDLE -> {
                IdleOverlay(
                    onStart = viewModel::onJump
                )
            }

            GamePhase.DEAD -> {
                GameOverOverlay(
                    score = state.score,
                    highScore = state.highScore,
                    onRetry = viewModel::onJump
                )
            }

            GamePhase.PLAYING,
            GamePhase.PAUSED -> Unit
        }
    }
}

@Composable
private fun IdleOverlay(
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBB000000))
            .clickable(onClick = onStart),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "UTNG Runner",
                color = Color(0xFFF9A825),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Toca o gira la corona",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun GameOverOverlay(
    score: Int,
    highScore: Int,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .clickable(onClick = onRetry),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAME OVER",
                color = Color(0xFFFF5252),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Puntaje: $score",
                color = Color.White,
                fontSize = 13.sp
            )

            Text(
                text = "Récord: $highScore",
                color = Color(0xFFFFD54F),
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Toca para reintentar",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp
            )
        }
    }
}