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

package webtrekk.android.sdk

import android.app.Activity
import android.content.Context
import androidx.annotation.RestrictTo
import androidx.work.WorkManager
import kotlinx.coroutines.*
import org.koin.dsl.module.module
import org.koin.log.EmptyLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.entity.*
import webtrekk.android.sdk.data.getWebtrekkDatabase
import webtrekk.android.sdk.domain.Scheduler
import webtrekk.android.sdk.domain.Sessions
import webtrekk.android.sdk.domain.external.*
import webtrekk.android.sdk.extension.initOrException
import webtrekk.android.sdk.extension.resolution
import webtrekk.android.sdk.module.dataModule
import webtrekk.android.sdk.module.externalInteractorsModule
import webtrekk.android.sdk.module.internalInteractorsModule
import webtrekk.android.sdk.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class WebtrekkImpl private constructor() : Webtrekk(), KoinComponent, CoroutineScope {

    private val _job = SupervisorJob()

    private val coroutineDispatchers by inject<CoroutineDispatchers>()
    private val appState by inject<AppState<TrackRequest>>()

    private val scheduler by inject<Scheduler>()

    private val autoTrack by inject<AutoTrack>()
    private val manualTrack by inject<ManualTrack>()
    private val trackCustomPage by inject<TrackCustomPage>()
    private val trackCustomEvent by inject<TrackCustomEvent>()
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

    init {
        val mainModule = module {
            single { WebtrekkSharedPrefs(context) }
            single { config.okHttpClient }
            single { WorkManager.getInstance() }
            single { getWebtrekkDatabase(context).trackRequestDao() }
            single { getWebtrekkDatabase(context).customParamDataDao() }
            single { WebtrekkLogger(config.logLevel) as Logger }
            single { CoroutineDispatchers(Dispatchers.Main, Dispatchers.Default, Dispatchers.IO) }
            single { AppStateImpl() as AppState<TrackRequest> }
        }

        startKoin(
            listOf(
                mainModule,
                dataModule,
                internalInteractorsModule,
                externalInteractorsModule
            ), logger = EmptyLogger()
        )
    }

    override fun init(context: Context, config: Config) {
        this.context = context.applicationContext
        this.config = config

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
                    screenResolution = context.resolution()
                ),
                trackingParams = trackingParams,
                autoTrack = autoTracking,
                isOptOut = hasOptOut()
            )
        )
    }

    override fun trackCustomPage(pageName: String, trackingParams: Map<String, String>) =
        config.run {
            trackCustomPage(
                TrackCustomPage.Params(
                    trackRequest = TrackRequest(
                        name = pageName,
                        screenResolution = context.resolution()
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut()
                )
            )
        }

    override fun trackCustomEvent(eventName: String, trackingParams: Map<String, String>) =
        config.run {
            trackCustomEvent(
                TrackCustomEvent.Params(
                    trackRequest = TrackRequest(
                        name = eventName,
                        screenResolution = context.resolution()
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut()
                )
            )
        }

    override fun optOut(value: Boolean) = context.run {
        optOutUser(Optout.Params(context = this, optOutValue = value))
    }

    override fun hasOptOut(): Boolean = context.run {
        return optOutUser.isActive()
    }

    override fun getEverId(): String = context.run {
        sessions.getEverId()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun internalInit() = launch(coroutineExceptionHandler) {
        sessions.setEverId()
        sessions.startNewSession()

        scheduler.scheduleCleanUp()
        scheduler.scheduleSendRequests(
            sendDelay = config.sendDelay,
            constraints = config.workManagerConstraints
        )

        if (config.autoTracking) {
            autoTrack(
                AutoTrack.Params(
                    context = context,
                    isOptOut = hasOptOut()
                )
            ).also { logInfo("Webtrekk started auto tracking") }
        } else {
            appState.disableAutoTrack(context)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun cancelParentJob() {
        _job.cancel()
    }

    companion object {

        @Volatile
        private lateinit var INSTANCE: WebtrekkImpl

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun getInstance(): WebtrekkImpl {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = WebtrekkImpl()
                }
            }

            return INSTANCE
        }
    }
}
