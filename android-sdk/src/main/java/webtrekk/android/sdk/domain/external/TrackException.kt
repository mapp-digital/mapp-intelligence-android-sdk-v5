package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.core.CustomKoinComponent
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.ExceptionWrapper
import webtrekk.android.sdk.extension.createString
import webtrekk.android.sdk.integration.IntelligenceEvent
import webtrekk.android.sdk.integration.MappIntelligenceListener
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Track Exception. This interface is used for track all client exception and custom exception .
 */
internal class TrackException(
    coroutineContext: CoroutineContext,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackException.Params>, CustomKoinComponent {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by inject<Logger>()

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // If opt out is active, then return
        IntelligenceEvent.sendEvent(
            invokeParams.context,
            MappIntelligenceListener.CRASH, invokeParams.trackRequest.name
        )
        if (invokeParams.isOptOut) return
        scope.launch(
            coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                logger
            )
        ) {
            val params =
                createParamsFromException(invokeParams.exception, invokeParams.exceptionType)
            // Cache the track request with its custom params.
            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logger.debug("Cached exception event request: $it") }
                .onFailure { logger.error("Error while caching exception event request: $it") }
        }
    }

    private fun createParamsFromException(
        exception: Exception,
        exceptionType: ExceptionType
    ): MutableMap<String, String> {
        val params = emptyMap<String, String>().toMutableMap()
        when (exceptionType) {
            ExceptionType.CAUGHT -> {
                params[UrlParams.CRASH_TYPE] = exceptionType.type
                params[UrlParams.CRASH_NAME] = exception.javaClass.name
                if (exception.message != null)
                    params[UrlParams.CRASH_MESSAGE] = exception.message!!
                if (exception.cause != null) {
                    if (exception.cause?.message != null) {
                        params[UrlParams.CRASH_CAUSE_MESSAGE] = exception.cause?.message!!
                    }
                }
                params[UrlParams.CRASH_STACK] = exception.stackTrace.createString()
                if (exception.cause != null)
                    params[UrlParams.CRASH_CAUSE_STACK] =
                        exception.cause?.stackTrace!!.createString()
            }
            ExceptionType.CUSTOM -> {
                params[UrlParams.CRASH_TYPE] = exceptionType.type
                params[UrlParams.CRASH_NAME] = (exception as ExceptionWrapper).name
                params[UrlParams.CRASH_MESSAGE] = (exception).customMessage
            }

            else -> return params
        }
        return params
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that is created and will be cached in the data base.
     * @param exception the custom params associated with the [trackRequest].
     * @param isOptOut the Exception.
     * @param exceptionType the ExceptionType.
     */
    data class Params(
        val trackRequest: TrackRequest,
        val isOptOut: Boolean,
        val exception: Exception,
        val exceptionType: ExceptionType,
        val context: Context? =null
    )
}