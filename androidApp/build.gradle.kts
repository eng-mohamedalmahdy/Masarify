plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("io.github.aakira:napier:2.6.1")

            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.masarify"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.lightfeather.Masarify"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}
dependencies {
    val koinVersion = "3.5.2-RC1"

    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("com.arkivanov.decompose:decompose:2.1.0-compose-experimental")
    implementation("io.github.xxfast:decompose-router:0.5.1")

    runtimeOnly("io.insert-koin:koin-androidx-compose:${koinVersion}")
    implementation("io.insert-koin:koin-core:${koinVersion}")
    implementation("io.insert-koin:koin-test:${koinVersion}")
    implementation("io.insert-koin:koin-android:${koinVersion}")

}
