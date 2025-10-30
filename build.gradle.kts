// Top-level build file, contains configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        maven("https://jitpack.io")
        mavenCentral()
    }
    dependencies {
        classpath("com.github.tafilovic:central-portal-publisher:2.0.4")
    }
}

plugins {
    id("com.android.application") version "8.13.0" apply false
    id("com.android.library") version "8.13.0" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    id("com.google.firebase.appdistribution") version "5.1.1" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.3.0" apply false
}

gradle.projectsEvaluated {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}