
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
    id("kotlin-parcelize") // Apply the plugin for Android
    id("com.arkivanov.parcelize.darwin")

//    id("dev.icerock.mobile.multiplatform-resources")

}

kotlin {
    android()

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries.framework {
//            baseName = "shared"
//            export("dev.icerock.moko:resources:0.22.3")
//            export("dev.icerock.moko:graphics:0.9.0")
//        }
//    }

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] =
            "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val sqlDelightVersion = "1.5.5"
        val decomposeVersion = "2.0.2"
        val commonMain by getting {
            dependencies {

                implementation("io.github.xxfast:decompose-router:0.4.0")
                implementation("com.arkivanov.decompose:decompose:${decomposeVersion}")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:${decomposeVersion}")

                implementation("com.arkivanov.essenty:parcelable:1.1.0")
                implementation("com.arkivanov.essenty:lifecycle:1.1.0")
                //         api("dev.icerock.moko:resources:0.23.0")
                //       api("dev.icerock.moko:resources-compose:0.23.0") // for compose multiplatform
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
                implementation("com.arkivanov.parcelize.darwin:runtime:0.2.1")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
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
//    commonMainApi("dev.icerock.moko:resources:0.23.0")
//    commonMainApi("dev.icerock.moko:resources-compose:0.23.0") // for compose multiplatform
//    commonTestImplementation("dev.icerock.moko:resources-test:0.23.0")

    implementation("androidx.compose.ui:ui-tooling-preview-android:1.5.1")
    implementation(project(mapOf("path" to ":core")))

}


//multiplatformResources {
//    multiplatformResourcesPackage = "com.lightfeather.masarify.res" // required
//}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}