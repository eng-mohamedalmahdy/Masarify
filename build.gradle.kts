buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
        maven { url = uri("https://jetbrains.bintray.com/intellij-third-party-dependencies") }
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven("https://jogamp.org/deployment/maven/")

    }

    dependencies {
        classpath(libs.gradle.plugin)
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.moko.resources) apply false

}


allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jogamp.org/deployment/maven/")

    }
}