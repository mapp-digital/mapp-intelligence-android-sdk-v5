# [Webtrekk Android SDK v5](https://webtrekk.github.io/webtrekk-android-sdk-v5/) [![Build Status](https://travis-ci.com/Webtrekk/webtrekk-android-sdk-v5.svg?branch=master)](https://travis-ci.com/Webtrekk/webtrekk-android-sdk-v5)

[Site](https://mapp.com/) |
[Docs](https://docs.mapp.com/display/ASVN) |
[Support](https://support.webtrekk.com/)

Webtrekk Android SDK is used to integrate Webtrekk tracking systems with your Android apps. Collect meaningful data about how your apps are used, track how your users interact with your app, how they view specific pages, and custom events. Based on the tracking data from apps different indicators can be measured, which are already known from the web analytics, such as page impressions, events, screen size, operating system, e-commerce tracking, etc.

Webtrekk Android SDK v5 is written entirely in [Kotlin](https://kotlinlang.org/) and uses [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for non-blocking executions, [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for enqueuing and sending the track requests to optimize the device battery and app performance.
Webtrekk internally, collects and caches the data that you specify for tracking, and later, it sends those data to Webtrekk analytic servers in periodic times.


# Installation
Gradle
```groovy
implementation 'com.webtrekk.webtrekksdk:webtrekksdk-android:5.0.3'
```

Maven
```xml
<dependency>
	<groupId>com.webtrekk.webtrekksdk</groupId>
	<artifactId>webtrekksdk-android</artifactId>
	<version>5.0.4</version>
	<type>pom</type>
</dependency>
```

The SDK requires that you enable Java 8 in your builds.
```groovy
compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
}
```

Allow the network permission in your app manifest.
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

The SDK supports min Android SDK (21).

Note that the SDK uses [AndroidX](https://developer.android.com/jetpack/androidx), make sure to migrate your app to [AndroidX Migration](https://developer.android.com/jetpack/androidx#using_androidx) to avoid Manifest merger failure.



# Read more
Check out the [docs](https://docs.webtrekk.com/display/ASVN) on the site to learn more about tracking server and custom params. 
We have implemented camera, push and form tracking in our [Test Application](https://github.com/Webtrekk/Android-advanced-test-application) 

# Contributing
Please check out our contributing guide before you start [here](https://github.com/Webtrekk/webtrekk-android-sdk-v5/blob/fc910d5dc6da3d3e289a1cc57bc281be0e34b5da/CONTRIBUTING.md).

# License
MIT License

Copyright (c) 2019 Webtrekk GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

