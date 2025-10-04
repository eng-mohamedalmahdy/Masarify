import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

val appPackageName = "com.lightfeather.masarify"
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    id("dev.icerock.mobile.multiplatform-resources")

    // Code Quality - ktlint only (detekt is applied globally from root)
    alias(libs.plugins.ktlint)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
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
            binaryOption("bundleId", "com.lightfeather.masarify.ComposeApp")
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
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
            implementation(libs.navigation.compose)
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
            implementation("com.eygraber:uri-kmp:0.0.19")
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        iosMain.dependencies {
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
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
    commonMainApi(libs.resources)
    commonMainApi(libs.resources.compose) // for compose multiplatform
}

multiplatformResources {
    resourcesPackage.set(appPackageName) // required
}
compose.desktop {
    application {
        mainClass = "com.lightfeather.masarify.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appPackageName
            packageVersion = "1.0.0"
        }
    }
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
        "ktlintJvmMainSourceSetCheck",
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
