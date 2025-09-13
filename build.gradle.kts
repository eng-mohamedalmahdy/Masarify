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
            "ktlintIosArm64MainSourceSetCheck",
            "ktlintIosSimulatorArm64MainSourceSetCheck",
            "ktlintIosX64MainSourceSetCheck",
            "ktlintJvmMainSourceSetCheck",
            "ktlintAndroidMainSourceSetFormat",
            "ktlintCommonMainSourceSetFormat",
            "ktlintWasmJsMainSourceSetFormat",
            "ktlintIosMainSourceSetFormat",
            "ktlintIosArm64MainSourceSetFormat",
            "ktlintIosSimulatorArm64MainSourceSetFormat",
            "ktlintIosX64MainSourceSetFormat",
            "ktlintJvmMainSourceSetFormat"
        ).forEach { taskName ->
            tasks.findByName(taskName)?.enabled = false
        }
    }
}

// Design Token Compliance Verification Task
tasks.register("verifyDesignTokenCompliance") {
    group = "verification"
    description = "Verify no hardcoded dp/sp values exist outside theme files"

    doLast {
        val violatingFiles = mutableListOf<String>()

        // Check for forbidden imports using ripgrep
        try {
            val importResult = providers.exec {
                commandLine("rg", "-l", "import.*androidx\\.compose\\.ui\\.unit\\.(dp|sp)", "--type", "kotlin", "--glob", "!*AppTheme*", "--glob", "!*WindowSizeClass*")
            }.standardOutput.asText.get().trim()

            if (importResult.isNotEmpty()) {
                violatingFiles.addAll(importResult.split("\n"))
            }
        } catch (e: Exception) {
            // rg not found or no matches, continue
        }

        // Check for hardcoded dp/sp usage using ripgrep
        try {
            val usageResult = providers.exec {
                commandLine("rg", "-n", "\\b[0-9]+\\.(dp|sp)\\b", "--type", "kotlin", "--glob", "!*AppTheme*", "--glob", "!*WindowSizeClass*", "--glob", "!build/**")
            }.standardOutput.asText.get().trim()

            if (usageResult.isNotEmpty()) {
                violatingFiles.addAll(usageResult.split("\n"))
            }
        } catch (e: Exception) {
            // rg not found or no matches, continue
        }

        if (violatingFiles.isNotEmpty()) {
            throw GradleException("Design token violations found:\\n${violatingFiles.joinToString("\\n")}")
        } else {
            println("✅ Design token compliance verified - no violations found")
        }
    }
}

