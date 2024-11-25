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
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule
import webtrekk.android.sdk.util.CoroutineDispatchers

/**
 * [WorkManager] worker responsible about cleaning the successfully executed cached requests.
 */
internal class CleanUpWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {
    /**
     * [coroutineDispatchers] the injected coroutine dispatchers.
     */
    val coroutineDispatchers: CoroutineDispatchers = AppModule.dispatchers

    override suspend fun doWork(): Result = coroutineScope {
        withContext(coroutineDispatchers.ioDispatcher) {
            mutex.withLock {
                /**
                 * [logger] the injected logger from Webtrekk.
                 */
                val logger by lazy { AppModule.logger }

                logger.debug("doWork - starting... ${tags.joinToString(separator = ", ")}")
                // this check and initialization is needed for cross platform solutions
                if (!Webtrekk.getInstance().isInitialized()) {
                    val configJson = WebtrekkSharedPrefs(applicationContext).configJson
                    val config = WebtrekkConfiguration.fromJson(configJson)
                    Webtrekk.getInstance().init(applicationContext, config)
                }

                /**
                 * [getCachedDataTracks] the injected internal interactor for getting the data from the data base.
                 */
                val getCachedDataTracks: GetCachedDataTracks =
                    InteractorModule.getCachedDataTracks()

                /**
                 * [clearTrackRequests] the injected internal interactor for deleting the data in the data base.
                 */
                val clearTrackRequests: ClearTrackRequests = InteractorModule.clearTrackRequest()

                // get the data from the data base with state DONE only.
                getCachedDataTracks(
                    GetCachedDataTracks.Params(
                        requestStates = listOf(
                            TrackRequest.RequestState.DONE
                        )
                    )
                ).onSuccess { dataTracks ->
                    if (dataTracks.isNotEmpty()) {
                        logger.info("Cleaning up the completed requests")

                        clearTrackRequests(ClearTrackRequests.Params(trackRequests = dataTracks.map { it.trackRequest }))
                            .onSuccess { logger.debug("Cleaned up the completed requests successfully") }
                            .onFailure {
                                logger.error("Failed while cleaning up the completed requests: $it")
                            }
                    }
                }.onFailure { logger.error("Error getting the cached completed requests: $it") }

                return@withContext Result.success()
            }
        }
    }

    companion object {
        val mutex = Mutex()
        const val TAG = "clean_up"
    }
}
