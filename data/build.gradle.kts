@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)

    // Code Quality
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.lightfeather.data"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "dataKit"

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
        binaries.executable() // Allows creating WASM binary
        // Optional: configure browser only if needed
        browser {
            commonWebpackConfig {
                // Can leave mostly default for non-UI modules
                outputFileName = "${project.name}.js"
            }
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
                implementation(projects.domain)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.bundles.multiplatformSettings)
                implementation(libs.napier)
                implementation(libs.bundles.ktor)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.sqldelight.android)
                implementation(libs.koin.android)
                implementation(libs.ktor.client.cio)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        nativeMain {
            dependencies {
                implementation(libs.sqldelight.native)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.sqldelight.sqlite)
                implementation(libs.ktor.client.cio)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.sqldelight.webworker.driver)
                implementation(npm("sql.js", libs.versions.sqlJs.get()))
                implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqldelight.get()))
                implementation(devNpm("copy-webpack-plugin", libs.versions.webPackPlugin.get()))
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3.1") // or latest
                implementation(libs.ktor.client.js)
            }
        }
    }
}

sqldelight {
    databases {
        linkSqlite = true
        create("Database") {
            packageName.set("com.lightfeather.masarify.database")
            generateAsync = true
            dialect("${libs.sqldelight.sqlite.dialect.get().module}:${libs.sqldelight.sqlite.dialect.get().version}")
            module("${libs.sqldelight.sqlite.json.get().module}:${libs.sqldelight.sqlite.json.get().version}")
        }
    }
}

// Ensure codegen tasks run first if you have moko resources
tasks.named("wasmJsProcessResources") {
    dependsOn("generateMRwasmJsMain") // if using moko
}

// Copy sqljs.worker.js into the processed resources folder with debug
tasks.named<Copy>("wasmJsProcessResources") {
    from("data/src/wasmJsMain/resources") {
        include("sqljs.worker.js")
        into(".") // keep at root so import.meta.url finds it
    }

    // Debug: log when task starts
    doFirst {
        println(">>> wasmJsProcessResources starting...")
        println(">>> Source folder: data/src/wasmJsMain/resources")
        val files =
            fileTree("data/src/wasmJsMain/resources") {
                include("sqljs.worker.js")
            }.files
        println(">>> Files found to copy: ${files.map { it.absolutePath }}")
    }

    // Debug: log when task finishes
    doLast {
        println(">>> wasmJsProcessResources finished.")
        println(">>> Files copied to: ${destinationDir.absolutePath}")
        val copiedFiles =
            fileTree(destinationDir) {
                include("sqljs.worker.js")
            }.files
        println(">>> Files actually copied: ${copiedFiles.map { it.absolutePath }}")
    }
}

// KtLint Configuration (inherits from root)
// Global configuration is applied via subprojects block in root build.gradle.kts

// DetektKT Configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$rootDir/detekt.yml")
    baseline = file("$rootDir/detekt-baseline.xml")
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}
