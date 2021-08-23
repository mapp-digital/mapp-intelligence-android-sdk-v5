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
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.FormTrackingSettings
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.TrackRequest
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
import webtrekk.android.sdk.events.ActionEvent
import webtrekk.android.sdk.events.MediaEvent
import webtrekk.android.sdk.events.PageViewEvent
import webtrekk.android.sdk.extension.appVersionCode
import webtrekk.android.sdk.extension.appVersionName
import webtrekk.android.sdk.extension.isCaughtAllowed
import webtrekk.android.sdk.extension.isCustomAllowed
import webtrekk.android.sdk.extension.isUncaughtAllowed
import webtrekk.android.sdk.extension.nullOrEmptyThrowError
import webtrekk.android.sdk.extension.resolution
import webtrekk.android.sdk.extension.validateEntireList
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule
import webtrekk.android.sdk.module.LibraryModule
import webtrekk.android.sdk.util.ExceptionWrapper
import webtrekk.android.sdk.util.appFirstOpen
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.util.currentSession
import webtrekk.android.sdk.util.getFileName
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * The concrete implementation of [Webtrekk]. This class extends [CoroutineScope] with a [SupervisorJob], it has the parent scope that will be passed to all the children coroutines.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class WebtrekkImpl private
constructor() : Webtrekk(),
    CoroutineScope {

    private val _job by lazy { InteractorModule.job }

    private val coroutineDispatchers by lazy { AppModule.dispatchers }
    private val appState by lazy { AppModule.appState }

    private val scheduler by lazy { InteractorModule.scheduler }

    private val autoTrack by lazy { InteractorModule.autoTrack }
    private val manualTrack by lazy { InteractorModule.manualTrack }
    private val trackCustomPage by lazy { InteractorModule.trackCustomPage }
    private val trackCustomEvent by lazy { InteractorModule.trackCustomEvent }
    private val trackCustomForm by lazy { InteractorModule.trackCustomForm }
    private val trackCustomMedia by lazy { InteractorModule.trackCustomMedia }
    private val trackException by lazy { InteractorModule.trackException }
    private val trackUncaughtException by lazy { InteractorModule.trackUncaughtException }
    private val optOutUser by lazy { InteractorModule.optOut }
    private val sendAndClean by lazy { InteractorModule.sendAndClean }
    internal val sessions by lazy { InteractorModule.sessions }
    internal val logger by lazy { AppModule.logger }
    private val cash by lazy { AppModule.cash }

    private val uncaughtExceptionHandler by lazy { InteractorModule.uncaughtExceptionHandler }

    private var lastEqual = false
    private var lastTimeMedia = 0L
    private var lastAction = "play"

    internal val context: Context
        get() = LibraryModule.application

    internal val config: Config
        get() = LibraryModule.configuration

    override val coroutineContext: CoroutineContext
        get() = _job + coroutineDispatchers.defaultDispatcher

    override fun init(context: Context, config: Config) {
        synchronized(WebtrekkImpl::class) {
            if (!LibraryModule.isInitialized()) {
                LibraryModule.initializeDI(context, config)
                internalInit()
            }
        }
    }

    override fun trackPage(
        context: Context,
        customPageName: String?,
        trackingParams: Map<String, String>
    ) = config.run {
        val contextName = customPageName ?: (context as Activity).componentName.className
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
                isOptOut = hasOptOut(),
                context = context
            ), coroutineDispatchers
        )
    }

    override fun trackPage(page: PageViewEvent) {
        if (!cash.canContinue(page.campaignParameters)) {
            page.campaignParameters = null
        }
        trackCustomPage(page.name, page.toHasMap())
    }

    override fun trackMedia(media: MediaEvent) {
        trackMedia(media.pageName, media.toHasMap())
    }

    override fun trackAction(action: ActionEvent) {
        if (!cash.canContinue(action.campaignParameters)) {
            action.campaignParameters = null
        }
        trackCustomEvent(action.name, action.toHasMap())
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
                    isOptOut = hasOptOut(),
                    context = context
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
                    ctParams = eventName,
                    context = context
                ), coroutineDispatchers
            )
        }

    override fun trackMedia(mediaName: String, trackingParams: Map<String, String>) {
        if (!mediaParamValidation(trackingParams)) {
            return
        }
        config.run {
            trackCustomMedia(
                TrackCustomMedia.Params(
                    trackRequest = TrackRequest(
                        name = mediaName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut(),
                    context = context
                ), coroutineDispatchers
            )
        }
    }

    override fun trackException(exception: Exception, exceptionType: ExceptionType) =
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
                    exception = exception,
                    exceptionType = exceptionType,
                    context = context
                ), coroutineDispatchers
            )
        }

    override fun trackException(exception: Exception) {
        if (config.exceptionLogLevel.isCaughtAllowed())
            trackException(exception, ExceptionType.CAUGHT)
    }

    override fun trackException(name: String, message: String) {
        if (config.exceptionLogLevel.isCustomAllowed())
            trackException(ExceptionWrapper(name, message), ExceptionType.CUSTOM)
    }

    override fun trackException(file: File) {
        config.run {
            trackUncaughtException(
                TrackUncaughtException.Params(
                    trackRequest = TrackRequest(
                        name = WEBTREKK_IGNORE,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode
                    ),
                    isOptOut = hasOptOut(),
                    file = file,
                    context = context
                ), coroutineDispatchers
            )
        }
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
                    fullContentSpecificFields = formTrackingSettings.fullContentSpecificFields,
                    context = context
                ), coroutineDispatchers
            )
        }
    }

    override fun trackUrl(url: Uri, mediaCode: String?) {
        val media = mediaCode ?: InternalParam.WT_MC_DEFAULT
        if (!url.toString().contains(media, true)) {
            logger.warn("This media code does not exist in the request")
            return
        } else {
            sessions.setUrl(url, media)
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

    override fun setIdsAndDomain(trackIds: List<String>, trackDomain: String) {
        trackDomain.nullOrEmptyThrowError("trackDomain")
        trackIds.validateEntireList("trackIds")
        context.run {
            sendAndClean(
                SendAndClean.Params(
                    context = this,
                    trackDomain = trackDomain,
                    trackIds = trackIds,
                    config = config
                ), coroutineDispatchers
            )
        }
    }

    override fun getUserAgent(): String = context.run {
        sessions.getUserAgent()
    }

    override fun anonymousTracking(
        enabled: Boolean,
        suppressParams: Set<String>,
        generateNewEverId: Boolean
    ) {
        sessions.setAnonymous(enabled)
        sessions.setAnonymousParam(suppressParams)
        if (generateNewEverId && !enabled)
            sessions.generateNewEverId()
    }

    /**
     * The internal starting point of the SDK, where sessions, schedulers..etc are used.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun internalInit() = launch(coroutineExceptionHandler(logger)) {
        if (config.shouldMigrate) {
            sessions.migrate()
        }

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

        if (config.exceptionLogLevel.isUncaughtAllowed()) {
            initUncaughtExceptionTracking()
        }
    }

    private fun mediaParamValidation(trackingParams: Map<String, String>): Boolean {
        if ("pos".equals(trackingParams[MediaParam.MEDIA_ACTION], true)) {
            if ((lastTimeMedia + 3000) > System.currentTimeMillis()) {
                logger.info("The limit for the position parameter is one request every 3 seconds")
                return false
            }
            lastTimeMedia = System.currentTimeMillis()
        }

        if ("play".equals(trackingParams[MediaParam.MEDIA_ACTION], true) or "pause".equals(
                trackingParams[MediaParam.MEDIA_ACTION],
                true
            )
        ) {
            lastAction = trackingParams[MediaParam.MEDIA_ACTION].toString()
        }
//  TODO maybe add later
//        if (trackingParams[MediaParam.MEDIA_DURATION] == null || trackingParams[MediaParam.MEDIA_POSITION] == null) {
//            logger.info("Duration and Position is required")
//            return false
//        }

        lastEqual =
            if (trackingParams[MediaParam.MEDIA_DURATION] == trackingParams[MediaParam.MEDIA_POSITION]) {
                if (lastEqual) {
                    logger.info("Duration and Position are the same")
                    return false
                } else {
                    true
                }
            } else {
                false
            }
        return true
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

        trackCustomEvent(WEBTREKK_IGNORE, trackingParams)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun initUncaughtExceptionTracking() {
        if (config.exceptionLogLevel.isUncaughtAllowed()) {
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)

            val fileName = getFileName(false, context)
            val loadFile = File(fileName)
            if (loadFile.exists())
                trackException(loadFile)
        }
    }

    companion object {

        const val WEBTREKK_IGNORE = "webtrekk_ignore"

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
