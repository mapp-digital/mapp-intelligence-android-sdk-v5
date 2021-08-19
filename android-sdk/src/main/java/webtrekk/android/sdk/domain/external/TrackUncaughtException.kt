package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.extension.readParam
import webtrekk.android.sdk.extension.validateLine
import webtrekk.android.sdk.integration.IntelligenceEvent
import webtrekk.android.sdk.integration.MappIntelligenceListener
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.END_EX_STRING
import webtrekk.android.sdk.util.EX_ITEM_SEPARATOR
import webtrekk.android.sdk.util.IncorrectErrorFileFormatException
import webtrekk.android.sdk.util.NO_CRASH_CAUSE_MESSAGE_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_CRASH_CAUSE_STACK_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_CRASH_MESSAGE_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_CRASH_NAME_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_CRASH_STACK_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_END_ITEM_SEPARATOR
import webtrekk.android.sdk.util.NO_START_ITEM_SEPARATOR
import webtrekk.android.sdk.util.START_EX_STRING
import webtrekk.android.sdk.util.coroutineExceptionHandler
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Track uncaughtException. This interface is used for auto track exception when app i crashing.
 */
internal class TrackUncaughtException(
    coroutineContext: CoroutineContext,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackUncaughtException.Params> {

    private val _job = Job()
    override val scope: CoroutineScope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by lazy { AppModule.logger }

    override fun invoke(
        invokeParams: TrackUncaughtException.Params,
        coroutineDispatchers: CoroutineDispatchers
    ) {

        IntelligenceEvent.sendEvent(
            invokeParams.context,
            MappIntelligenceListener.CRASH, invokeParams.trackRequest.name
        )
        // If opt out is active, then return
        if (invokeParams.isOptOut) return

        scope.launch(
            coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                logger
            )
        ) {
            val paramsList = createListParamsFromFile(invokeParams.file)
            paramsList.forEach {
                // Cache the track request with its custom params.
                cacheTrackRequestWithCustomParams(
                    CacheTrackRequestWithCustomParams.Params(
                        invokeParams.trackRequest,
                        it
                    )
                )
                    .onSuccess { logger.debug("Cached exception event request: $it") }
                    .onFailure { logger.error("Error while caching exception event request: $it") }
            }
        }
    }

    private fun createListParamsFromFile(file: File): MutableList<MutableMap<String, String>> {
        val paramsList = emptyList<MutableMap<String, String>>().toMutableList()
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(file))
            var line: String?
            var value: String
            while (br.readLine().also { line = it } != null) {
                if (line != START_EX_STRING) throw IncorrectErrorFileFormatException(
                    NO_START_ITEM_SEPARATOR
                )

                val params = emptyMap<String, String>().toMutableMap()
                params[UrlParams.CRASH_TYPE] = ExceptionType.UNCAUGHT.type

                value = br.readParam()
                if (value != "") params[UrlParams.CRASH_NAME] = value
                br.validateLine(EX_ITEM_SEPARATOR, NO_CRASH_NAME_ITEM_SEPARATOR)

                value = br.readParam()
                if (value != "") params[UrlParams.CRASH_MESSAGE] = value
                br.validateLine(EX_ITEM_SEPARATOR, NO_CRASH_MESSAGE_ITEM_SEPARATOR)

                value = br.readParam()
                if (value != "") params[UrlParams.CRASH_CAUSE_MESSAGE] = value
                br.validateLine(EX_ITEM_SEPARATOR, NO_CRASH_CAUSE_MESSAGE_ITEM_SEPARATOR)

                value = br.readParam()
                if (value != "") params[UrlParams.CRASH_STACK] = value
                br.validateLine(EX_ITEM_SEPARATOR, NO_CRASH_STACK_ITEM_SEPARATOR)

                value = br.readParam()
                if (value != "") params[UrlParams.CRASH_CAUSE_STACK] = value
                br.validateLine(EX_ITEM_SEPARATOR, NO_CRASH_CAUSE_STACK_ITEM_SEPARATOR)
                br.validateLine(END_EX_STRING, NO_END_ITEM_SEPARATOR)
                paramsList.add(params)
            }
        } catch (e: IncorrectErrorFileFormatException) {
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
            if (file.delete()) logger.debug("File " + file.name + " delete success")
        }
        return paramsList
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
        val file: File,
        val context: Context? = null
    )
}