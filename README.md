# Webtrekk Android SDK v5 **[BETA]**
Webtrekk Android SDK is used to integrate Webtrekk tracking systems with your Android apps. Collect your app usage, track how your users are using your app, define specific pages and custom events. And send the data to Webtrekk analytics servers to be used for further analysis.

Webtrekk Android SDK v5 is built in [Kotlin](https://kotlinlang.org/) and uses [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) under the hood, [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for enqueuing and sending the track requests to optimize the device battery and performance.
Webtrekk internally, collects and caches the data that you specify for tracking, and later, it sends those data to Webtrekk analytics server in periodic time.

# Contents
* [Installation](#installation)
* [Usage](#usage)
    * [Configuration](#configuration)
        * [WorkManager Constraints](#workmanager_constraints)
        * [OkHttpClient Builder](#okhttpclient_builder)
        * [Default Configuration](#default_configuration)
    * [Initialize](#initialize)
    * [Tracking](#tracking)
        * [Auto Track](#auto_track)
        * [Manual Track](#manual-track)
        * [Track Custom Page](#track-custom-page)
        * [Track Custom Event](#track-custom-event)
    * [Custom Params](#custom-params)
    * [Opt Out](#opt-out)
    * [User Ever Id](#user-ever-id)
* [Read more](#read-more)
* [License](#license)

<a name=installation></a>
# Installation
In progress

<a name=usage></a>
# Usage

<a name=configuration></a>
## Configuration
To start integrating with Webtrekk, [WebtrekkConfiguration](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/WebtrekkConfiguration.kt) is the entry point where you can set up all your configurations that will be used by the SDK. It's recommended to set up the configurations in your [Application](https://developer.android.com/reference/android/app/Application) class. Note that `trackIds` and `trackDomain` are the mandatory.

```kotlin
val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("track Id"), trackDomain = "track domain")
       .logLevel(Logger.Level.BASIC)
       .sendDelay(TimeUnit.HOURS, 12) // The interval time for sending the requests
       .disableAutoTracking() // Auto tracking is enabled by default
       .build()
```

<a name=workmanager_constraints></a>
### WorkManager Constraints
Since the SDK uses WorkManager for enqueueing the requests (cached data) and sending them in [Config.sendDelay](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt) time in the background in order. It guarantees to send those data requests in periodic time even if your app is not in the background, and that's for enhancing your app's usage battery and that you don't have to worry about the performance.
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

<a name=okhttpclient_builder></a>
### OkHttpClient Builder
Also, you can customize the [okHttpClient](https://github.com/square/okhttp) used in the SDK, to give your more freedom of setting up your pinning certificates, interceptors...etc.
```kotlin
val okHttpClient = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .build()
            
val webtrekkConfiguration = WebtrekkConfiguration.Builder(trackIds = listOf("track Id"), trackDomain = "track domain")
            .okHttpClient(okHttpClient = okHttpClient) 
            .build()   
```

<a name=default_configuration></a>
### Default Configuration
Only `trackIds` and `trackDomain` are the mandatory to be defined in the configurations, all other configurations have default values.
Check out [DefaultConfiguration](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/DefaultConfiguration.kt).

<a name=initialize></a>
## Initialize
To start using [Webtrekk](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Webtrekk.kt), you must retrieve an instance first and then initialize the context [Context](https://developer.android.com/reference/android/content/Context) and Webtrekk configurations [Config](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt). Without specifying the context nor the configurations first, Webtrekk will throw [IllegalStateException](https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/IllegalStateException.html) upon invoking any method.

```kotlin
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val webtrekkConfigurations =
            WebtrekkConfiguration.Builder(listOf("track Id"), "track domain")
                .logLevel(Logger.Level.BASIC)
                .sendDelay(TimeUnit.HOURS, 12)
                .disableAutoTracking()
                .build()

        Webtrekk.getInstance().init(this, webtrekkConfigurations)
    }
}
```
Make sure your app's manifest has the network permission.
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

<a name=tracking></a>
## Tracking

<a name=auto_track></a>
### Auto Track
At minimum usage, just initializing the SDK without disabling the auto track, the SDK will start tracking your activities and fragments, sending them to Webtrekk's server. Note, that auto track is enabled by default, to disable auto track, call `disableAutoTracking()` in configurations.

<a name=manual_track></a>
### Manual Track
In case of disabling the auto track, and interested in specific activities/fragments, use `trackPage()` method in your activities/fragments.
Note, If auto tracking [Config.autoTracking](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/Config.kt) is enabled, then this function will return immediately without sending its tracking data, alongside with a warning indicating that auto tracking is enabled. Also, you can define some custom tracking params to be sent within this page. Please check out how to define [tracking params](#custom_params) below.

```kotlin
Webtrekk.getInstance().trackPage(context = this, customPageName = "Product activity")
```

<a name=track_custom_page></a>
### Track Custom Page
Tracks a custom page, with some custom tracking params. Can be used alongside the auto tracking.

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

<a name=track_custom_event></a>
### Track Custom Event
Tracks a specific custom event, with some custom tracking params. Can be used alongside the auto tracking.

```kotlin
val trackingParams = TrackingParams()
trackingParams.putAll(
            mapOf(
                Param.EVENT_CLICK to "true"
            )
        )

Webtrekk.getInstance().trackCustomEvent(eventName = "Event campaign clicks", trackingParams = trackingParams)
``` 

<a name=custom_params></a>
## Custom Params
You can define params to be sent with every manual track or custom page/event track. There are some predefined params in the SDK
in [Params](https://github.com/Neno0o/webtrekk-new-android-sdk/blob/master/android-sdk/src/main/java/webtrekk/android/sdk/ParamType.kt). In case you want to define your custom params, you can use `TrackingParams`, see the example below.

In kotlin: 

```kotlin
// First, define your custom params (names/keys) as extension properties of [Param], for consistency and code convention, check out the corresponding name in your server. For example, if that param has key in your server with name "cp100", then you can define it in your app in this way.
val Param.BACKGROUND_COLOR
    inline get() = customParam(ParamType.PAGE_PARAM, 100)
    
// Add your custom params to [TrackingParams] object, which is a map of custom params name(keys) you defined with their values.
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
// Define your custom params (names/keys) as they are in the server. For example, if that param has key in your server with name "cp100", then you can define it in your app in this way.
private static final String BACKGROUND_PARAM = createCustomParam(ParamType.PAGE_CATEGORY, 100);

// Construct a map, mapping your servers (names/keys) to the value.
Map<String, String> params = new LinkedHashMap<>();
params.put(BACKGROUND_PARAM, "blue");

// Send that map object to Webtrekk, in Manual tracking or Page/Event tracking.
Webtrekk.getInstance().trackCustomPage("Product Page", params);
```

<a name=opt_out></a>
## Opt Out
The SDK allows to opt out entirely from tracking. Internally, calling this method will cause to delete all the current tracking data that are cached in the database (if `sendCurrentData` is false), canceling sending requests, shutting down work manager's worker and disabling all incoming tracking requests.

To opt out entirely and delete all caching data without sending them.
```kotlin
Webtrekk.getInstance().optOut(value = true, sendCurrentData = false)
```

To send current caching data before opting out.
```kotlin
Webtrekk.getInstance().optOut(value = true, sendCurrentData = true)
```

To opt in again.
```kotlin
Webtrekk.getInstance().optOut(value = false)
```

To check if opt out is active or not.
```kotlin
Webtrekk.getInstance().hasOptOut() // Will return true if opt out is active
```

<a name=user_ever_id></a>
## User Ever Id
The SDK generates a unique ID for each user at first initializing the SDK. That ID is being sent at ever track request to identify the user.
To retrieve the Ever Id.

```kotlin
Webtrekk.getInstance().getEverId()
```

<a name=read_more></a>
# Read more
Check out the docs on the site to learn more about tracking and uses cases.

<a name=license></a>
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
