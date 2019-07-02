# Releasing

* All branches must be merged to `master` except `release`.
* Merge `master` to `release`.
* Travis job will start building `release` branch, and after success, it will deploy automatically the artifacts of current version x.y.z to Bintray.
* After deploying successfully, `release` branch, update `README` with latest download versions and in `CHANGELOG`, change `in progress` to the release date. The commit message "released version x.y.z".
* Merge `release` to `master`.
* Starting a new fresh development on `master`, open `gradle.properties` and increment the library version, so it's the current version in progress. Also, open `CHANGELOG` and mention that version with `(in progress)`. And the commit message "start development version x.y.z".
* To start a new release of current version, go to the first step with commit message "prepare version x.y.z to release".