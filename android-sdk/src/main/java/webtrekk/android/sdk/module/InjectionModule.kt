package webtrekk.android.sdk.module

import android.content.Context
import androidx.work.Constraints
import androidx.work.WorkManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.api.datasource.SyncPostRequestsDataSource
import webtrekk.android.sdk.api.datasource.SyncPostRequestsDataSourceImpl
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSource
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSourceImpl
import webtrekk.android.sdk.core.ActivityAppStateImpl
import webtrekk.android.sdk.core.AppState
import webtrekk.android.sdk.core.AppStateImpl
import webtrekk.android.sdk.core.DisabledStateImpl
import webtrekk.android.sdk.core.FragmentStateImpl
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.core.SchedulerImpl
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.core.SessionsImpl
import webtrekk.android.sdk.core.WebtrekkLogger
import webtrekk.android.sdk.data.WebtrekkDatabase
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.Cash
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.getWebtrekkDatabase
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.CustomParamRepositoryImpl
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import webtrekk.android.sdk.domain.external.AutoTrack
import webtrekk.android.sdk.domain.external.ManualTrack
import webtrekk.android.sdk.domain.external.Optout
import webtrekk.android.sdk.domain.external.SendAndClean
import webtrekk.android.sdk.domain.external.TrackCustomEvent
import webtrekk.android.sdk.domain.external.TrackCustomForm
import webtrekk.android.sdk.domain.external.TrackCustomMedia
import webtrekk.android.sdk.domain.external.TrackCustomPage
import webtrekk.android.sdk.domain.external.TrackException
import webtrekk.android.sdk.domain.external.TrackUncaughtException
import webtrekk.android.sdk.domain.external.UncaughtExceptionHandler
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.domain.internal.ClearCustomParamsRequest
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.internal.ExecutePostRequest
import webtrekk.android.sdk.domain.internal.ExecuteRequest
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.util.CoroutineDispatchers
import kotlin.coroutines.CoroutineContext

object AppModule {
    internal val config: Config
        get() = LibraryModule.configuration

    internal val webtrekkSharedPrefs: WebtrekkSharedPrefs by lazy {
        WebtrekkSharedPrefs(
            LibraryModule.application
        )
    }

    internal val cash: Cash by lazy { Cash() }

    internal val logger: Logger by lazy { WebtrekkLogger(config.logLevel) }

    internal val dispatchers: CoroutineDispatchers by lazy {
        CoroutineDispatchers(
            Dispatchers.Main,
            Dispatchers.Default,
            Dispatchers.IO
        )
    }

    internal val appState: AppState<DataAnnotationClass> by lazy { provideAppState() }

    private fun provideAppState(): AppState<DataAnnotationClass> {
        return if (config.fragmentsAutoTracking && config.activityAutoTracking)
            AppStateImpl(sessions = InteractorModule.sessions)
        else if (config.fragmentsAutoTracking)
            FragmentStateImpl()
        else if (config.activityAutoTracking)
            ActivityAppStateImpl(sessions = InteractorModule.sessions)
        else DisabledStateImpl()
    }
}

object NetworkModule {

    val okHttpClient: OkHttpClient by lazy {
        LibraryModule.configuration.okHttpClient
    }

    val workManagerConstraints: Constraints by lazy {
        provideWorkManagerConstraints(
            config = LibraryModule.configuration
        )
    }

    val workManager: WorkManager by lazy { WorkManager.getInstance(LibraryModule.application) }

    private fun provideWorkManagerConstraints(
        config: Config
    ): Constraints = config.workManagerConstraints
}

object InteractorModule {
    internal val job: CompletableJob by lazy { SupervisorJob() }

    internal val coroutineContext: CoroutineContext
        get() = job + AppModule.dispatchers.defaultDispatcher

    internal val sessions: Sessions
        get() = SessionsImpl(AppModule.webtrekkSharedPrefs, DataModule.trackRequestDao, coroutineContext)

    internal val scheduler: Scheduler by lazy {
        SchedulerImpl(
            NetworkModule.workManager,
            AppModule.config
        )
    }

    internal fun cacheTrackRequest(): CacheTrackRequest =
        CacheTrackRequest(DataModule.trackRequestRepository)

    internal fun getCachedDataTracks(): GetCachedDataTracks =
        GetCachedDataTracks(DataModule.trackRequestRepository)

    internal fun cacheTrackRequestWithCustomParams(): CacheTrackRequestWithCustomParams =
        CacheTrackRequestWithCustomParams(
            DataModule.trackRequestRepository,
            DataModule.customParamsRepository
        )

    internal fun executeRequest(): ExecuteRequest =
        ExecuteRequest(DataModule.trackRequestRepository, DataModule.syncRequestDataSource)

    internal fun executePostRequest(): ExecutePostRequest =
        ExecutePostRequest(DataModule.trackRequestRepository, DataModule.syncPostRequestsDataSource)

    internal fun clearTrackRequest(): ClearTrackRequests =
        ClearTrackRequests(DataModule.trackRequestRepository)

    internal fun clearCustomParamsRequest(): ClearCustomParamsRequest =
        ClearCustomParamsRequest(DataModule.customParamsRepository)

    internal val autoTrack: AutoTrack by lazy {
        AutoTrack(
            coroutineContext,
            AppModule.appState,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val manualTrack: ManualTrack by lazy {
        ManualTrack(
            coroutineContext,
            sessions,
            cacheTrackRequest(),
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomPage: TrackCustomPage by lazy {
        TrackCustomPage(
            coroutineContext,
            sessions,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomEvent: TrackCustomEvent by lazy {
        TrackCustomEvent(
            coroutineContext,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomForm: TrackCustomForm by lazy {
        TrackCustomForm(
            coroutineContext,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomMedia: TrackCustomMedia by lazy {
        TrackCustomMedia(
            coroutineContext,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackException: TrackException by lazy {
        TrackException(
            coroutineContext,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackUncaughtException: TrackUncaughtException by lazy {
        TrackUncaughtException(
            coroutineContext,
            cacheTrackRequestWithCustomParams()
        )
    }

    internal val uncaughtExceptionHandler: UncaughtExceptionHandler by lazy {
        UncaughtExceptionHandler(
            Thread.getDefaultUncaughtExceptionHandler(),
            LibraryModule.application
        )
    }

    internal val optOut: Optout by lazy {
        Optout(
            coroutineContext,
            sessions,
            scheduler,
            AppModule.appState,
            clearTrackRequest()
        )
    }

    internal val sendAndClean: SendAndClean by lazy {
        SendAndClean(
            coroutineContext,
            scheduler
        )
    }
}

object DataModule {

    internal val database: WebtrekkDatabase by lazy { getWebtrekkDatabase(LibraryModule.application) }

    internal val trackRequestDao: TrackRequestDao by lazy { database.trackRequestDao() }

    internal val customParamsDao: CustomParamDao by lazy { database.customParamDataDao() }

    internal val trackRequestRepository: TrackRequestRepository by lazy {
        TrackRequestRepositoryImpl(trackRequestDao)
    }

    internal val customParamsRepository: CustomParamRepository by lazy {
        CustomParamRepositoryImpl(customParamsDao)
    }

    internal val syncRequestDataSource: SyncRequestsDataSource<DataTrack> by lazy {
        SyncRequestsDataSourceImpl(NetworkModule.okHttpClient)
    }

    internal val syncPostRequestsDataSource: SyncPostRequestsDataSource<List<DataTrack>> by lazy {
        SyncPostRequestsDataSourceImpl(NetworkModule.okHttpClient)
    }
}

object LibraryModule {

    private val initializer = AtomicBoolean(false)
    internal fun isInitialized(): Boolean = initializer.get()

    private var appContext: Context? = null
    internal val application: Context
        get() {
            return appContext
                ?: throw IllegalStateException("Context must be initialized first")
        }

    private var config: Config? = null
    internal val configuration: Config
        get() {
            return config ?: throw IllegalStateException(
                "Webtrekk configurations must be set before invoking any method." +
                        " Use Webtrekk.getInstance().init(context, configuration)"
            )
        }

    internal fun initializeDI(context: Context, configuration: Config) {
        if (initializer.compareAndSet(false, true)) {
            appContext = context.applicationContext
            config = configuration
        }
    }

    fun release() {
        initializer.set(false)
        //appContext = null
        config = null
    }
}