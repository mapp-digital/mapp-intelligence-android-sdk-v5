/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.core

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.FormTrackingSettings
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.extension.initOrException
import webtrekk.android.sdk.extension.resolution
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.entity.DataAnnotationClass
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.getWebtrekkDatabase
import webtrekk.android.sdk.domain.external.AutoTrack
import webtrekk.android.sdk.domain.external.ManualTrack
import webtrekk.android.sdk.domain.external.Optout
import webtrekk.android.sdk.domain.external.TrackCustomEvent
import webtrekk.android.sdk.domain.external.TrackCustomForm
import webtrekk.android.sdk.domain.external.TrackCustomPage
import webtrekk.android.sdk.domain.external.TrackException
import webtrekk.android.sdk.extension.appVersionCode
import webtrekk.android.sdk.extension.appVersionName
import webtrekk.android.sdk.module.dataModule
import webtrekk.android.sdk.module.internalInteractorsModule
import webtrekk.android.sdk.util.appFirstOpen
import webtrekk.android.sdk.util.currentSession
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

/**
 * The concrete implementation of [Webtrekk]. This class extends [KoinComponent] for getting the injected dependencies. Also extends [CoroutineScope] with a [SupervisorJob], it has the parent scope that will be passed to all the children coroutines.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class WebtrekkImpl private constructor() : Webtrekk(), CustomKoinComponent, CoroutineScope {

    private val _job = SupervisorJob()

    private val coroutineDispatchers by inject<CoroutineDispatchers>()
    private val appState by inject<AppState<TrackRequest>>()

    private val scheduler by inject<Scheduler>()

    private val autoTrack by inject<AutoTrack>()
    private val manualTrack by inject<ManualTrack>()
    private val trackCustomPage by inject<TrackCustomPage>()
    private val trackCustomEvent by inject<TrackCustomEvent>()
    private val trackCustomForm by inject<TrackCustomForm>()
    private val trackException by inject<TrackException>()
    private val optOutUser by inject<Optout>()

    internal val sessions by inject<Sessions>()
    internal val logger by inject<Logger>()

    internal var context: Context by Delegates.initOrException(errorMessage = "Context must be initialized first")
    internal var config: Config by Delegates.initOrException(
        errorMessage = "Webtrekk configurations must be set before invoking any method." +
            " Use Webtrekk.getInstance().init(context, configuration)"
    )

    override val coroutineContext: CoroutineContext
        get() = _job + coroutineDispatchers.defaultDispatcher

    override fun init(context: Context, config: Config) {
        this.context = context.applicationContext
        this.config = config

        loadModules()
        internalInit()
    }

    override fun trackPage(
        context: Context,
        customPageName: String?,
        trackingParams: Map<String, String>
    ) = config.run {
        val contextName = customPageName ?: (context as Activity).localClassName

        manualTrack(
            ManualTrack.Params(
                trackRequest = TrackRequest(
                    name = contextName,
                    screenResolution = context.resolution(),
                    forceNewSession = currentSession,
                    appFirstOpen = appFirstOpen,
                    appVersionName = context.appVersionName,
                    appVersionCode = context.appVersionCode
                ),
                trackingParams = trackingParams,
                autoTrack = autoTracking,
                isOptOut = hasOptOut()
            ), coroutineDispatchers
        )
    }

    override fun trackCustomPage(pageName: String, trackingParams: Map<String, String>) =
        config.run {
            trackCustomPage(
                TrackCustomPage.Params(
                    trackRequest = TrackRequest(
                        name = pageName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut()
                ), coroutineDispatchers
            )
        }

    override fun trackCustomEvent(eventName: String, trackingParams: Map<String, String>) =
        config.run {
            trackCustomEvent(
                TrackCustomEvent.Params(
                    trackRequest = TrackRequest(
                        name = "0",
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut(),
                    ctParams = eventName

                ), coroutineDispatchers
            )
        }

    override fun trackException(exception: Exception) =
        config.run {
            trackException(
                TrackException.Params(
                    trackRequest = TrackRequest(
                        name = "webtrekk_ignore",
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    isOptOut = hasOptOut(),
                    exception = exception
                ), coroutineDispatchers
            )
        }

    override fun formTracking(
        context: Context,
        view: View?,
        formTrackingSettings: FormTrackingSettings
    ) {
        val contextName =
            if (formTrackingSettings.formName.isEmpty()) context.javaClass.name else formTrackingSettings.formName
        val viewGroup = if (view != null) view.rootView as ViewGroup
        else (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        config.run {
            trackCustomForm(
                TrackCustomForm.Params(
                    trackRequest = TrackRequest(
                        name = contextName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    isOptOut = hasOptOut(),
                    viewGroup = viewGroup,
                    formName = contextName,
                    trackingIds = formTrackingSettings.fieldIds,
                    renameFields = formTrackingSettings.renameFields,
                    confirmButton = formTrackingSettings.confirmButton,
                    anonymous = formTrackingSettings.anonymous,
                    changeFieldsValue = formTrackingSettings.changeFieldsValue,
                    fieldsOrder = formTrackingSettings.pathAnalysis,
                    anonymousSpecificFields = formTrackingSettings.anonymousSpecificFields,
                    fullContentSpecificFields = formTrackingSettings.fullContentSpecificFields

                ), coroutineDispatchers
            )
        }
    }

    override fun optOut(value: Boolean, sendCurrentData: Boolean) = context.run {
        optOutUser(
            Optout.Params(
                context = this,
                optOutValue = value,
                sendCurrentData = sendCurrentData
            ), coroutineDispatchers
        )
    }

    override fun hasOptOut(): Boolean = context.run {
        return optOutUser.isActive()
    }

    override fun getEverId(): String = context.run {
        sessions.getEverId()
    }

    override fun getUserAgent(): String = context.run {
        sessions.getUserAgent()
    }

    /**
     * Loading and init the dependencies that will be injected.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun loadModules() {
        val mainModule = module {
            single { WebtrekkSharedPrefs(context) }
            single { config.okHttpClient }
            single { WorkManager.getInstance(context) }
            single { getWebtrekkDatabase(context).trackRequestDao() }
            single { getWebtrekkDatabase(context).customParamDataDao() }
            single { WebtrekkLogger(config.logLevel) as Logger }
            single {
                CoroutineDispatchers(
                    Dispatchers.Main,
                    Dispatchers.Default,
                    Dispatchers.IO
                )
            }
            if (config.fragmentsAutoTracking && config.activityAutoTracking)
                single { AppStateImpl() as AppState<DataAnnotationClass> }
            else if (config.fragmentsAutoTracking)
                single { FragmentStateImpl() as AppState<DataAnnotationClass> }
            else
                single { ActivityAppStateImpl() as AppState<DataAnnotationClass> }
        }

        val externalInteractorsModule = module {
            single {
                AutoTrack(
                    coroutineContext,
                    get(),
                    get()
                )
            }
            single {
                ManualTrack(
                    coroutineContext,
                    get(),
                    get()
                )
            }
            single { TrackCustomPage(coroutineContext, get()) }
            single {
                TrackCustomEvent(
                    coroutineContext,
                    get()
                )
            }

            single {
                TrackCustomForm(
                    coroutineContext,
                    get()
                )
            }

            single {
                TrackException(
                    coroutineContext,
                    get()
                )
            }

            single {
                Optout(
                    coroutineContext,
                    get(),
                    get(),
                    get(),
                    get()
                )
            }
        }

        try {
            val koinApplication = koinApplication {
                modules(listOf(
                    mainModule,
                    dataModule,
                    internalInteractorsModule,
                    externalInteractorsModule))
            }
            MyKoinContext.koinApp = koinApplication
        } catch (e: Exception) {
            logger.error("Webtrekk is already in use: $e")
        }
    }

    /**
     * The internal starting point of the SDK, where sessions, schedulers..etc are used.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun internalInit() = launch(coroutineExceptionHandler(logger)) {
        sessions.setEverId() // Setting up the ever id at first start of using the SDK.

        // Starting a new session at every freshly app open.
        sessions.startNewSession().also { logger.info("A new session has started") }

        // If there's a new update, auto track it as an event.
        if (isAppUpdate()) {
            logger.info("The app has new version")

            sendAppUpdateEvent()
        }

        // Scheduling the workers for cleaning up the current cache, and setting up the periodic worker for sending the requests.
        scheduler.scheduleCleanUp()
        scheduler.scheduleSendRequests(
            repeatInterval = config.requestsInterval,
            constraints = config.workManagerConstraints
        )

        // If auto tracked is enabled, start [AutoTrack] use case.
        if (config.autoTracking) {
            autoTrack(
                AutoTrack.Params(
                    context = context,
                    isOptOut = hasOptOut()
                ), coroutineDispatchers
            ).also { logger.info("Webtrekk has started auto tracking") }
        } else {
            appState.disable(context)
        }
    }

    /**
     * Returns true if the app has a new update, false otherwise.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun isAppUpdate(): Boolean {
        return sessions.isAppUpdated(context.appVersionName)
    }

    /**
     * Track the app updated as an event tracking.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun sendAppUpdateEvent() {
        val trackingParams = TrackingParams()
        trackingParams.putAll(
            mapOf(
                UrlParams.APP_UPDATED to "1"
            )
        )

        trackCustomEvent(APP_UPDATE_EVENT, trackingParams)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun cancelParentJob() {
        _job.cancel()
    }

    companion object {

        const val APP_UPDATE_EVENT = "app_updated"

        @Volatile
        private lateinit var INSTANCE: WebtrekkImpl

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun getInstance(): WebtrekkImpl {
            synchronized(this) {
                if (!Companion::INSTANCE.isInitialized) {
                    INSTANCE =
                        WebtrekkImpl()
                }
            }

            return INSTANCE
        }
    }
}

internal object MyKoinContext {
    var koinApp: KoinApplication? = null
}

internal interface CustomKoinComponent : KoinComponent {
    // override the used Koin instance to use mylocalKoinInstance
    override fun getKoin(): Koin = MyKoinContext.koinApp!!.koin
}