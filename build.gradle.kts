// Top-level build file, contains configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    id("com.google.firebase.appdistribution") version "5.2.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.0" apply false
    id("com.google.devtools.ksp") version "2.3.0" apply false
    id("io.github.tafilovic.central-portal-publisher") version "2.0.10" apply false
}

gradle.projectsEvaluated {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}