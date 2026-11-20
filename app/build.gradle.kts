/** Configura la aplicación Android, sus variantes, SDK, dependencias y generación de BuildConfig. */
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")

    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val nasaApiKey = localProperties.getProperty("NASA_API_KEY") ?: "DEMO_KEY"
val aiBackendUrl = localProperties.getProperty("AI_BACKEND_URL") ?: "http://10.0.2.2:8000/"

android {
    namespace = "com.example.nasaexplorer"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.nasaexplorer"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "NASA_API_KEY",
            "\"$nasaApiKey\""
        )
        buildConfigField(
            "String",
            "AI_BACKEND_URL",
            "\"$aiBackendUrl\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            manifestPlaceholders["usesCleartextTraffic"] = "false"
        }
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("androidx.datastore:datastore-preferences:1.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Retrofit: solicitudes HTTP
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Conversión de JSON a objetos Kotlin
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Corrutinas para operaciones asíncronas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Traducción local opcional de informes DONKI
    implementation("com.google.mlkit:translate:17.0.3")

    // Coil para carga de imágenes
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")
}