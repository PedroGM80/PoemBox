plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.dokka")
}

android {
    namespace = "dev.pgm.poembox"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    ndkVersion = "23.1.7779620"

    defaultConfig {
        applicationId = "dev.pgm.poembox"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

        bundle {
            density {
                enableSplit = true
            }
            abi {
                enableSplit = true
            }
            language {
                enableSplit = false
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    packagingOptions {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    dynamicFeatures = setOf()
}

dependencies {
    implementation(libs.activity.compose)
    implementation(libs.appcompat)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.security.crypto)
    implementation(libs.security.crypto.ktx)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.datastore.preferences)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.coroutines.android)
    implementation(libs.multidex)

    kapt(libs.room.compiler)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}
