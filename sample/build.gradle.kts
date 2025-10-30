import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties().apply {
    val localFile = project.rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

val trackDomain = localProperties.getProperty("trackDomain").toString()
val trackIds = localProperties.getProperty("trackIds").toLong()

android {
    namespace = "com.example.webtrekk.androidsdk"
    compileSdk = 36
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.example.webtrekk.androidsdk"
        minSdk = 23
        targetSdk = 35
        versionCode = 7
        versionName = "1.0.11"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(type = "String", name = "TRACK_DOMAIN", "\"$trackDomain\"")
        buildConfigField(type = "String", name = "TRACK_IDS", "\"$trackIds\"")
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17) // Configure Java Toolchain for JDK 17
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17) // Set JVM target within compilerOptions
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    lint {
        abortOnError = false
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    implementation(libs.kotlin)
    implementation(libs.bundles.base)
    implementation(libs.bundles.ui.components)
    implementation(libs.bundles.coil)
    implementation(libs.viewmodel.ktx)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.android.material)
    implementation(libs.constraint.layout)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.work.manager)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.bundles.media3)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

    implementation(libs.mapp.android.engage)
    //implementation(libs.mapp.android.intelligence)
    implementation(project(":android-sdk"))

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.android.test)
}