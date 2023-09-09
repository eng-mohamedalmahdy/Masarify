buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
        maven { url = uri("https://jetbrains.bintray.com/intellij-third-party-dependencies") }
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    }
    dependencies {
        classpath ("com.squareup.sqldelight:gradle-plugin:1.5.1")
        classpath ("dev.icerock.moko:resources-generator:0.23.0")
        classpath("com.arkivanov.parcelize.darwin:gradle-plugin:0.2.1")

    }
}
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)

}