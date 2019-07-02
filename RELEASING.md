# Releasing

1 - All branches must be merged to `master` except `release`.
2 - Merge `master` to `release`.
3 - Travis job will start building `release` branch, and after success, it will deploy automatically the artifacts to bintray.
4 - On `release` branch, update `README` with latest versions and in `CHANGELOG`, change `in progress` to the release date. The commit message "released version x.y.z".
5 - Merge `release` to `master`.
6 - Staring a new fresh development on `master`, open `gradle.properties` and increment the library version, so it's the current version in progress. Also, open `CHANGELOG` and mention that version with `(in progress)`. And the commit message "start development version x.y.z".
7 - To start releasing current version, go to step "1" with commit message "prepare version x.y.z to release".