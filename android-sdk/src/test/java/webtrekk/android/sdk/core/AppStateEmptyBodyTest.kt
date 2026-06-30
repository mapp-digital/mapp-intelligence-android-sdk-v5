package webtrekk.android.sdk.core

import android.app.Activity
import android.os.Bundle
import io.mockk.mockk
import org.junit.Test

/**
 * Tests for LifecycleWrapper lifecycle overrides that contain explanatory comments
 * instead of silent empty bodies (the SonarQube fix was adding those comments).
 *
 * The no-op comment bodies live in LifecycleWrapper (the base class).
 * The concrete subclasses (AppStateImpl, ActivityAppStateImpl) OVERRIDE onActivityStarted
 * with real logic that requires lifecycleReceiver to be initialised — those are integration
 * concerns tested elsewhere (androidTest).  Here we verify only the base-class no-ops.
 */
internal class AppStateEmptyBodyTest {

    private val activity = mockk<Activity>(relaxed = true)
    private val bundle = mockk<Bundle>(relaxed = true)

    // A minimal concrete LifecycleWrapper that does not override the no-op methods,
    // so we exercise exactly the commented-out bodies from the SonarQube fix.
    private val wrapper = object : LifecycleWrapper() {}

    @Test
    fun `LifecycleWrapper onActivityStarted does not throw`() {
        wrapper.onActivityStarted(activity)
    }

    @Test
    fun `LifecycleWrapper onActivityResumed does not throw`() {
        wrapper.onActivityResumed(activity)
    }

    @Test
    fun `LifecycleWrapper onActivityPaused does not throw`() {
        wrapper.onActivityPaused(activity)
    }

    @Test
    fun `LifecycleWrapper onActivitySaveInstanceState does not throw`() {
        wrapper.onActivitySaveInstanceState(activity, bundle)
    }

    // Verify that DisabledStateImpl (which also has no overrides) behaves as a no-op
    @Test
    fun `DisabledStateImpl onActivityStarted does not throw`() {
        val disabled = DisabledStateImpl()
        disabled.onActivityStarted(activity)
    }

    @Test
    fun `DisabledStateImpl onActivityResumed does not throw`() {
        val disabled = DisabledStateImpl()
        disabled.onActivityResumed(activity)
    }

    @Test
    fun `DisabledStateImpl onActivityPaused does not throw`() {
        val disabled = DisabledStateImpl()
        disabled.onActivityPaused(activity)
    }

    @Test
    fun `DisabledStateImpl onActivitySaveInstanceState does not throw`() {
        val disabled = DisabledStateImpl()
        disabled.onActivitySaveInstanceState(activity, bundle)
    }
}
