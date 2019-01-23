package webtrekk.android.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.RestrictTo
import kotlinx.coroutines.*
import org.koin.dsl.module.module
import org.koin.log.EmptyLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import webtrekk.android.sdk.data.DaoProvider
import webtrekk.android.sdk.data.SharedPrefs
import webtrekk.android.sdk.data.model.TrackRequest
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.CustomParamRepositoryImpl
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import webtrekk.android.sdk.domain.external.AutoTrack
import webtrekk.android.sdk.domain.external.Sessions
import webtrekk.android.sdk.domain.external.TrackEvent
import webtrekk.android.sdk.domain.external.TrackPage
import webtrekk.android.sdk.domain.internal.AddCustomParams
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.GetCachedTrackRequests
import webtrekk.android.sdk.util.*
import webtrekk.android.sdk.worker.ScheduleManager
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class WebtrekkImpl : Webtrekk(), KoinComponent, CoroutineScope {

    private var context: Context by Delegates.notNullOrException(errorMessage = "Context must be initialized")
    private var config: Config by Delegates.notNullOrException(
        errorMessage = "Webtrekk configurations must be set before invoking any method." +
            " Use Webtrekk.getInstance().init(context, configuration)"
    )

    private val sharedPrefs by inject<SharedPrefs>()

    private val appState by inject<AppState<TrackRequest>>()
    private val scheduleManager by inject<ScheduleManager>()

    private val trackRequestRepository by inject<TrackRequestRepository>()
    private val customParamsRepository by inject<CustomParamRepository>()

    private val cacheTrackRequest by inject<CacheTrackRequest>()
    private val getCachedTrackRequests by inject<GetCachedTrackRequests>()
    private val addCustomParams by inject<AddCustomParams>()
    private val addTrackRequestWithCustomParams by inject<CacheTrackRequestWithCustomParams>()

    private val autoTrack by inject<AutoTrack>()
    private val trackPage by inject<TrackPage>()
    private val trackEvent by inject<TrackEvent>()
    internal val sessions by inject<Sessions>()

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    internal var logger: Logger? = null

    init {
        val module = module {
            single { SharedPrefs(context) }

            single { AppStateImpl() as AppState<TrackRequest> }
            single { ScheduleManager() }

            single { TrackRequestRepositoryImpl(DaoProvider.provideTrackRequestDao(context)) as TrackRequestRepository }
            single { CustomParamRepositoryImpl(DaoProvider.provideCustomParamDao(context)) as CustomParamRepository }

            single { CacheTrackRequest(trackRequestRepository, coroutineContext) }
            single { GetCachedTrackRequests(trackRequestRepository, coroutineContext) }
            single { AddCustomParams(customParamsRepository, coroutineContext) }
            single {
                CacheTrackRequestWithCustomParams(
                    trackRequestRepository,
                    customParamsRepository,
                    coroutineContext
                )
            }

            single { AutoTrack(appState, cacheTrackRequest) }
            single { TrackPage(addTrackRequestWithCustomParams) }
            single { TrackEvent(addTrackRequestWithCustomParams) }
            single { Sessions(sharedPrefs) }
        }

        startKoin(listOf(module), logger = EmptyLogger())
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: WebtrekkImpl? = null

        fun getInstance(): WebtrekkImpl {
            return instance ?: synchronized(this) {
                instance ?: WebtrekkImpl().also { instance = it }
            }
        }
    }

    override fun init(context: Context, config: Config) {
        this.context = context.applicationContext
        this.config = config

        internalInit()
    }

    override fun track(context: Context, customPageName: String?) = config.run {
        if (autoTracking) {
            Log.w("Webtrekk", "Auto track is enabled by default")
            return
        }

        val activityName = customPageName ?: (context as? Activity)?.localClassName
    }

    override fun setCustomPageName(context: Context, customName: String) = config.run {
    }

    override fun trackPage(pageName: String, trackingParams: Map<String, String>) = config.run {
        trackPage(
            TrackRequest(name = pageName, screenResolution = context.resolution()),
            trackingParams
        )
    }

    override fun trackEvent(eventName: String, trackingParams: Map<String, String>) = config.run {
        trackEvent(
            TrackRequest(name = eventName, screenResolution = context.resolution()),
            trackingParams
        )
    }

    override fun optOut() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getEverId(): String = context.run {
        sessions.getEverId()
    }

    override fun clear() {
        job.cancel()
    }

    private fun internalInit() = launch {
        logger = WebtrekkLogger(config.logLevel)

        sessions.setEverId()
        sessions.startNewSession()

        scheduleManager.startScheduling(config)

        if (config.autoTracking) {
            autoTrack(context).also { logInfo("Webtrekk started auto tracking") }
        } else {
            appState.disableAutoTrack(context)
        }
    }
}
