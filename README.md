# UTNG Runner

Juego tipo **Runner** desarrollado para **Wear OS** con Kotlin y Compose for Wear OS.
El jugador controla un personaje que debe saltar obstáculos, deslizarse y obtener la mayor puntuación posible usando la pantalla táctil o la corona del reloj.

## Características

* Juego diseñado para relojes Wear OS.
* Interfaz desarrollada con Compose for Wear OS.
* Dibujo del juego mediante `Canvas`.
* Control táctil:

    * Toque: saltar o iniciar partida.
* Control con corona rotatoria:

    * Giro hacia arriba: saltar.
    * Giro hacia abajo: deslizarse.
* Sistema de puntuación, niveles y vidas.
* Obstáculos con detección de colisiones AABB.
* Monedas coleccionables.
* Persistencia del récord mediante DataStore.
* Retroalimentación háptica al saltar.
* Pruebas unitarias para la lógica del motor del juego.
* Arquitectura limpia con separación por capas.

## Arquitectura

El proyecto utiliza una estructura basada en Clean Architecture y MVVM.

```text
wear/
└── src/main/java/mx/utng/ich/wear/
    ├── data/
    │   ├── datasource/
    │   │   └── PreferencesDataSource.kt
    │   └── repository/
    │       └── ScoreRepositoryImpl.kt
    │
    ├── domain/
    │   ├── model/
    │   │   ├── GameState.kt
    │   │   └── Player.kt
    │   ├── repository/
    │   │   └── ScoreRepository.kt
    │   └── usecase/
    │       ├── GetHighScoreUseCase.kt
    │       └── SaveHighScoreUseCase.kt
    │
    └── presentation/
        ├── GameActivity.kt
        ├── game/
        │   ├── GameEngine.kt
        │   ├── GameRenderer.kt
        │   ├── GameScreen.kt
        │   ├── GameViewModel.kt
        │   └── GameViewModelFactory.kt
        └── theme/
```

## Capas del proyecto

### Domain

Contiene las reglas y modelos puros del juego.
No depende de Android, Compose, Canvas ni Wear OS.

Incluye:

* `GameState`
* `Player`
* `Obstacle`
* `Coin`
* `ScoreRepository`
* Casos de uso para obtener y guardar el récord.

### Data

Se encarga del almacenamiento de información.

Incluye:

* `PreferencesDataSource`: guarda el puntaje máximo usando DataStore.
* `ScoreRepositoryImpl`: implementación concreta del repositorio de puntajes.

### Presentation

Contiene la interfaz y la interacción con el jugador.

Incluye:

* `GameViewModel`: administra el estado del juego mediante `StateFlow`.
* `GameEngine`: procesa la lógica, gravedad, puntuación, niveles y colisiones.
* `GameRenderer`: dibuja el juego usando Canvas.
* `GameScreen`: conecta el ViewModel, Canvas, controles táctiles y corona rotatoria.
* `GameViewModelFactory`: realiza inyección manual de dependencias.

## Tecnologías utilizadas

* Kotlin
* Android Studio
* Wear OS
* Compose for Wear OS
* Material 3 para Wear OS
* Canvas de Jetpack Compose
* ViewModel
* StateFlow
* Coroutines
* DataStore Preferences
* JUnit 4
* Gradle Kotlin DSL

## Requisitos

* Android Studio actualizado.
* JDK 11 o superior.
* Emulador Wear OS o reloj físico compatible.
* Android SDK configurado.
* Mínimo Wear OS 3.0.

## Configuración principal

```kotlin
minSdk = 30
applicationId = "mx.utng.utngrunner"
```

## Ejecución

1. Clonar el repositorio:

```bash
git clone URL_DEL_REPOSITORIO
```

2. Abrir el proyecto en Android Studio.

3. Sincronizar Gradle:

```text
File > Sync Project with Gradle Files
```

4. Seleccionar un emulador Wear OS o un reloj físico.

5. Ejecutar el módulo `wear`.

## Ejecutar pruebas unitarias

Desde la terminal, en la raíz del proyecto:

```bash
.\gradlew.bat :wear:testDebugUnitTest
```

Las pruebas verifican:

* Aplicación de gravedad.
* Incremento de puntuación.
* Cambio de nivel.
* Colisiones con obstáculos.
* Finalización del juego cuando las vidas llegan a cero.

## Controles del juego

| Acción          | Control                                          |
| --------------- | ------------------------------------------------ |
| Iniciar partida | Tocar la pantalla                                |
| Saltar          | Tocar la pantalla o girar la corona hacia arriba |
| Deslizarse      | Girar la corona hacia abajo                      |
| Reintentar      | Tocar la pantalla al terminar la partida         |

## Convención de commits

El proyecto usa Conventional Commits.

Ejemplos:

```text
feat: add GameEngine — pure function game logic with AABB collision detection
feat: add GameViewModel with StateFlow, 60fps game loop and use case integration
test: add GameEngine unit tests for physics, scoring, collision and game over
```

## Versión

```text
v1.0.0
```

## Autor

Proyecto académico desarrollado para la Universidad Tecnológica del Norte de Guanajuato.

**UTNG Runner — Juego para Wear OS con Clean Architecture, MVVM y Compose for Wear OS.**
