# Change Log
## Version 5.1.12-alpha01 *(UNRELEASED)*
* Updated targetSdk to version 35
* Updated android gradle plugin to version 8.10.1
* Updated kotlin version to 2.1.21
* Updated 3rd party dependencies to the latest versions
* Updated gradle files to *.kts
* Updated librarie

# Change Log
## Version 5.1.11 *(2024-12-02)*
* Bug fixes and performance improvements

# Change Log
## Version 5.1.10 *(2024-09-25)*
* Changed exception tracking to include 'ct=webtrekk_ignore' as a query parameter in a tracking requests.
* Page name in 'p' parameter for exception tracking request is set to 0.
* This version includes maintenance tasks for our React Native and Flutter plugins.

# Change Log
## Version 5.1.10 *(2024-07-23)*
* 'p' parameter is hardcoded to value 5.0.0
* This version includes maintenance tasks for our React Native and Flutter plugins.

# Change Log
## Version 5.1.9 *(2024-05-15)*
* Fixed bug - duplicate request are sent when application starts with optOut set to true, and there are some requests previously stored in the database.

# Change Log
## Version 5.1.8 *(2024-03-27)*
* Fixed unexpected tracking behaviour for media tracking
* Updated targetSdk to 34
* Updated Gradle v7.x.x to v8.2.2
* Updated 3rd party dependencies for SDK
* (Sample App) Migrated Kotlin synthetics to viewBinding (https://developer.android.com/topic/libraries/view-binding/migration)
* (Sample App) Migrated ExoPlayer2 to Media3 (https://developer.android.com/media/media3/exoplayer/migration-guide)

# Change Log
## Version 5.1.7.2 *(2024-01-10)*
* Fixed bug - everId not generated after anonymousTracking set to false
* Fixed bug - firstAppOpen parameter not properly calculated

# Change Log
## Version 5.1.7.1 *(2023-11-23)*
* Fixed issue of typealias TrackingParameters is not recognised

# Change Log
## Version 5.1.7 *(2023-11-23)*
* Add New Product Status to Android SDK

# Change Log
## Version 5.1.6.1 *(2023-08-17)*
* Hotfix version
* Updated calculation of the Set everId configuration parameter

# Change Log
## Version 5.1.6 *(2023-07-17)*
* Enabled customers to set a temporary session ID in anonymous tracking to improve data quality
* Internal performance improvements

# Change Log
## Version 5.1.5.3 *(2023-05-18)*
* Hotfix version
* Updated WorkManager version to 2.8.1 from previous 2.8.0-rc01 version.
* Updated Room library to version 2.5.1 because of some important fixed included in this release. More about this on official page: https://developer.android.com/jetpack/androidx/releases/room#2.5.1
* Fixed bug when campaign parameters are not sent if anonymous tracking is enabled

## Version 5.1.5.2 *(2023-01-31)*
* Hotfix version
* Updated WorkManager version to 2.8.0-rc01 because current release version has known bug that cause OOM exception.
* Fixed bug when wt_mc parameter is injected into parameter value when media code is mc.

## Version 5.1.5.1 *(2023-01-10)*
* Hotfix version
* Fixed bug that occasionally deleted optional parameters in requests before they were sent
* Fixed database migration exception that happened on some devices
* Added sending requests and re-scheduling SendWorker on every SDK initialization/app open.

## Version 5.1.5 *(2022-11-15)*
* Added additional tracking parameters
  - cs801 >> version of the SDK
  - cs802 >> Platform
* Adopted behaviour for consentless tracking. EverId is not stored or sent when anonymous tracking is enabled. Also, if a user changes from consent to consentless tracking, previously saved everId will be automatically deleted.
* Added option to match users between Engage and Intelligence SDK in order to better target specific customers.
* Added method to get current configuration parameters in runtime. 
* Fixed invalid encoding of the everId parameter on devices with arabic language
* Fixed wrong definition for campaign tracking ("cc" parameters is tracked now)

## Version 5.1.4.2 *(2022-04-18)*
* HotFix version
* Fixed issue for incorrect parsing EventParameters.
* Fixed issue when anonymous suppressed parameters not calculated for some request's types.

## Version 5.1.4.1 *(2022-04-21)*
* HotFix version
* EverID can pre properly set on initialization.

## Version 5.1.4 *(2022-04-21)*
* Changed Webtrekk.reset(Context, Config) method. Removed second parameter.
* Added new methods for updating configuration in runtime
* Fixed bug for uncaught exceptions not tracked

## Version 5.1.3 *(2022-03-15)*
* Updated compile to Java 11
* Updated targetSdkVersion to 31 (Android 12); 
  This also means that application must adopt to the behavioral changes required for applications targeting Android 12.
  See more: <a href="https://developer.android.com/about/versions/12/behavior-changes-12"/>
* Updated all dependencies on latest versions
* Updated unit tests

## Version 5.1.2 *(2022-02-25)*
* New method for resetting SDK in runtime
* New method for triggering immediate data sending and deleting sent data
* Set custom everId through Config Builder

## Version 5.1.1 *(2021-12-14)*
* Stability improvements

## Version 5.1.1-beta3 *(2021-11-25)*
* BugFix: Fixed autoTracking disable

## Version 5.1.1-beta2 *(2021-09-22)*
* Improve: Now the media can contain the name of the page 

## Version 5.1.1-beta1 *(2021-09-2)*
* Improve: Koin is removed from the project

## Version 5.1.0.2 *(2021-08-12)*
* HotFix version
* Fixed DeepLinking tracking 

## Version 5.1.0.1 *(2021-07-20)* 
* HotFix version
* Fixed app first open issue
* Downgrade AppCompat to 1.2
 
## Version 5.1.0 *(2021-06-6)*
* Improve Support for Android 11 
* Added Anonymous Tracking
* Added Option to Include App Version In Every Request 

## Version 5.0.9 *(2021-05-16)*
* Removed JCenter 
* Improve Object tracking 
* Improve code stability
* Improve code comments 

## Version 5.0.8 *(2021-04-29)*
* Move SDK to maven central

## Version 5.0.7 *(2021-04-01)*
* Code improve

## Version 5.0.6 *(2021-03-31)*
* Improve Default Params logic

## Version 5.0.5. *(2021-03-31)*
* Added object tracking
* Added Deep-linking tracking
* Added Campaign tracking

## Version 5.0.4. *(2020-11-02)*
* Bug fix for device update
* Added android 11 support

## Version 5.0.3. *(2020-08-26)*
* Added Migration from version v4 to version v5

## Version 5.0.2. *(2020-07-09)*
* Add Crash Analytics
* Add Media Tracking
* Minor bug fix

## Version 5.0.1.-beta2 *(2020-05-04)*
* Update Koin version.
* Minor bug fix.

## Version 5.0.1.-beta1 *(2020-03-04)*
* Add form tracking. 
* Minor bug fix.

## Version 5.0.0. *(2019-12-03)*
* Add Annotation track configuration support.
* Logger time bug fix.
* Optimised internet communication.
* Logger is now disabled by default. 
* Dependency update and code modernization.
* Minor bug fix.

## Version 5.0.0-beta11 *(2019-10-08)*
* Add Batch support.
* Support disables specific screens auto-tracking.
* Support disable Activity auto-tracking.

## Version 5.0.0-beta10 *(2019-09-17)*
* Add Pixel integration in the android.
* Add AppFirstOpen parameter.
* Fix bug now auto and manual tracking works together on first Android Screen.
* Update dependency.

## Version 5.0.0-beta09 *(2019-08-09)*
* Fix Koin conflict if `startKoin` is called from app side.

## Version 5.0.0-beta08 *(2019-07-26)*
* App Update Event is auto tracked.
* Auto tracking full name activity/fragment support.

## Version 5.0.0-beta07 *(2019-07-11)*
* Add Android API tracking parameter as part of the tracking request.
* Auto track of the app version name & code.
* Add `wt_mc=` to the media code param.

## Version 5.0.0-beta06 *(2019-06-26)*
* Update `kotlin-gradle-plugin` to `1.3.31`.
* Update `WorkManager` to `2.1.0-beta02`, set initial delay to send requests worker.
* Add "TimeZone" query param to the request URL `tz`.
* Min SDK is 21.

## Version 5.0.0-beta04 *(2019-04-23)*
* Support track from App to Web by sending ever Id to Pixel Web SDK.
* Support disable fragments auto tracking in the configurations `disableFragmentsAutoTracking()`.
* Add "language" query param to the request URL `la`.

## Version 5.0.0-beta03 *(2019-03-28)*
* `sendDelay` in the configurations renamed to `requestsInterval`.
* Update WorkManager to 2.0.0.

## Version 5.0.0-beta02 *(2019-03-22)*
* Min SDK is 15.

## Version 5.0.0-beta01 *(2019-03-20)*
* Initial release.
