package mx.utng.ich.wear.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.utng.ich.wear.domain.model.GamePhase
import mx.utng.ich.wear.domain.model.GameState
import mx.utng.ich.wear.domain.model.Player
import mx.utng.ich.wear.domain.usecase.GetHighScoreUseCase
import mx.utng.ich.wear.domain.usecase.SaveHighScoreUseCase

class GameViewModel(
    private val getHighScore: GetHighScoreUseCase,
    private val saveHighScore: SaveHighScoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())

    val state: StateFlow<GameState> = _state.asStateFlow()

    private var gameFrame = 0L
    private var gameJob: Job? = null

    init {
        loadHighScore()
    }

    fun startGame() {
        gameJob?.cancel()

        _state.value = GameState(
            phase = GamePhase.PLAYING,
            highScore = _state.value.highScore
        )

        gameFrame = 0L

        gameJob = viewModelScope.launch {
            while (_state.value.phase == GamePhase.PLAYING) {
                delay(16L)

                _state.update { currentState ->
                    GameEngine.update(
                        state = currentState,
                        frame = gameFrame++
                    )
                }
            }

            if (_state.value.phase == GamePhase.DEAD) {
                saveHighScore(_state.value.score)

                val savedHighScore = getHighScore()

                _state.update {
                    it.copy(highScore = savedHighScore)
                }
            }
        }
    }

    fun onJump() {
        val currentState = _state.value

        when (currentState.phase) {
            GamePhase.IDLE,
            GamePhase.DEAD -> startGame()

            GamePhase.PLAYING -> {
                val player = currentState.player

                if (!player.isJumping && player.y >= Player.FLOOR_Y - 5f) {
                    _state.update {
                        it.copy(
                            player = it.player.copy(
                                velocityY = Player.JUMP_VELOCITY,
                                isJumping = true
                            )
                        )
                    }
                }
            }

            GamePhase.PAUSED -> Unit
        }
    }

    fun onSlide() {
        val currentState = _state.value

        if (
            currentState.phase != GamePhase.PLAYING ||
            currentState.player.isJumping
        ) {
            return
        }

        _state.update {
            it.copy(
                player = it.player.copy(
                    slideFrames = Player.SLIDE_DURATION
                )
            )
        }
    }

    private fun loadHighScore() {
        viewModelScope.launch {
            val highScore = getHighScore()

            _state.update {
                it.copy(highScore = highScore)
            }
        }
    }

    override fun onCleared() {
        gameJob?.cancel()
        super.onCleared()
    }
}