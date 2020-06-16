package webtrekk.android.sdk.domain.external

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
import webtrekk.android.sdk.util.createString
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.util.START_EX_STRING
import kotlin.coroutines.CoroutineContext
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

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
            val params: MutableMap<String, String>
            if (invokeParams.exceptionType == ExceptionType.UNCAUGHT) {
                params = createParamsFromFile(invokeParams.file, invokeParams.exceptionType)
            } else {
                params = createParamsFromException(invokeParams.exception, invokeParams.exceptionType)
            }
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

    private fun createParamsFromFile(file: File?, exceptionType: ExceptionType): MutableMap<String, String> {
        val params = emptyMap<String, String>().toMutableMap()
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(file))
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                if (line != START_EX_STRING) throw Exception("no start item")

                params[UrlParams.CRASH_TYPE] = exceptionType.type
                params[UrlParams.CRASH_NAME] = br.readLine()

                val crashMessage = br.readLine()
                if (crashMessage != null)
                    params[UrlParams.CRASH_MESSAGE] = crashMessage

                val crashCauseMessage = br.readLine()
                if (crashCauseMessage != null)
                    params[UrlParams.CRASH_CAUSE_MESSAGE] = crashCauseMessage

                val crashStack = br.readLine()
                if (crashStack != null)
                    params[UrlParams.CRASH_STACK] = crashMessage
                val crashStackCause = br.readLine()
                if (crashStackCause != null)
                    params[UrlParams.CRASH_CAUSE_STACK] = crashStackCause
            }
        } catch (e: Exception) {
            logger.error("Incorrect File Exception Format:$e")
        } catch (e: FileNotFoundException) {
            logger.error("Can't read exception file:$e")
        } catch (e: IOException) {
            logger.error("Can't read exception file:$e")
        } finally {
            try {
                br!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (file != null) {
                if (file.delete()) logger.debug("File " + file.getName() + " delete success")
            }
        }
        return params
    }

    private fun createParamsFromException(exception: Exception?, exceptionType: ExceptionType): MutableMap<String, String> {
        val params = emptyMap<String, String>().toMutableMap()
        if (exception != null) {
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
                        params[UrlParams.CRASH_CAUSE_STACK] = exception.cause?.stackTrace!!.createString()
                }
                ExceptionType.CUSTOM -> {
                    params[UrlParams.CRASH_TYPE] = exceptionType.type
                    params[UrlParams.CRASH_NAME] = (exception as ExceptionWrapper).name
                    params[UrlParams.CRASH_MESSAGE] = (exception as ExceptionWrapper).customMessage
                }
            }
        }
        return params
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
        val exception: Exception?,
        val exceptionType: ExceptionType,
        val file: File?
    )
}