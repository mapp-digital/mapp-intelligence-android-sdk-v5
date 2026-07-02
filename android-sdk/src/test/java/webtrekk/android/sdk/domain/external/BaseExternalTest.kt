package webtrekk.android.sdk.domain.external

import android.content.Context
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.core.WebtrekkImpl
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule
import webtrekk.android.sdk.module.LibraryModule
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.configuration

@ExperimentalCoroutinesApi
open class BaseExternalTest {

    @RelaxedMockK
    lateinit var appContext: Context

    protected val job = SupervisorJob()
    protected val dispatcher = Dispatchers.Unconfined
    protected val coroutineScope = CoroutineScope(dispatcher + job)
    protected val coroutineContext = coroutineScope.coroutineContext

    lateinit var webtrekk: Webtrekk

    @Before
    open fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(dispatcher)

        // Mock all static singletons BEFORE init() so internalInit() never reaches
        // the real InteractorModule / DataModule / Room.
        mockkObject(LibraryModule)
        mockkObject(AppModule)
        mockkObject(InteractorModule)

        every { LibraryModule.isInitialized() } returns false
        every { LibraryModule.application } returns appContext
        every { LibraryModule.configuration } returns configuration
        every { LibraryModule.initializeDI(any(), any()) } answers {
            every { LibraryModule.isInitialized() } returns true
        }

        val mockDispatchers = CoroutineDispatchers(dispatcher, dispatcher, dispatcher)
        every { AppModule.dispatchers } returns mockDispatchers
        every { AppModule.logger } returns mockk(relaxed = true)
        every { AppModule.appState } returns mockk(relaxed = true)
        every { AppModule.cash } returns mockk(relaxed = true)
        every { AppModule.webtrekkSharedPrefs } returns mockk<WebtrekkSharedPrefs>(relaxed = true)

        val mockSessions = mockk<Sessions>(relaxed = true)
        every { mockSessions.getEverId() } returns "test-ever-id"
        every { mockSessions.getEverIdMode() } returns GenerationMode.AUTO_GENERATED
        every { mockSessions.isAppUpdated(any()) } returns false
        every { mockSessions.isAnonymous() } returns false
        coEvery { mockSessions.startNewSession() } returns Unit
        coEvery { mockSessions.migrate() } returns Unit

        every { InteractorModule.job } returns SupervisorJob()
        every { InteractorModule.sessions } returns mockSessions
        every { InteractorModule.scheduler } returns mockk(relaxed = true)
        every { InteractorModule.autoTrack } returns mockk(relaxed = true)
        every { InteractorModule.manualTrack } returns mockk(relaxed = true)
        every { InteractorModule.trackCustomPage } returns mockk(relaxed = true)
        every { InteractorModule.trackCustomEvent } returns mockk(relaxed = true)
        every { InteractorModule.trackCustomForm } returns mockk(relaxed = true)
        every { InteractorModule.trackCustomMedia } returns mockk(relaxed = true)
        every { InteractorModule.trackException } returns mockk(relaxed = true)
        every { InteractorModule.trackUncaughtException } returns mockk(relaxed = true)
        every { InteractorModule.optOut } returns mockk(relaxed = true)
        every { InteractorModule.sendAndClean } returns mockk(relaxed = true)
        every { InteractorModule.uncaughtExceptionHandler } returns mockk(relaxed = true)

        webtrekk = spyk<WebtrekkImpl>()
        webtrekk.init(appContext, configuration)
    }

    @After
    open fun tearDown() {
        coroutineScope.cancel()
        Dispatchers.resetMain()
        unmockkAll()
    }
}
