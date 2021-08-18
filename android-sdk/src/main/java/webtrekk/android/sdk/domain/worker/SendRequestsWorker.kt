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

package webtrekk.android.sdk.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.withContext
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.ExecutePostRequest
import webtrekk.android.sdk.domain.internal.ExecuteRequest
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.extension.batch
import webtrekk.android.sdk.extension.buildPostRequest
import webtrekk.android.sdk.extension.buildUrlRequest
import webtrekk.android.sdk.extension.stringifyRequestBody
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InternalInteractorsModule
import webtrekk.android.sdk.util.*

/**
 * [WorkManager] worker that retrieves the data from the data base, builds the requests and send them to the server.
 */
internal class SendRequestsWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        /**
         * [coroutineDispatchers] the injected coroutine dispatchers.
         */
        val coroutineDispatchers: CoroutineDispatchers = AppModule.dispatchers

        /**
         * [getCachedDataTracks] the injected internal interactor for getting the data from the data base.
         */
        val getCachedDataTracks: GetCachedDataTracks =
            InternalInteractorsModule.getCachedDataTracks()

        /**
         * [executeRequest] the injected internal interactor for executing the requests.
         */
        val executeRequest: ExecuteRequest = InternalInteractorsModule.executeRequest()

        /**
         * [ExecutePostRequest] the injected internal interactor for executing the requests.
         */
        val executePostRequest: ExecutePostRequest = InternalInteractorsModule.executePostRequest()

        /**
         * [logger] the injected logger from Webtrekk.
         */
        val logger by lazy { AppModule.logger }

        val trackDomainLocal = if (inputData.getString("trackDomain") != null) {
            inputData.getString("trackDomain")
        } else {
            trackDomain
        }
        val trackIdsLocal = if (inputData.getStringArray("trackIds") != null) {
            inputData.getStringArray("trackIds")!!.toList()
        } else {
            trackIds
        }
        // retrieves the data in the data base with state of NEW or FAILED only.
        // todo handle Result.failure()
        withContext(coroutineDispatchers.ioDispatcher) {
            getCachedDataTracks(
                GetCachedDataTracks.Params(
                    requestStates = listOf(
                        TrackRequest.RequestState.NEW,
                        TrackRequest.RequestState.FAILED
                    )
                )
            )
                .onSuccess { dataTracks ->
                    if (dataTracks.isNotEmpty()) {
                        logger.info("Executing the requests")
                        // Must execute requests sync and in order
                        if (batchSupported) {
                            dataTracks.asSequence().batch(requestPerBatch).forEach { group ->
                                val urlRequest =
                                    group.buildPostRequest(
                                        trackDomainLocal!!,
                                        trackIdsLocal, currentEverId, anonymous,
                                        anonymousParam
                                    )
                                logger.info("Sending request = $urlRequest, Request Body= " + urlRequest.stringifyRequestBody())

                                executePostRequest(
                                    ExecutePostRequest.Params(
                                        request = urlRequest,
                                        dataTracks = dataTracks
                                    )
                                )
                                    .onSuccess { logger.debug("Sent the request successfully $it") }
                                    .onFailure { logger.error("Failed to send the request $it") }
                                //   }
                            }
                        } else {
                            dataTracks.forEach { dataTrack ->

                                val urlRequest =
                                    dataTrack.buildUrlRequest(
                                        trackDomainLocal!!,
                                        trackIdsLocal,
                                        currentEverId,
                                        anonymous,
                                        anonymousParam
                                    )
                                logger.info("Sending request = $urlRequest")

                                executeRequest(
                                    ExecuteRequest.Params(
                                        request = urlRequest,
                                        dataTrack = dataTrack
                                    )
                                )
                                    .onSuccess { logger.debug("Sent the request successfully $it") }
                                    .onFailure { logger.error("Failed to send the request $it") }
                            }
                        }
                    }
                }
                .onFailure { logger.error("Error getting cached data tracks: $it") }
        }

        return Result.success()
    }

    companion object {
        const val TAG = "send_track_requests"
        const val TAG_ONE_TIME_WORKER = "send_track_requests_now"
    }
}
