import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val moduleName = "tech.lightfeather.designsystem"
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mokoResources)

    // Code Quality - ktlint only (detekt is applied globally from root)
    alias(libs.plugins.ktlint)
}

composeCompiler {
    // Disable experimental features that may not be supported
    enableStrongSkippingMode.set(false)
}

kotlin {

//    // Target declarations - add or remove as needed below. These define
//    // which platforms this KMP module supports.
//    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
//    androidLibrary {
//        namespace =
//        compileSdk = 36
//        minSdk = 24
//
//        withHostTestBuilder {
//        }
//
//        withDeviceTestBuilder {
//            sourceSetTreeName = "test"
//        }.configure {
//            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        }
//
//    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcfName = "designsystemKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.jetbrains.iconsExtended)
                implementation(libs.bundles.coil)
                implementation(libs.compose.window.sizes)
                implementation(libs.bundles.material3Adaptive)
                implementation(libs.material3.material3)
                implementation(libs.kotlinx.datetime)
                implementation(libs.compottie)
                implementation(libs.compottie.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.ui.tooling)
                implementation(libs.androidx.activity.compose)
            }
        }
    }
}
compose.resources {
    publicResClass = true
}
android {
    namespace = moduleName
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        namespace = moduleName
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    commonMainApi(libs.resources)
    commonMainApi(libs.resources.compose) // for compose multiplatform
}

// KtLint Configuration (inherits from root)
// Global configuration is applied via subprojects block in root build.gradle.kts

// Detekt configuration is managed in root build.gradle.kts via subprojects block

multiplatformResources {
    resourcesPackage.set(moduleName) // required
}
