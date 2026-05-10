import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

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
    id("dev.icerock.mobile.multiplatform-resources") version "0.25.2"

    alias(libs.plugins.googleServices) apply false

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

// ==================== Detekt Configuration ====================

plugins.apply("io.gitlab.arturbosch.detekt")

// Configure Detekt globally for all subprojects
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("$rootDir/detekt.yml"))
        baseline = file("$rootDir/detekt-baseline.xml")
        ignoreFailures = false // Fail build on violations
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "11"

        // Set source to scan all source sets in the module
        setSource(files("src"))

        // Exclude generated files and build directories
        exclude(
            "**/build/**",
            "**/generated/**",
            "**/commonMainResourceAccessors/**",
            "**/MR.kt",
            "**/Res.kt",
            "**/*.generated.kt"
        )

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("reports/detekt/${project.name}-detekt.xml"))

            sarif.required.set(true)
            sarif.outputLocation.set(layout.buildDirectory.file("reports/detekt/${project.name}-detekt.sarif"))

            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.file("reports/detekt/${project.name}-detekt.html"))

            txt.required.set(false)
            md.required.set(false)
        }
    }

    dependencies {
        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }
}

// Create report merge tasks
val detektReportMergeXml by tasks.registering(ReportMergeTask::class) {
    group = "verification"
    description = "Merge all detekt XML reports from subprojects"
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    group = "verification"
    description = "Merge all detekt SARIF reports from subprojects"
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

// Wire up subproject detekt tasks to merge tasks after evaluation
gradle.projectsEvaluated {
    subprojects {
        tasks.withType<Detekt>().configureEach {
            finalizedBy(detektReportMergeXml, detektReportMergeSarif)

            detektReportMergeXml.configure {
                input.from(xmlReportFile)
            }

            detektReportMergeSarif.configure {
                input.from(sarifReportFile)
            }
        }
    }
}

// Task to generate HTML from merged XML report
tasks.register<Exec>("detektHtmlReport") {
    group = "verification"
    description = "Generate HTML report from merged detekt XML"

    dependsOn(detektReportMergeXml)

    commandLine(
        "bash", "-c", """
        if [ -f scripts/generate-detekt-html.sh ]; then
            ./scripts/generate-detekt-html.sh
        else
            echo "⚠️  HTML generator script not found at scripts/generate-detekt-html.sh"
            exit 1
        fi
    """.trimIndent()
    )

    // Only run if XML report exists
    onlyIf {
        detektReportMergeXml.get().output.get().asFile.exists()
    }
}

// Main detekt task that runs on all modules
tasks.register("detektAll") {
    group = "verification"
    description = "Run detekt analysis on all modules and generate merged reports (XML, SARIF, HTML)"

    dependsOn(subprojects.map { "${it.path}:detekt" })
    finalizedBy(detektReportMergeXml, detektReportMergeSarif, "detektHtmlReport")

    // Always print report summary, even if task fails
    doFirst {
        println("\n" + "=".repeat(80))
        println("🔍 Running Detekt analysis on all modules...")
        println("=".repeat(80))
    }
}

// Task to print report summary after detekt runs
tasks.register("detektReportSummary") {
    group = "verification"
    description = "Print summary of detekt reports"

    mustRunAfter(detektReportMergeXml, detektReportMergeSarif, "detektHtmlReport")

    doLast {
        val xmlReport = file("build/reports/detekt/merge.xml")
        val sarifReport = file("build/reports/detekt/merge.sarif")
        val htmlReport = file("build/reports/detekt/merge.html")

        println("\n" + "=".repeat(80))
        println("📊 Detekt Reports Generated")
        println("=".repeat(80))

        if (xmlReport.exists()) {
            println("  • XML:   ${xmlReport.absolutePath}")
        }
        if (sarifReport.exists()) {
            println("  • SARIF: ${sarifReport.absolutePath}")
        }
        if (htmlReport.exists()) {
            println("  • HTML:  ${htmlReport.absolutePath}")
            println("\n💡 View HTML report:")
            println("   open ${htmlReport.absolutePath}")
        } else if (xmlReport.exists()) {
            println("  ⚠️  HTML report not generated yet")
            println("     Run: ./scripts/generate-detekt-html.sh")
        }

        println("=".repeat(80) + "\n")
    }
}

// Wire up report summary to run after merge tasks
detektReportMergeXml.configure {
    finalizedBy("detektReportSummary")
}
detektReportMergeSarif.configure {
    finalizedBy("detektReportSummary")
}
