@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

val appPackageName = "tech.lightfeather.masarify"
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokoResources)
    alias(libs.plugins.googleServices)

    // Code Quality - ktlint only (detekt is applied globally from root)
    alias(libs.plugins.ktlint)
}

composeCompiler {
    // Disable experimental features that may not be supported
    enableStrongSkippingMode.set(false)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Fix bundle ID inference issue
            binaryOption("bundleId", "tech.lightfeather.masarify.ComposeApp")
        }
    }

    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static =
                            (static ?: mutableListOf()).apply {
                                // Serve sources to debug inside browser
                                add(rootDirPath)
                                add(projectDirPath)
                            }
                    }
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {

        commonMain.dependencies {
            implementation(projects.domain)
            implementation(projects.data)
            implementation(projects.designsystem)
            implementation(libs.kotlinx.datetime)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.window.sizes)
            implementation(libs.bundles.navigation3)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.bundles.koinCommon)
            implementation(libs.napier)
            implementation(libs.bundles.material3Adaptive)
            implementation(libs.jetbrains.iconsExtended)
            implementation(libs.compose.back.handler)
            implementation(libs.okio.core)
            implementation(libs.uri.kmp)
            implementation(libs.bundles.filekit)
            implementation(libs.compottie)
            implementation(libs.compottie.resources)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.biometric)
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(project.dependencies.enforcedPlatform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            implementation(libs.kmpworkmanager.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ui.test)
        }
        iosMain.dependencies {
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.kmpworkmanager.core)
        }
    }
}

android {
    namespace = appPackageName
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = appPackageName
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    debugImplementation(compose.uiTest)
    commonMainApi(libs.resources)
    commonMainApi(libs.resources.compose) // for compose multiplatform
}

multiplatformResources {
    resourcesPackage.set(appPackageName) // required
}
tasks.named("wasmJsProcessResources") {
    dependsOn("generateMRwasmJsMain") // moko’s codegen task
}
tasks.named<Copy>("wasmJsProcessResources") {
    from("$buildDir/generated/moko-resources/wasmJsMain/res") {
        into(".") // keep the folder structure (./localization/…)
    }
}
val copyWasmResources =
    tasks.register("copyWasmResources", Copy::class.java) {
        // Source folder: your static resources folder
        val resourcesDir = file("$rootDir/composeApp/src/wasmJsMain/resources")

        from(resourcesDir)
        into(layout.buildDirectory.dir("sqlite"))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        // Optional: flatten if you don’t want subfolders
        include("**/*.js", "**/*.wasm")
    }
tasks.named("wasmJsProcessResources") {
    dependsOn(copyWasmResources)
}

// KtLint Configuration - disable scanning of generated files
ktlint {
    filter {
        include("src/**/*.kt")
        exclude("build/**")
        exclude("**/generated/**")
        exclude("**/build/generated/**")
        exclude("**/MR.kt")
        exclude("**/Res.kt")
        exclude("**/*ResourceCollectors*.kt")
        exclude("**/*ResourceAccessors*.kt")
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
}

// Disable KtLint for generated files by disabling specific problematic tasks
afterEvaluate {
    // Disable source sets that contain generated code
    listOf(
        "ktlintAndroidMainSourceSetCheck",
        "ktlintCommonMainSourceSetCheck",
        "ktlintWasmJsMainSourceSetCheck",
        "ktlintIosMainSourceSetCheck",
    ).forEach { taskName ->
        tasks.findByName(taskName)?.enabled = false
    }

    // Create a custom KtLint task that only scans actual source files
    tasks.register("ktlintCheckSourceOnly") {
        group = "verification"
        description = "Run KtLint only on source files, excluding generated code"

        doLast {
            exec {
                workingDir = project.rootDir
                commandLine = listOf("./gradlew", "ktlintFormat")
            }
        }
    }
}

// Make all run tasks depend on code quality checks
tasks.matching { it.name.contains("run", true) }.configureEach {
    dependsOn(":detektAll")
    mustRunAfter("ktlintCheck")
}

// Exclude UI (Compose) content tests from the JVM unit-test task.
// runComposeUiTest requires an Android/iOS/WASM runtime; on JVM it always fails.
// Content tests run correctly via iosSimulatorArm64Test and wasmJsBrowserTest.
afterEvaluate {
    tasks.withType<Test>().matching { it.name == "testDebugUnitTest" }.configureEach {
        filter {
            excludeTestsMatching("*ContentTest")
        }
    }
}

// ==================== Screen Recording ====================
// Uses dedicated start/stop task pairs with finalizedBy so recordings are always
// captured and finalized even when the test task itself fails.

afterEvaluate {

    // ── iOS ──────────────────────────────────────────────────────────────
    var iosRecordingProcess: Process? = null

    val startIosRecording =
        tasks.register("startIosRecording") {
            group = "verification"
            doLast {
                File("$buildDir/reports/recordings").mkdirs()
                try {
                    iosRecordingProcess =
                        ProcessBuilder(
                            "bash",
                            "$rootDir/scripts/record-ios-tests.sh",
                            "$buildDir/reports/recordings/ios-test.mp4",
                        ).inheritIO().start()
                } catch (e: Exception) {
                    logger.warn("[recording] iOS: ${e.message}")
                }
            }
        }
    val stopIosRecording =
        tasks.register("stopIosRecording") {
            group = "verification"
            doLast {
                // xcrun simctl recordVideo finalizes the MP4 on SIGINT, not SIGTERM
                try {
                    iosRecordingProcess?.pid()?.let { pid ->
                        ProcessBuilder("kill", "-2", pid.toString()).start().waitFor()
                        Thread.sleep(2_000) // wait for finalization
                    }
                } catch (_: Exception) {
                }
                try {
                    iosRecordingProcess?.destroy()
                } catch (_: Exception) {
                }
                iosRecordingProcess = null
            }
        }
    tasks.findByName("iosSimulatorArm64Test")?.let {
        it.dependsOn(startIosRecording)
        it.finalizedBy(stopIosRecording)
    }

    // ── Android ──────────────────────────────────────────────────────────
    var androidRecordingProcess: Process? = null

    val startAndroidRecording =
        tasks.register("startAndroidRecording") {
            group = "verification"
            doLast {
                File("$buildDir/reports/recordings").mkdirs()
                try {
                    androidRecordingProcess =
                        ProcessBuilder(
                            "adb",
                            "shell",
                            "screenrecord",
                            "--time-limit",
                            "180",
                            "/sdcard/test-android.mp4",
                        ).start()
                } catch (e: Exception) {
                    logger.warn("[recording] Android: ${e.message}")
                }
            }
        }
    val stopAndroidRecording =
        tasks.register("stopAndroidRecording") {
            group = "verification"
            doLast {
                // Send SIGINT to screenrecord on device so it writes the MP4 moov atom cleanly
                try {
                    ProcessBuilder("adb", "shell", "killall", "-2", "screenrecord")
                        .inheritIO()
                        .start()
                        .waitFor()
                    Thread.sleep(2_000) // wait for screenrecord to flush and close
                } catch (_: Exception) {
                }
                try {
                    androidRecordingProcess?.destroy()
                } catch (_: Exception) {
                }
                androidRecordingProcess = null
                try {
                    ProcessBuilder(
                        "adb",
                        "pull",
                        "/sdcard/test-android.mp4",
                        "$buildDir/reports/recordings/android-test.mp4",
                    ).inheritIO().start().waitFor()
                    ProcessBuilder("adb", "shell", "rm", "/sdcard/test-android.mp4")
                        .inheritIO()
                        .start()
                        .waitFor()
                } catch (e: Exception) {
                    logger.warn("[recording] Android pull: ${e.message}")
                }
            }
        }
    tasks.findByName("connectedDebugAndroidTest")?.let {
        it.dependsOn(startAndroidRecording)
        it.finalizedBy(stopAndroidRecording)
    }

    // ── WASM (Playwright screenshot of Karma HTML report after tests) ──
    val startWasmRecording =
        tasks.register("startWasmRecording") {
            group = "verification"
            doLast {
                File("$buildDir/reports/recordings").mkdirs()
            }
        }
    val stopWasmRecording =
        tasks.register("stopWasmRecording") {
            group = "verification"
            doLast {
                // Screenshot Karma's HTML report as PNG after tests complete (no CDP interference)
                try {
                    ProcessBuilder(
                        "node",
                        "$rootDir/scripts/screenshot-wasm-report.js",
                        "$buildDir",
                    ).inheritIO().start().waitFor()
                } catch (e: Exception) {
                    logger.warn("[recording] WASM screenshot: ${e.message}")
                }
            }
        }
    tasks.findByName("wasmJsBrowserTest")?.let {
        it.dependsOn(startWasmRecording)
        it.finalizedBy(stopWasmRecording)
    }
}

// Unified HTML report across all platform test results
tasks.register<Exec>("generateTestReport") {
    group = "verification"
    description = "Generate unified HTML test report from all platform results"

    // Resolve node via 'which node' so nvm/volta/brew paths all work without
    // requiring Gradle to inherit the shell PATH (it often does not in IDE runs).
    val nodeBin =
        providers
            .exec {
                commandLine("bash", "-c", "which node || command -v node")
            }.standardOutput.asText
            .get()
            .trim()
            .ifEmpty { "node" }

    commandLine(
        nodeBin,
        "$rootDir/scripts/generate-test-report.js",
        "$buildDir",
    )
    isIgnoreExitValue = true
}
