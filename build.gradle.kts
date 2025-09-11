plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.sqldelight) apply false
    id("dev.icerock.mobile.multiplatform-resources") version "0.25.0"

    // Code Quality Plugins
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false

}

// Global KtLint configuration for all subprojects
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.4.1")
        android.set(true)
        ignoreFailures.set(false)

        // Only include src directories, exclude everything else
        filter {
            include("**/src/**/*.kt")
            exclude("**/build/**")
            exclude("**/generated/**")
        }
    }
    
    // Disable KtLint for Main source sets that contain generated files
    afterEvaluate {
        listOf(
            "ktlintAndroidMainSourceSetCheck",
            "ktlintCommonMainSourceSetCheck",
            "ktlintWasmJsMainSourceSetCheck",
            "ktlintIosMainSourceSetCheck",
            "ktlintJvmMainSourceSetCheck"
        ).forEach { taskName ->
            tasks.findByName(taskName)?.enabled = false
        }
    }
}

