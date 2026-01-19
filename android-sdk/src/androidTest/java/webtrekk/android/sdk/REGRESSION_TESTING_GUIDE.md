# Regression Testing Guide for Webtrekk Android SDK

## What are Regression Tests?

**Regression tests** are tests that verify that previously working functionality continues to work correctly after code changes. They are designed to:

1. **Prevent breaking changes**: Ensure new features or bug fixes don't break existing functionality
2. **Maintain stability**: Catch regressions (bugs that appear after changes) early
3. **Document expected behavior**: Serve as living documentation of how the SDK should work
4. **Enable confident refactoring**: Allow developers to refactor code knowing tests will catch issues

## Why Regression Tests Matter for This SDK

The Webtrekk SDK is a **critical infrastructure library** that:
- Tracks user behavior and analytics data
- Handles sensitive user data (privacy compliance)
- Works in background (WorkManager, coroutines)
- Persists data (SharedPreferences, Room database)
- Sends network requests (API communication)

A bug in any of these areas could:
- Lose tracking data
- Violate privacy regulations (GDPR)
- Impact app performance
- Break customer integrations

## Types of Regression Tests for This SDK

### 1. **API Contract Tests**
Verify that public API methods work as documented and don't change behavior unexpectedly.

### 2. **End-to-End Integration Tests**
Test complete workflows from initialization to data sending.

### 3. **Data Persistence Tests**
Ensure data is correctly saved and retrieved across app restarts.

### 4. **Concurrency Tests**
Verify thread-safety and proper handling of concurrent operations.

### 5. **Backward Compatibility Tests**
Ensure existing integrations continue to work after SDK updates.

## How to Write Regression Tests

### Test Structure

Regression tests should be:
- **Comprehensive**: Cover all critical paths
- **Deterministic**: Produce consistent results
- **Fast**: Run quickly to enable frequent testing
- **Isolated**: Don't depend on external services or previous test state
- **Maintainable**: Easy to understand and update

### Best Practices

1. **Use descriptive test names** that explain what is being tested
2. **Test one thing per test** - makes failures easier to diagnose
3. **Use setup/teardown** to ensure clean state between tests
4. **Mock external dependencies** (network, system services)
5. **Test both happy paths and edge cases**
6. **Include timing/performance assertions** where relevant

## Test Organization

```
android-sdk/src/androidTest/java/webtrekk/android/sdk/
├── regression/
│   ├── RegressionTestSuite.kt          # Main regression test suite
│   ├── api/
│   │   ├── InitializationRegressionTest.kt
│   │   ├── TrackingRegressionTest.kt
│   │   └── ConfigurationRegressionTest.kt
│   ├── persistence/
│   │   ├── EverIdPersistenceRegressionTest.kt
│   │   └── SessionPersistenceRegressionTest.kt
│   ├── integration/
│   │   ├── EndToEndTrackingRegressionTest.kt
│   │   └── WorkManagerIntegrationRegressionTest.kt
│   └── compatibility/
│       └── BackwardCompatibilityRegressionTest.kt
```

## Running Regression Tests

```bash
# Run all regression tests
./gradlew :android-sdk:connectedAndroidTest --tests "webtrekk.android.sdk.regression.*"

# Run specific regression test suite
./gradlew :android-sdk:connectedAndroidTest --tests "webtrekk.android.sdk.regression.api.*"

# Run with verbose output
./gradlew :android-sdk:connectedAndroidTest --tests "webtrekk.android.sdk.regression.*" --info
```

## When to Add Regression Tests

Add regression tests when:
1. **Fixing a bug** - Add a test that reproduces the bug, then verify the fix
2. **Adding new features** - Ensure new code doesn't break existing features
3. **Refactoring** - Verify behavior remains unchanged
4. **After production issues** - Document and prevent recurrence

## Maintenance

- **Review regularly**: Update tests when requirements change
- **Keep tests passing**: Fix or remove flaky tests
- **Document failures**: When a regression test fails, document why
- **Version control**: Track test changes alongside code changes
