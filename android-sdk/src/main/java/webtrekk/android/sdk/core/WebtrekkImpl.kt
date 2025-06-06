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
import webtrekk.android.sdk.ActiveConfig
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.CustomParam
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.FormTrackingSettings
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.model.GenerationMode
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
import webtrekk.android.sdk.events.eventParams.MediaParameters
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
import webtrekk.android.sdk.util.generateEverId
import webtrekk.android.sdk.util.getFileName
import webtrekk.android.sdk.util.webtrekkLogger
import java.io.File
import java.util.Objects
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
                val configJson = config.toJson()
                AppModule.webtrekkSharedPrefs.configJson = configJson
                webtrekkLogger.info("CONFIG: $configJson")
            } else {
                webtrekkLogger.warn("Webtrekk is already initialized!")
            }
        }
    }

    override fun trackPage(
        context: Context,
        customPageName: String?,
        trackingParams: Map<String, String>
    ) = config.run {
        val contextName = customPageName ?: (context as Activity).componentName.className
        val appFirstOpen = appFirstOpen(true)
        manualTrack(
            ManualTrack.Params(
                trackRequest = TrackRequest(
                    name = contextName,
                    screenResolution = context.resolution(),
                    forceNewSession = currentSession,
                    appFirstOpen = appFirstOpen,
                    appVersionName = context.appVersionName,
                    appVersionCode = context.appVersionCode,
                    everId = getEverId(),
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
        this.trackMedia(
            pageName = media.pageName,
            mediaName = media.parameters.name,
            trackingParams = media.toHasMap()
        )
    }

    override fun trackAction(action: ActionEvent) {
        if (!cash.canContinue(action.campaignParameters)) {
            action.campaignParameters = null
        }
        trackCustomEvent(action.name, action.toHasMap())
    }

    override fun trackCustomPage(pageName: String, trackingParams: Map<String, String>) =
        config.run {
            val appFirstOpen = appFirstOpen(true)
            trackCustomPage(
                TrackCustomPage.Params(
                    trackRequest = TrackRequest(
                        name = pageName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId()
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut(),
                    context = context
                ), coroutineDispatchers
            )
        }

    override fun trackCustomEvent(eventName: String, trackingParams: Map<String, String>) =
        config.run {
            val appFirstOpen = appFirstOpen(true)
            trackCustomEvent(
                TrackCustomEvent.Params(
                    trackRequest = TrackRequest(
                        name = "0",
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId(),
                    ),
                    trackingParams = trackingParams,
                    isOptOut = hasOptOut(),
                    ctParams = eventName,
                    context = context
                ), coroutineDispatchers
            )
        }

    override fun trackMedia(mediaName: String, trackingParams: Map<String, String>) {
        this.trackMedia(mediaName, mediaName, trackingParams)
    }

    override fun trackMedia(
        pageName: String,
        mediaName: String,
        trackingParams: Map<String, String>
    ) {
        if (!mediaParamValidation(trackingParams)) {
            return
        }
        config.run {
            val appFirstOpen = appFirstOpen(true)
            val params = trackingParams.toMutableMap()

            if (!params.containsKey(MediaParam.MEDIA_DURATION) ||
                params[MediaParam.MEDIA_DURATION].isNullOrEmpty() ||
                "null".equals(params[MediaParam.MEDIA_DURATION], true)
            ) {
                params[MediaParam.MEDIA_DURATION] = "0"
            }

            if (!params.containsKey(MediaParam.MEDIA_POSITION) ||
                params[MediaParam.MEDIA_POSITION].isNullOrEmpty() ||
                "null".equals(params[MediaParam.MEDIA_POSITION], true)
            ) {
                params[MediaParam.MEDIA_POSITION] = "0"
            }

            trackCustomMedia(
                TrackCustomMedia.Params(
                    trackRequest = TrackRequest(
                        name = pageName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId(),
                    ),
                    trackingParams = params,
                    isOptOut = hasOptOut(),
                    context = context,
                    mediaName = mediaName
                ), coroutineDispatchers
            )
        }
    }

    override fun trackException(exception: Exception, exceptionType: ExceptionType) =
        config.run {
            val appFirstOpen = appFirstOpen(true)
            trackException(
                TrackException.Params(
                    trackRequest = TrackRequest(
                        name = "0",
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId(),
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
            val appFirstOpen = appFirstOpen(true)
            trackUncaughtException(
                TrackUncaughtException.Params(
                    trackRequest = TrackRequest(
                        name = "0",
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId(),
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
            val appFirstOpen = appFirstOpen(true)
            trackCustomForm(
                TrackCustomForm.Params(
                    trackRequest = TrackRequest(
                        name = contextName,
                        screenResolution = context.resolution(),
                        forceNewSession = currentSession,
                        appFirstOpen = appFirstOpen,
                        appVersionName = context.appVersionName,
                        appVersionCode = context.appVersionCode,
                        everId = getEverId(),
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

    override fun optOut(value: Boolean, sendCurrentData: Boolean) =
        optOutUser(
            Optout.Params(
                context = context,
                optOutValue = value,
                sendCurrentData = sendCurrentData
            ), coroutineDispatchers
        )

    override fun hasOptOut(): Boolean =
        optOutUser.isActive()


    override fun getEverId(): String? =
        sessions.getEverId()

    override fun setIdsAndDomain(trackIds: List<String>, trackDomain: String) {
        trackDomain.nullOrEmptyThrowError("trackDomain")
        trackIds.validateEntireList("trackIds")

        config.apply {
            this.trackDomain = trackDomain
            this.trackIds = trackIds
        }
    }

    override fun getTrackIds(): List<String> {
        return config.trackIds
    }

    override fun getTrackDomain(): String {
        return config.trackDomain
    }

    override fun getUserAgent(): String = context.run {
        sessions.getUserAgent()
    }

    override fun anonymousTracking(
        enabled: Boolean,
        suppressParams: Set<String>,
    ) {
        sessions.setAnonymous(enabled)
        sessions.setAnonymousParam(suppressParams)
        sessions.setEverId(generateEverId(), false, GenerationMode.AUTO_GENERATED)
    }

    override fun isAnonymousTracking(): Boolean {
        return sessions.isAnonymous()
    }


    /**
     * Set custom everId
     * If null or empty string provided, new everId will be generated and set
     *
     */
    @Synchronized
    override fun setEverId(everId: String?) {
        val newEverId: String
        val everIdMode: GenerationMode

        if (everId.isNullOrEmpty()) {
            newEverId = generateEverId()
            everIdMode = GenerationMode.AUTO_GENERATED
        } else {
            newEverId = everId
            everIdMode = GenerationMode.USER_GENERATED
        }
        sessions.setEverId(newEverId, true, everIdMode)
    }

    override fun getVersionInEachRequest(): Boolean {
        return config.versionInEachRequest
    }

    override fun setVersionInEachRequest(enabled: Boolean) {
        config.versionInEachRequest = enabled
    }

    override fun setBatchEnabled(enabled: Boolean) {
        config.batchSupport = enabled
    }

    override fun getRequestsPerBatch(): Int {
        return config.requestPerBatch
    }

    override fun setRequestPerBatch(requestsCount: Int) {
        config.requestPerBatch = requestsCount
    }

    override fun getExceptionLogLevel(): ExceptionType {
        return config.exceptionLogLevel
    }

    override fun setExceptionLogLevel(exceptionLogLevel: ExceptionType) {
        config.exceptionLogLevel = exceptionLogLevel
        initUncaughtExceptionTracking()
    }

    override fun clearSdkConfig() {
        AppModule.webtrekkSharedPrefs.apply {
            sharedPreferences.edit().clear().apply()
            previousSharedPreferences.edit().clear().apply()
        }
        LibraryModule.release()
    }

    override fun isUserMatchingEnabled(): Boolean {
        return config.userMatchingEnabled
    }

    override fun setUserMatchingEnabled(enabled: Boolean) {
        config.userMatchingEnabled = enabled
    }

    /**
     * The internal starting point of the SDK, where sessions, schedulers..etc are used.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun internalInit() = launch(coroutineExceptionHandler(logger)) {
        if (config.shouldMigrate) {
            sessions.migrate()
        }

        // Setting up the ever id at first start of using the SDK.
        if (config.everId.isNullOrEmpty()) {
            //everId will be autogenerated if not exist, otherwise, this call has no effect
            sessions.setEverId(null, false, GenerationMode.AUTO_GENERATED)
        } else {
            // user set custom value for everId; force updating everId
            setEverId(config.everId)
        }

        // Starting a new session at every freshly app open.
        sessions.startNewSession().also { logger.info("A new session has started") }

        // If there's a new update, auto track it as an event.
        if (isAppUpdate()) {
            logger.info("The app has new version")

            sendAppUpdateEvent()
        }

        // If auto tracked is enabled, start [AutoTrack] use case.
        if (config.autoTracking) {
            autoTrack.invoke(
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

        // Clean all finished works (SUCCEEDED, FAILED, CANCELED)
        scheduler.pruneWorks()

        // Scheduling the workers for clearing sent requests.
        scheduler.scheduleCleanUp()

        // Scheduling the periodic worker for sending requests
        scheduler.scheduleSendRequests(
            repeatInterval = config.requestsInterval,
            constraints = config.workManagerConstraints
        )
    }

    private fun mediaParamValidation(trackingParams: Map<String, String>): Boolean {
        if (Objects.equals(
                trackingParams[MediaParam.MEDIA_ACTION],
                MediaParameters.Action.POS.code()
            )
        ) {
            if ((lastTimeMedia + 3000) > System.currentTimeMillis()) {
                logger.info("The limit for the position parameter is one request every 3 seconds")
                return false
            }
            lastTimeMedia = System.currentTimeMillis()
        }

        return trackingParams.containsKey(MediaParam.MEDIA_DURATION) &&
                trackingParams.containsKey(MediaParam.MEDIA_POSITION)
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

        trackCustomEvent(CustomParam.WEBTREKK_IGNORE, trackingParams)
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

    override fun sendRequestsNowAndClean() {
        sendAndClean(
            SendAndClean.Params(
                context = context,
                trackDomain = config.trackDomain,
                trackIds = config.trackIds,
                config = config
            ), coroutineDispatchers
        )
    }

    override fun isBatchEnabled(): Boolean {
        return config.batchSupport
    }

    override fun isInitialized(): Boolean {
        return LibraryModule.isInitialized()
    }

    override fun setLogLevel(logLevel: Logger.Level) {
        logger.setLevel(logLevel)
    }

    override fun setRequestInterval(minutes: Long) {
        config.requestsInterval = minutes
    }

    override fun getDmcUserId(): String? {
        return if (config.userMatchingEnabled) sessions.getDmcUserId() else null
    }

    override fun getCurrentConfiguration(): ActiveConfig {
        return ActiveConfig(
            trackDomains = getTrackDomain(),
            trackIds = getTrackIds(),
            everId = getEverId(),
            everIdMode = sessions.getEverIdMode(),
            userAgent = getUserAgent(),
            userMatchingId = getDmcUserId(),
            anonymousParams = sessions.isAnonymousParam(),
            isAnonymous = isAnonymousTracking(),
            logLevel = config.logLevel,
            exceptionLogLevel = getExceptionLogLevel(),
            requestInterval = config.requestsInterval,
            requestsPerBatch = getRequestsPerBatch(),
            isActivityAutoTracking = config.activityAutoTracking,
            isFragmentAutoTracking = config.fragmentsAutoTracking,
            isAutoTracking = config.autoTracking,
            isBatchSupport = isBatchEnabled(),
            isOptOut = hasOptOut(),
            isUserMatching = isUserMatchingEnabled(),
            shouldMigrate = config.shouldMigrate,
            sendVersionInEachRequest = getVersionInEachRequest(),
            appFirstOpen = sessions.getAppFirstOpen(false) == "1",
            temporarySessionId = sessions.getTemporarySessionId(),
        )
    }

    override fun setTemporarySessionId(sessionId: String) {
        sessions.setTemporarySessionId(sessionId)
    }

    companion object {
        @Volatile
        private var INSTANCE: WebtrekkImpl? = null

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun getInstance(): WebtrekkImpl {
            synchronized(WebtrekkImpl::class) {
                val mInstance = INSTANCE
                if (mInstance == null) {
                    INSTANCE = WebtrekkImpl()
                }
                return INSTANCE!!
            }
        }

        /**
         * Reset singleton instance, delete all config settings.
         *
         * Context is mandatory, config is optional. If not provided, SDK will be initialized with default values.
         */
        fun reset(context: Context) {
            var ids: List<String>? = emptyList()
            var domain: String? = null
            var configBackup: Config? = null

            INSTANCE?.let {
                it.sendRequestsNowAndClean()
                configBackup = it.config.copy().apply { everId = null }
                ids = it.config.trackIds
                domain = it.config.trackDomain
                it.clearSdkConfig()
            }

            val mConfig = configBackup ?: WebtrekkConfiguration.Builder(ids!!, domain!!).build()
            INSTANCE = WebtrekkImpl().also { webtrekk ->
                webtrekk.init(context.applicationContext, mConfig)
            }
        }
    }
}
