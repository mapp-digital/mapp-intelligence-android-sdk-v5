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
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.ExecuteRequest
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.extension.buildUrlRequest
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.logInfo
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.trackDomain
import webtrekk.android.sdk.util.trackIds

internal class SendRequestsWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    private val coroutineDispatchers: CoroutineDispatchers by inject()
    private val getCachedDataTracks: GetCachedDataTracks by inject()
    private val executeRequest: ExecuteRequest by inject()

    override val coroutineContext: CoroutineDispatcher
        get() = coroutineDispatchers.ioDispatcher

    override suspend fun doWork(): Result {
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
                    logInfo("Executing the requests")

                    // Must execute requests sync and in order
                    dataTracks.forEach { dataTrack ->
                        val urlRequest = dataTrack.buildUrlRequest(trackDomain, trackIds)
                        logInfo("Sending request = $urlRequest")

                        executeRequest(
                            ExecuteRequest.Params(
                                request = urlRequest,
                                dataTrack = dataTrack
                            )
                        )
                            .onSuccess { logDebug("Sent the request successfully $it") }
                            .onFailure { logError("Failed to send the request $it") }
                    }
                }
            }
            .onFailure { logError("Error getting cached data tracks: $it") }

        return Result.success()
    }

    companion object {
        const val TAG = "send_track_requests"
    }
}
