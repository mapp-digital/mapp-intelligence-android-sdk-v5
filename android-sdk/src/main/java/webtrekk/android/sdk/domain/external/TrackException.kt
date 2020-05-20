package webtrekk.android.sdk.domain.external

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.core.CustomKoinComponent
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.ExceptionWrapper
import webtrekk.android.sdk.util.coroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

enum class ExceptionType(val type: String) {
    UNCAUGHT("1"),
    CUSTOM("2"),
    CAUGHT("3"),
    ALL("4"),
    UNCAUGHT_AND_CUSTOM("5"),
    UNCAUGHT_AND_CAUGHT("6"),
    CUSTOM_AND_CAUGHT("7")
}

// TODO: Add comments
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
        if (invokeParams.isOptOut) return

        scope.launch(coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
            logger
        )
        ) {
            val params = createParamsFromException(invokeParams.exception, invokeParams.exceptionType)

            // Cache the track request with its custom params.
            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logger.debug("Cached custom page request: $it") }
                .onFailure { logger.error("Error while caching custom page request: $it") }
        }
    }

    private fun createParamsFromException(exception: Exception, exceptionType: ExceptionType): MutableMap<String, String> {
        val params = emptyMap<String, String>().toMutableMap()
        if (exceptionType.type == "3") {
            params[UrlParams.CRASH_TYPE] = exceptionType.type
            params[UrlParams.CRASH_NAME] = exception.javaClass.name
            if (exception.message != null)
                params[UrlParams.CRASH_MESSAGE] = exception.message!!
            if (exception.cause != null) {
                if (exception.cause!!.message != null) {
                    params[UrlParams.CRASH_CAUSE_MESSAGE] = exception.cause!!.message!!
                }
            }
            params[UrlParams.CRASH_STACK] = exception.stackTrace.createString()
            if (exception.cause != null)
                params[UrlParams.CRASH_CAUSE_STACK] = exception.cause!!.stackTrace.createString()
        } else if (exceptionType.type == "2") {
            params[UrlParams.CRASH_TYPE] = exceptionType.type
            params[UrlParams.CRASH_NAME] = (exception as ExceptionWrapper).name
            params[UrlParams.CRASH_MESSAGE] = (exception as ExceptionWrapper).customMessage
        }
        return params
    }

    private fun Array<StackTraceElement>.createString(): String {
        var stackString = ""
        for (element in this) {
            if (stackString.isNotEmpty()) stackString += "|"
            val lineNumber = if (element.className.contains("android.app.") || element.className.contains("java.lang.")) -1 else element.lineNumber
            var stackItem = element.className + "." +
                element.methodName + "(" + element.fileName
            stackItem += if (lineNumber < 0) ")" else ":" + element.lineNumber + ")"
            stackString += if (stackString.length + stackItem.length <= 255) stackItem else break
        }
        return stackString
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that is created and will be cached in the data base.
     * @param trackingParams the custom params associated with the [trackRequest].
     * @param isOptOut the opt out value.
     */
    data class Params(
        val trackRequest: TrackRequest,
        val isOptOut: Boolean,
        val exception: Exception,
        val exceptionType: ExceptionType
    )
}