# Change Log

## Version 5.0.0-beta08 *(in progress)*
* App Update Event is auto tracked.

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