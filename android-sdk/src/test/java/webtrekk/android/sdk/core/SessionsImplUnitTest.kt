package webtrekk.android.sdk.core

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.After
import org.junit.Test
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.module.AppModule

@OptIn(ExperimentalCoroutinesApi::class)
internal class SessionsImplUnitTest {

    private lateinit var sharedPrefs: WebtrekkSharedPrefs
    private lateinit var trackRequestDao: TrackRequestDao
    private lateinit var sessions: SessionsImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sharedPrefs = mockk(relaxUnitFun = true)
        trackRequestDao = mockk(relaxUnitFun = true)
        every { sharedPrefs.anonymousTracking } returns false
        mockkObject(AppModule)
        every { AppModule.logger } returns mockk(relaxed = true)
        sessions = SessionsImpl(sharedPrefs, trackRequestDao, dispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `setEverId sets new id and updates dao when not anonymous`() = runTest(dispatcher) {
        var everIdBacking: String? = null
        var modeBacking: GenerationMode? = null

        every { sharedPrefs.everId } answers { everIdBacking }
        every { sharedPrefs.everId = any() } answers { everIdBacking = firstArg() }
        every { sharedPrefs.everIdGenerationMode } answers { modeBacking }
        every { sharedPrefs.everIdGenerationMode = any() } answers { modeBacking = firstArg() }

        sessions.setEverId("newId", forceUpdate = true, mode = GenerationMode.AUTO_GENERATED)
        dispatcher.scheduler.advanceUntilIdle()

        assertThat(everIdBacking).isEqualTo("newId")
        assertThat(modeBacking).isEqualTo(GenerationMode.AUTO_GENERATED)
        coVerify(exactly = 1) { trackRequestDao.updateEverId("newId") }
    }

    @Test
    fun `optOut toggles value`() {
        var optOutBacking = false
        every { sharedPrefs.optOut } answers { optOutBacking }
        every { sharedPrefs.optOut = any() } answers { optOutBacking = firstArg() }

        sessions.optOut(true)
        assertThat(optOutBacking).isTrue()
        assertThat(sessions.isOptOut()).isTrue()
    }

    @Test
    fun `isAppUpdated stores version when first run`() {
        val version = "1.2.3"
        io.mockk.every { sharedPrefs.contains(WebtrekkSharedPrefs.APP_VERSION) } returns false
        io.mockk.every { sharedPrefs.appVersion } returns version

        val updated = sessions.isAppUpdated(version)

        assertThat(updated).isFalse()
        verify { sharedPrefs.appVersion = version }
    }

    @Test
    fun `isAppUpdated returns true when version changes`() {
        val oldVersion = "1.0.0"
        val newVersion = "2.0.0"
        io.mockk.every { sharedPrefs.contains(WebtrekkSharedPrefs.APP_VERSION) } returns true
        io.mockk.every { sharedPrefs.appVersion } returns oldVersion

        val updated = sessions.isAppUpdated(newVersion)

        assertThat(updated).isTrue()
        verify { sharedPrefs.appVersion = newVersion }
    }
}
