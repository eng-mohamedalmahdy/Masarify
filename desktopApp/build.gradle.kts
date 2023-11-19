import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":shared"))
                implementation("com.arkivanov.decompose:decompose:2.1.0-compose-experimental")
                implementation("io.github.xxfast:decompose-router:0.5.1")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.1.0-compose-experimental")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}