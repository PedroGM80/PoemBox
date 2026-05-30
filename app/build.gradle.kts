import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// ── Versión leída desde version.properties ────────────────────────────────
val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) versionPropsFile.inputStream().use { load(it) }
}
val vCode = versionProps.getProperty("versionCode", "1").toInt()
val vName = versionProps.getProperty("versionName", "1.0.0")

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = "dev.pgm.poembox"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.pgm.poembox"
        minSdk = 24
        targetSdk = 37
        versionCode = vCode
        versionName = vName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ── Firma de release ─────────────────────────────────────────────────
    // Prioridad: variables de entorno (CI) > keystore.properties (local)
    signingConfigs {
        create("release") {
            val envKeystore    = System.getenv("RELEASE_KEYSTORE_PATH")
            val envStorePass   = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            val envKeyAlias    = System.getenv("RELEASE_KEY_ALIAS")
            val envKeyPass     = System.getenv("RELEASE_KEY_PASSWORD")

            if (envKeystore != null) {
                storeFile     = file(envKeystore)
                storePassword = envStorePass
                keyAlias      = envKeyAlias
                keyPassword   = envKeyPass
            } else {
                // Desarrollo local: leer keystore.properties (nunca commitear)
                val localProps = rootProject.file("keystore.properties")
                if (localProps.exists()) {
                    val kp = Properties().apply { localProps.inputStream().use { load(it) } }
                    storeFile     = rootProject.file(kp.getProperty("storeFile", ""))
                    storePassword = kp.getProperty("storePassword", "")
                    keyAlias      = kp.getProperty("keyAlias", "")
                    keyPassword   = kp.getProperty("keyPassword", "")
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.ui.text.google.fonts)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material.icons.extended)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Play
    implementation(libs.play.review)

    // Glance (widgets)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Billing
    implementation(libs.android.billing)

    // MediaPipe LLM — IA on-device
    implementation(libs.mediapipe.tasks.genai)

    // Modules
    implementation(project(":domain"))
    implementation(project(":data"))

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
