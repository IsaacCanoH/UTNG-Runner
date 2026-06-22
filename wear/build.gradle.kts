plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.utng.ich.wear"
    compileSdk = 37

    defaultConfig {
        applicationId = "mx.utng.utngrunner"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Dependencias actuales del proyecto: NO BORRAR
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.wear.tooling.preview)

    // Debe conservarse porque tu código importa androidx.wear.compose.material3.*
    implementation(libs.compose.material3)

    implementation(libs.compose.ui.tooling)
    implementation(libs.play.services.wearable)

    // ==============================
    // PASO 1: dependencias agregadas
    // ==============================

    // Wear Compose Foundation:
    // Necesaria para TransformingLazyColumn y rememberTransformingLazyColumnState.
    implementation("androidx.wear.compose:compose-foundation:1.3.1")

    // ViewModel y corrutinas
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // DataStore para guardar el mejor puntaje
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Health Services: frecuencia cardiaca
    implementation("androidx.health:health-services-client:1.1.0-alpha03")

    // Pruebas locales
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("junit:junit:4.13.2")

    // Pruebas instrumentadas existentes
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Herramientas para debug y previews
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}