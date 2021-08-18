package webtrekk.android.sdk.module

import android.content.Context
import androidx.work.Constraints
import androidx.work.WorkManager
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
import webtrekk.android.sdk.core.*
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
import webtrekk.android.sdk.domain.external.*
import webtrekk.android.sdk.domain.internal.*
import webtrekk.android.sdk.util.CoroutineDispatchers
import java.util.concurrent.TimeUnit
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
            AppStateImpl()
        else if (config.fragmentsAutoTracking)
            FragmentStateImpl()
        else
            ActivityAppStateImpl()
    }
}

object NetworkModule {

    private val timeUnit = TimeUnit.SECONDS
    private const val callTimeout: Long = 60L
    private const val connectTimeout: Long = 60L
    private const val readTimeout: Long = 60L

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(callTimeout, timeUnit)
            .connectTimeout(connectTimeout, timeUnit)
            .readTimeout(readTimeout, timeUnit)
            .build()
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

object ExternalInteractorsModule {
    internal val job: CompletableJob by lazy { SupervisorJob() }

    internal val coroutineContext: CoroutineContext
        get() = job + AppModule.dispatchers.defaultDispatcher

    internal val autoTrack: AutoTrack by lazy {
        AutoTrack(
            coroutineContext,
            AppModule.appState,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val manualTrack: ManualTrack by lazy {
        ManualTrack(
            coroutineContext,
            InternalInteractorsModule.sessions,
            InternalInteractorsModule.cacheTrackRequest(),
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomPage: TrackCustomPage by lazy {
        TrackCustomPage(
            coroutineContext,
            InternalInteractorsModule.sessions,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomEvent: TrackCustomEvent by lazy {
        TrackCustomEvent(
            coroutineContext,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomForm: TrackCustomForm by lazy {
        TrackCustomForm(
            coroutineContext,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackCustomMedia: TrackCustomMedia by lazy {
        TrackCustomMedia(
            coroutineContext,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackException: TrackException by lazy {
        TrackException(
            coroutineContext,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
        )
    }

    internal val trackUncaughtException: TrackUncaughtException by lazy {
        TrackUncaughtException(
            coroutineContext,
            InternalInteractorsModule.cacheTrackRequestWithCustomParams()
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
            InternalInteractorsModule.sessions,
            InternalInteractorsModule.scheduler,
            AppModule.appState,
            InternalInteractorsModule.clearTrackRequest()
        )
    }

    internal val sendAndClean: SendAndClean by lazy {
        SendAndClean(
            coroutineContext,
            InternalInteractorsModule.scheduler
        )
    }
}

object InternalInteractorsModule {

    internal val sessions: Sessions by lazy { SessionsImpl(AppModule.webtrekkSharedPrefs) }

    internal val scheduler: Scheduler by lazy { SchedulerImpl(NetworkModule.workManager) }

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

    @Volatile
    lateinit var application: Context

    @Volatile
    lateinit var configuration: Config

    fun initializeDI(context: Context, config: Config) {
        if (!::application.isInitialized) {
            synchronized(this) {
                application = context.applicationContext
                configuration = config
            }
        }
    }
}