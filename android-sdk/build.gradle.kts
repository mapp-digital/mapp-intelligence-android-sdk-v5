import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("central.portal.publisher")
}

val VERSION = project.findProperty("VERSION_NAME") as String
val PUBLISHED_GROUP_ID = project.findProperty("GROUP") as String
val ARTIFACT = project.findProperty("POM_ARTIFACT_ID") as String?
val LIBRARY_NAME = project.findProperty("POM_NAME") as String?
fun getSdkVersionName(): String = "\"${VERSION}\""


android {
    namespace = "webtrekk.android.sdk"
    compileSdk = 36
    buildToolsVersion = "35.0.0"

    lint {
        targetSdk = 35
        checkReleaseBuilds = false
        abortOnError = false
        textReport = true
    }

    defaultConfig {
        minSdk = 21
        resValue("string", "appoxee_sdk_version", getSdkVersionName())
        buildConfigField(type = "String", name = "VERSION_NAME", value = "\"$VERSION\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("consumer-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
            consumerProguardFiles("consumer-rules.pro")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17) // Configure Java Toolchain for JDK 17
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17) // Set JVM target within compilerOptions
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }

    packaging {
        resources {
            pickFirsts += "META-INF/LICENSE.md"
            pickFirsts += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.appcompat)
    implementation(libs.kotlin)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.work.manager)

    implementation(libs.okhttp)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlintest.runner.junit5)
    testImplementation(libs.junit.jupiter.engine)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.test.mockk)
    //androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.truth)
}

centralPortalPublisher {
    componentName = "release"
    groupId = PUBLISHED_GROUP_ID
    artifactId = ARTIFACT
    version = VERSION
}
