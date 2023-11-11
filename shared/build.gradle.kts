import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageAppDirTask
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractExecutable
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.library.impl.KotlinLibraryLayoutImpl
import java.io.FileFilter
import org.jetbrains.kotlin.konan.file.File as KonanFile


plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
    id("kotlin-parcelize") // Apply the plugin for Android
    id("com.arkivanov.parcelize.darwin")
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    androidTarget()

    jvm("desktop")



    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )


    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        extraSpecAttributes["resources"] =
            "['src/commonMain/resources/**', 'src/iosMain/resources/**']"

        framework {
            baseName = "shared"
            isStatic = true
            export("dev.icerock.moko:resources:0.23.0")
            export("dev.icerock.moko:graphics:0.9.0") // toUIColor here
        }

    }

    sourceSets {

        val ktorVersion = "2.3.4"
        val sqlDelightVersion = "1.5.5"
        val decomposeVersion = "2.0.2"
        val koinVersion = "3.2.0"
        val napierVersion = "2.6.1"
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation("media.kamel:kamel-image:0.7.3")
                implementation("io.ktor:ktor-client-core:$ktorVersion")

                implementation("io.insert-koin:koin-core:${koinVersion}")
                implementation("io.insert-koin:koin-compose:1.1.0")
                implementation("io.insert-koin:koin-test:${koinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("io.github.aakira:napier:$napierVersion")

                implementation("io.github.xxfast:decompose-router:0.4.0")
                implementation("com.arkivanov.decompose:decompose:${decomposeVersion}")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:${decomposeVersion}")

                implementation("com.arkivanov.essenty:parcelable:1.1.0")
                implementation("com.arkivanov.essenty:lifecycle:1.1.0")


                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                api(compose.materialIconsExtended)
                implementation(compose.ui)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

            }
        }
        val androidMain by getting {

            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                implementation("io.insert-koin:koin-android:${koinVersion}")


            }
            dependsOn(commonMain)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {

            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
                implementation("com.arkivanov.parcelize.darwin:runtime:0.2.1")
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")

            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.5.10")
                implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")

            }
            dependsOn(commonMain)
        }


    }


}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.lightfeather.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }

}
dependencies {


    implementation("androidx.compose.ui:ui-tooling-preview-desktop:1.6.0-alpha08")
    commonMainApi("dev.icerock.moko:resources:0.23.0")
    commonMainApi("dev.icerock.moko:resources-compose:0.23.0") // for compose multiplatform
    commonTestImplementation("dev.icerock.moko:resources-test:0.23.0")

    //MVVM
    commonMainApi("dev.icerock.moko:mvvm-core:0.16.1") // only ViewModel, EventsDispatcher, Dispatchers.UI
    commonMainApi("dev.icerock.moko:mvvm-flow:0.16.1") // api mvvm-core, CFlow for native and binding extensions

    // compose multiplatform
    commonMainApi("dev.icerock.moko:mvvm-compose:0.16.1") // api mvvm-core, getViewModel for Compose Multiplatfrom
    commonMainApi("dev.icerock.moko:mvvm-flow-compose:0.16.1") // api mvvm-flow, binding extensions for Compose Multiplatfrom

    implementation("androidx.compose.ui:ui-tooling-preview-android:1.5.1")

}



multiplatformResources {
    multiplatformResourcesPackage = "com.lightfeather.masarify" // required
    disableStaticFrameworkWarning = true

}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
tasks.withType<KotlinNativeLink>()
    .matching { linkTask -> linkTask.binary is AbstractExecutable }
    .configureEach {
        val task: KotlinNativeLink = this

        doLast {
            val binary: NativeBinary = task.binary
            val outputDir: File = task.outputFile.get().parentFile
            task.libraries
                .filter { library -> library.extension == "klib" }
                .filter(File::exists)
                .forEach { inputFile ->
                    val klibKonan = KonanFile(inputFile.path)
                    val klib = KotlinLibraryLayoutImpl(
                        klib = klibKonan,
                        component = "default"
                    )
                    val layout = klib.extractingToTemp

                    // extracting bundles
                    layout
                        .resourcesDir
                        .absolutePath
                        .let(::File)
                        .listFiles(FileFilter { it.extension == "bundle" })
                        // copying bundles to app
                        ?.forEach { bundleFile ->
                            logger.info("${bundleFile.absolutePath} copying to $outputDir")
                            bundleFile.copyRecursively(
                                target = File(outputDir, bundleFile.name),
                                overwrite = true
                            )
                        }
                }
        }
    }

tasks.withType<AbstractNativeMacApplicationPackageAppDirTask> {
    val task: AbstractNativeMacApplicationPackageAppDirTask = this

    doLast {
        val execFile: File = task.executable.get().asFile
        val execDir: File = execFile.parentFile
        val destDir: File = task.destinationDir.asFile.get()
        val bundleID: String = task.bundleID.get()

        val outputDir = File(destDir, "$bundleID.app/Contents/Resources")
        outputDir.mkdirs()

        execDir.listFiles().orEmpty()
            .filter { it.extension == "bundle" }
            .forEach { bundleFile ->
                logger.info("${bundleFile.absolutePath} copying to $outputDir")
                bundleFile.copyRecursively(
                    target = File(outputDir, bundleFile.name),
                    overwrite = true
                )
            }
    }
}