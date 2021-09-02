package webtrekk.android.sdk.module

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.rules.ExpectedException.none
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration

class InjectionModuleTest {

    private val trackIds = listOf("794940687426749")
    private val trackDomain = "http://tracker-int-01.webtrekk.net"

    private lateinit var config: Config
    private lateinit var context: Context

    @Rule
    @JvmField
    val expectedException: ExpectedException = none()

    fun expectUninitializedPropertyAccessException() {
        expectedException.expect(IllegalStateException::class.java)
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()

        config = WebtrekkConfiguration.Builder(trackIds, trackDomain)
            .build()

        Webtrekk.getInstance().init(context, config)
    }

    @Test
    fun initialized_library_test() {
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        webtrekk.optOut(false)
        val optOut = webtrekk.hasOptOut()
        assertSame(optOut::class.java, Boolean::class.java)
    }

    @Test
    fun webtrekk_is_singleton_test() {
        runBlocking {
            val webtrekk1 = coroutineScope { Webtrekk.getInstance() }
            val webtrekk2 = coroutineScope { Webtrekk.getInstance() }
            assertSame(webtrekk1, webtrekk2)
        }
    }

    @Test
    fun library_module_is_singleton_test() {
        val config1 = LibraryModule.configuration
        val config2 = LibraryModule.configuration

        assertSame(config1, config2)
    }

    @Test
    fun database_is_singleton_test() {
        val instance1 = DataModule.database
        val instance2 = DataModule.database

        assertSame(instance1, instance2)
    }

    @Test
    fun trackRequestDao_is_singleton_test() {
        val instance1 = DataModule.trackRequestDao
        val instance2 = DataModule.trackRequestDao

        assertSame(instance1, instance2)
    }

    @Test
    fun customParamsDao_is_singleton_test() {
        val instance1 = DataModule.customParamsDao
        val instance2 = DataModule.customParamsDao

        assertSame(instance1, instance2)
    }

    @Test
    fun trackRequestRepository_is_singleton_test() {
        val instance1 = DataModule.trackRequestRepository
        val instance2 = DataModule.trackRequestRepository

        assertSame(instance1, instance2)
    }

    @Test
    fun customParamsRepository_is_singleton_test() {
        val instance1 = DataModule.customParamsRepository
        val instance2 = DataModule.customParamsRepository

        assertSame(instance1, instance2)
    }

    @Test
    fun syncRequestDataSource_is_singleton_test() {
        val instance1 = DataModule.syncRequestDataSource
        val instance2 = DataModule.syncRequestDataSource

        assertSame(instance1, instance2)
    }

    @Test
    fun sessions_is_singleton_test() {
        val instance1 = InteractorModule.sessions
        val instance2 = InteractorModule.sessions

        assertSame(instance1, instance2)
    }

    @Test
    fun scheduler_is_singleton_test() {
        val instance1 = InteractorModule.scheduler
        val instance2 = InteractorModule.scheduler

        assertSame(instance1, instance2)
    }

    @Test
    fun cacheTrackRequest_is_not_singleton_test() {
        val instance1 = InteractorModule.cacheTrackRequest()
        val instance2 = InteractorModule.cacheTrackRequest()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun cachedDataTracks_is_not_singleton_test() {
        val instance1 = InteractorModule.getCachedDataTracks()
        val instance2 = InteractorModule.getCachedDataTracks()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun cacheTrackRequestWithCustomParams_is_not_singleton_test() {
        val instance1 = InteractorModule.cacheTrackRequestWithCustomParams()
        val instance2 = InteractorModule.cacheTrackRequestWithCustomParams()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun executeRequest_is_not_singleton_test() {
        val instance1 = InteractorModule.executeRequest()
        val instance2 = InteractorModule.executeRequest()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun executePostRequest_is_not_singleton_test() {
        val instance1 = InteractorModule.executePostRequest()
        val instance2 = InteractorModule.executePostRequest()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun clearTrackRequest_is_not_singleton_test() {
        val instance1 = InteractorModule.clearTrackRequest()
        val instance2 = InteractorModule.clearTrackRequest()

        assertNotSame(instance1, instance2)
    }

    @Test
    fun okHttpClient_is_singleton_test() {
        val instance1 = NetworkModule.okHttpClient
        val instance2 = NetworkModule.okHttpClient

        assertSame(instance1, instance2)
    }

    @Test
    fun workManagerConstraints_is_singleton_test() {
        val instance1 = NetworkModule.workManagerConstraints
        val instance2 = NetworkModule.workManagerConstraints

        assertSame(instance1, instance2)
    }

    @Test
    fun workManager_is_singleton_test() {
        val instance1 = NetworkModule.workManager
        val instance2 = NetworkModule.workManager

        assertSame(instance1, instance2)
    }

    @Test
    fun job_is_singleton_test() {
        val instance1 = InteractorModule.job
        val instance2 = InteractorModule.job

        assertSame(instance1, instance2)
    }

    @Test
    fun autoTrack_is_singleton_test() {
        val instance1 = InteractorModule.autoTrack
        val instance2 = InteractorModule.autoTrack

        assertSame(instance1, instance2)
    }

    @Test
    fun manualTrack_is_singleton_test() {
        val instance1 = InteractorModule.manualTrack
        val instance2 = InteractorModule.manualTrack

        assertSame(instance1, instance2)
    }

    @Test
    fun trackCustomPage_is_singleton_test() {
        val instance1 = InteractorModule.trackCustomPage
        val instance2 = InteractorModule.trackCustomPage

        assertSame(instance1, instance2)
    }

    @Test
    fun trackCustomEvent_is_singleton_test() {
        val instance1 = InteractorModule.trackCustomEvent
        val instance2 = InteractorModule.trackCustomEvent

        assertSame(instance1, instance2)
    }

    @Test
    fun trackCustomForm_is_singleton_test() {
        val instance1 = InteractorModule.trackCustomForm
        val instance2 = InteractorModule.trackCustomForm

        assertSame(instance1, instance2)
    }

    @Test
    fun trackCustomMedia_is_singleton_test() {
        val instance1 = InteractorModule.trackCustomMedia
        val instance2 = InteractorModule.trackCustomMedia

        assertSame(instance1, instance2)
    }

    @Test
    fun trackException_is_singleton_test() {
        val instance1 = InteractorModule.trackException
        val instance2 = InteractorModule.trackException

        assertSame(instance1, instance2)
    }

    @Test
    fun trackUncaughtException_is_singleton_test() {
        val instance1 = InteractorModule.trackUncaughtException
        val instance2 = InteractorModule.trackUncaughtException

        assertSame(instance1, instance2)
    }

    @Test
    fun uncaughtExceptionHandler_is_singleton_test() {
        val instance1 = InteractorModule.uncaughtExceptionHandler
        val instance2 = InteractorModule.uncaughtExceptionHandler

        assertSame(instance1, instance2)
    }

    @Test
    fun optOut_is_singleton_test() {
        val instance1 = InteractorModule.optOut
        val instance2 = InteractorModule.optOut

        assertSame(instance1, instance2)
    }

    @Test
    fun sendAndClean_is_singleton_test() {
        val instance1 = InteractorModule.sendAndClean
        val instance2 = InteractorModule.sendAndClean

        assertSame(instance1, instance2)
    }
}