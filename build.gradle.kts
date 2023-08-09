// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
    }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.navigationSafeArgs) apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
}
true // Needed to make the Suppress annotation work for the plugins block