package webtrekk.android.sdk.module

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.core.ActivityAppStateImpl
import webtrekk.android.sdk.core.AppStateImpl
import webtrekk.android.sdk.core.DisabledStateImpl
import webtrekk.android.sdk.core.FragmentStateImpl
import webtrekk.android.sdk.data.entity.DataAnnotationClass

/**
 * Tests for the provideAppState() logic — validates the refactored chained-if → when
 * returns the correct AppState implementation for each configuration combination.
 *
 * Because provideAppState() is private we invoke it via reflection. The when expression must
 * produce identical results to the original if/else chain.
 */
internal class InjectionModuleTest {

    @Before
    fun setUp() {
        mockkObject(LibraryModule)
        mockkObject(InteractorModule)
        every { InteractorModule.sessions } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    private fun mockConfig(fragments: Boolean, activity: Boolean): Config {
        val config = mockk<Config>(relaxed = true)
        every { config.fragmentsAutoTracking } returns fragments
        every { config.activityAutoTracking } returns activity
        return config
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeProvideAppState(): webtrekk.android.sdk.core.AppState<DataAnnotationClass> {
        val method = AppModule::class.java.getDeclaredMethod("provideAppState")
        method.isAccessible = true
        return method.invoke(AppModule) as webtrekk.android.sdk.core.AppState<DataAnnotationClass>
    }

    @Test
    fun `fragments=true and activity=true returns AppStateImpl`() {
        every { LibraryModule.configuration } returns mockConfig(fragments = true, activity = true)
        assertThat(invokeProvideAppState()).isInstanceOf(AppStateImpl::class.java)
    }

    @Test
    fun `fragments=true and activity=false returns FragmentStateImpl`() {
        every { LibraryModule.configuration } returns mockConfig(fragments = true, activity = false)
        assertThat(invokeProvideAppState()).isInstanceOf(FragmentStateImpl::class.java)
    }

    @Test
    fun `fragments=false and activity=true returns ActivityAppStateImpl`() {
        every { LibraryModule.configuration } returns mockConfig(fragments = false, activity = true)
        assertThat(invokeProvideAppState()).isInstanceOf(ActivityAppStateImpl::class.java)
    }

    @Test
    fun `fragments=false and activity=false returns DisabledStateImpl`() {
        every { LibraryModule.configuration } returns mockConfig(fragments = false, activity = false)
        assertThat(invokeProvideAppState()).isInstanceOf(DisabledStateImpl::class.java)
    }
}
