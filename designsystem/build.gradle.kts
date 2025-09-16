import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val moduleName = "com.lightfeather.designsystem"
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("dev.icerock.mobile.multiplatform-resources")

    // Code Quality
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
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

    jvm() // For JVM apps

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

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
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

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(rootProject.file("detekt.yml"))
    baseline = rootProject.file("detekt-baseline.xml")
    ignoreFailures = false

    // Optional: be explicit about what source roots you expect detekt to analyze
    // (useful for KMP modules)
    source =
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/jvmMain/kotlin",
            "src/iosMain/kotlin",
        )
}

tasks.withType<Detekt>().configureEach {
    // TASK-LEVEL excludes use glob patterns (not regex). This is the crucial part.
    exclude("**/build/**", "**/generated/**", "**/commonMainResourceAccessors/**")

    // Optional: show what files will be analyzed (use --info to see logger output)
    doFirst {
        val ktFiles =
            source.files
                .flatMap { root ->
                    root.walkTopDown().filter { it.isFile && (it.extension == "kt" || it.extension == "kts") }.toList()
                }.filterNot {
                    it.absolutePath.contains("${project.buildDir.path}")
                } // attempt to filter build dir copies

        logger.lifecycle("Detekt will analyze ${ktFiles.size} Kotlin files (showing first 100):")
        ktFiles.take(100).forEach { logger.lifecycle("  - ${it.absolutePath}") }
    }

    // reports (keep as you already had)
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}
multiplatformResources {
    resourcesPackage.set(moduleName) // required
}
