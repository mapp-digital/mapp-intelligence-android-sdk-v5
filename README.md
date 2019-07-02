# Webtrekk Android SDK v5 **[BETA]**
Webtrekk Android SDK is used to integrate Webtrekk tracking systems with your Android apps. Collect meaningful data about how your apps are used, track how your users interact with your app, how they view specific pages, and custom events. Based on the tracking data from apps different indicators can be measured, which are already known from the web analytics, such as page impressions, events, screen size, operating system, e-commerce tracking, etc.

Webtrekk Android SDK v5 is written entirely in [Kotlin](https://kotlinlang.org/) and uses [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for non-blocking executions, [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for enqueuing and sending the track requests to optimize the device battery and app performance.
Webtrekk internally, collects and caches the data that you specify for tracking, and later, it sends those data to Webtrekk analytic servers in periodic times.

# Contents
- [Installation](#installation)
- [Usage](#usage)
    - [Configuration](#configuration)
        - [Default Configuration](#default-configuration)
        - [WorkManager Constraints *(Optional)*](#workmanager-constraints) 
        - [OkHttpClient Builder *(Optional)*](#okhttpclient-builder)
    - [Initialization](#initialization)
    - [Tracking](#tracking)
        - [Auto Track](#auto-track)
        - [Manual Track](#manual-track)
        - [Track Custom Page](#track-custom-page)
        - [Track Custom Event](#track-custom-event)
    - [Custom Params](#custom-params)
    - [Opt Out](#opt-out)
    - [User Ever Id](#user-ever-id)
    - [App to Web](#app-to-web)
- [Read more](#read-more)
- [Contributing](#contributing)
- [License](#license)

# Installation
Gradle
```groovy
implementation 'com.webtrekk.webtrekksdk:webtrekksdk-android:5.0.0-beta06'
```

Maven
```xml
<dependency>
	<groupId>com.webtrekk.webtrekksdk</groupId>
	<artifactId>webtrekksdk-android</artifactId>
	<version>5.0.0-beta06</version>
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

# Usage

## Configuration
[WebtrekkConfiguration](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/WebtrekkConfiguration.kt) is the entry point where you can set up all your configurations that will be used by the SDK. It's recommended to set up the configurations in [Application](https://developer.android.com/reference/android/app/Application) class. Note that `trackIds` and `trackDomain` are mandatory.

```kotlin
val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("track Id"), trackDomain = "track domain")
       .logLevel(Logger.Level.BASIC)
       .requestsInterval(TimeUnit.MINUTES, 15) // The periodic time for sending the cached tracking data to the server
       .disableAutoTracking() // Auto tracking is enabled by default
       .build()
```

### Default Configuration
`trackIds` and `trackDomain` are the mandatory to be defined in the configurations, all other configurations have default values which you can override their values.
Check out [DefaultConfiguration](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/DefaultConfiguration.kt).

### WorkManager Constraints *(Optional)*
The SDK uses [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for scheduling and sending the cached tracking data (requests) in periodic times [Config.requestsInterval](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt) in the background. It guarantees to execute if your app exits or even if the app is not in the background, and that's to enhance the device battery and the overall performance.
You can customize the WorkManager constraints. Also check out the default constraints [DefaultConfiguration](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/DefaultConfiguration.kt).
```kotlin
val workManagerConstraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("track Id"), trackDomain = "track domain")
            .workManagerConstraints(constraints = workManagerConstraints)   
            .build()
```

### OkHttpClient Builder *(Optional)*
You can override the [okHttpClient](https://github.com/square/okhttp) used in the SDK, to setup certificates pinning, interceptors...etc.
```kotlin
val okHttpClient = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .build()
            
val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("track Id"), trackDomain = "track domain")
            .okHttpClient(okHttpClient = okHttpClient) 
            .build()   
```
## Initialization
Obtain an instance of [Webtrekk](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Webtrekk.kt) `Webtrekk.getInstance()`. Provide the context [Context](https://developer.android.com/reference/android/content/Context) and Webtrekk configurations [Config](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt) to `Webtrekk.getInstance().init(this, webtrekkConfigurations)`. Without context or configurations, Webtrekk will throw [IllegalStateException](https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/IllegalStateException.html) upon invoking any method.
It's recommended to init [Webtrekk](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Webtrekk.kt) in [Application](https://developer.android.com/reference/android/app/Application) class.

```kotlin
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val webtrekkConfigurations =
            WebtrekkConfiguration.Builder(listOf("track Id"), "track domain")
                .logLevel(Logger.Level.BASIC)
                .requestsInterval(TimeUnit.MINUTES, 15)
                .disableAutoTracking()
                .build()

        Webtrekk.getInstance().init(this, webtrekkConfigurations)
    }
}
```

## Tracking

### Auto Track
At the minimum usage, by just initializing the SDK [Webtrekk](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Webtrekk.kt) without disabling the auto track in the configurations, the SDK will start automatically tracking your activities and fragments, caching the data, and later send the data to the servers. Note, that auto track is enabled by default, to disable auto track of (activities and fragments), call `disableAutoTracking()` in configurations. If you want to disable auto track for fragments only, call `disableFragmentsAutoTracking()`.

```kotlin
val webtrekkConfigurations =
            WebtrekkConfiguration.Builder(listOf("track Id"), "track domain")
                .logLevel(Logger.Level.BASIC)
                .build()

Webtrekk.getInstance().init(this, webtrekkConfigurations) // (Minimum usage) Auto track is enabled by default
```

### Manual Track
By default, auto tracking will track every page (activities/fragments) in the app. To track specific pages (activities/fragments), disable auto track first in the configuration `disableAutoTrack()`, then call `trackPage()` method within your activities/fragments.
Note, calling `trackPage()` while auto track [Config.autoTracking](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt) is still enabled, then this function will return immediately, alongside with a warning indicating that auto tracking is enabled.
If you want to override the activity/fragment name *(optional)*, then set up the desired name in `customPageName`.
You can define some custom tracking params *(optional)* that will be attached within this tracking page. Please check out how to define [tracking params](#custom_params) below.

```kotlin
Webtrekk.getInstance().trackPage(context = this, customPageName = "Product activity", trackingParams = emptyMap())
```

### Track Custom Page
Tracks a custom page, with custom tracking params. It works separately from other trackers.

```kotlin
val trackingParams = TrackingParams()
trackingParams.putAll(
            mapOf(
                Param.INTERNAL_SEARCH to "search",
                Param.BACKGROUND_COLOR to "blue",
                Param.TRACKING_LOCATION to "my new location"
            )
        )

Webtrekk.getInstance().trackCustomPage(pageName = "First page", trackingParams = trackingParams)
``` 

The main difference between `trackCustomPage()` and `trackPage()` is, the latter has `context` as parameter, and most likely, must be called within activity or fragment to track current page, and it can't be used if auto tracking is enabled. Otherwise, some pages (activities/fragments) will be tracked twice.

### Track Custom Event
Tracks a specific custom event, with custom tracking params. It works separately from other trackers.

```kotlin
val trackingParams = TrackingParams()
trackingParams.putAll(
            mapOf(
                Param.EVENT_CLICK to "true"
            )
        )

Webtrekk.getInstance().trackCustomEvent(eventName = "Event campaign clicks", trackingParams = trackingParams)
``` 

## Custom Params
Custom params are additional params that will be attached with manual or custom page/event tracking. There are some predefined params in the SDK
in [Params](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/ParamType.kt). In case you want to define your custom params, you first have to configure them in the server, then init `TrackingParams`, see the example below.

In kotlin: 
```kotlin
// First, define your custom params (names/keys) as extension properties of [Param], for consistency and code convention, check out the corresponding name in the server. For example, if that param has key in server with name "cp100", then you can define it in your app in this way.
val Param.BACKGROUND_COLOR
    inline get() = customParam(ParamType.PAGE_PARAM, 100) // This is equal to "cp100"
    
// Add custom params to [TrackingParams] object, which is a map of custom params name(keys) and their values (that will be tracked).
val trackingParams = TrackingParams()
trackingParams.putAll(
            mapOf(
                Param.BACKGROUND_COLOR to "blue"
            )
        )
    
// Send your trackingParams object to Webtrekk, in Manual tracking or Page/Event tracking.
Webtrekk.getInstance().trackCustomPage("Product Page", trackingParams)
``` 
In Java:
```java
// Define custom params (names/keys) as they are in the server. For example, if that param has key in server with name "cp100", then you can define it in your app in this way.
private static final String BACKGROUND_PARAM = createCustomParam(ParamType.PAGE_PARAM, 100); // This is equal to "cp100"

// Build a map, mapping your servers (names/keys) to the values.
Map<String, String> params = new LinkedHashMap<>();
params.put(BACKGROUND_PARAM, "blue");

// Send that map object to Webtrekk, in Manual tracking or Page/Event tracking.
Webtrekk.getInstance().trackCustomPage("Product Page", params);
```

## Opt Out
The SDK allows to opt out entirely from tracking. Internally, calling this method will cause to delete all the current tracking data that are cached in the database (if `sendCurrentData` is set to false), canceling sending requests, shutting down work manager's worker and disabling all incoming tracking requests.

To opt out entirely and delete all caching data without sending the data to the servers.
```kotlin
Webtrekk.getInstance().optOut(value = true, sendCurrentData = false)
```

To send current caching data to the servers before opting out.
```kotlin
Webtrekk.getInstance().optOut(value = true, sendCurrentData = true)
```

To stop (disable) opting out.
```kotlin
Webtrekk.getInstance().optOut(value = false)
```

To check if opt out is active or not.
```kotlin
Webtrekk.getInstance().hasOptOut() // Returns true if opt out is active
```

## User Ever Id
The SDK generates a unique ID for each end-user at first initializing the SDK. That ID is attached at ever track request to identify the end-user.
To retrieve the Ever Id.

```kotlin
Webtrekk.getInstance().getEverId()
```

## App to Web
To support tracking [WebView](https://developer.android.com/guide/webapps/webview) in apps, the [User Ever Id](#user-ever-id) must be sent to Pixel Web SDK on the web side, to resume tracking of the current user visit.
To achieve this, there are two options:

First: by using [WebtrekkWebInterface](https://github.com/Webtrekk/webtrekk-android-sdk-BETA/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/WebtrekkWebInterface.kt), just pass this object to [WebView](https://developer.android.com/guide/webapps/webview) JavaScript interface.

```kotlin
webView.addJavascriptInterface(WebtrekkWebInterface(Webtrekk.getInstance()), WebtrekkWebInterface.TAG)
```

*Note*: you must enable JavaScript in [WebView](https://developer.android.com/guide/webapps/webview) when uses this feature.

```kotlin
webView.settings.javaScriptEnabled = true
```

Second: by appending `wt_eid` to the Url with [User Ever Id](#user-ever-id).

```kotlin
webView.loadUrl("https://your_website_url.com/?wt_eid=the ever id")
```

# Read more
Check out the [docs](https://docs.webtrekk.com/display/ASVN) on the site to learn more about tracking server and custom params. 

# Contributing
Please check out our contributing guide before you start [here](https://github.com/Webtrekk/webtrekk-android-sdk-BETA/blob/fc910d5dc6da3d3e289a1cc57bc281be0e34b5da/CONTRIBUTING.md).

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
