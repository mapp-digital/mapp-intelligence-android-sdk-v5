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
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import android.util.Log
import webtrekk.android.sdk.ActiveConfig
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.WebtrekkImpl
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.domain.internal.ExecutePostRequest
import webtrekk.android.sdk.domain.internal.ExecuteRequest
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.batchSupported
import webtrekk.android.sdk.util.requestPerBatch

@OptIn(ExperimentalCoroutinesApi::class)
internal class SendRequestsWorkerTest {

    private val context: Context = mockk(relaxed = true)
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testDispatchers = CoroutineDispatchers(
        testDispatcher,
        testDispatcher,
        testDispatcher
    )

    private val mockLogger: Logger = mockk(relaxed = true)
    private val mockGetCachedDataTracks: GetCachedDataTracks = mockk()
    private val mockExecutePostRequest: ExecutePostRequest = mockk()
    private val mockExecuteRequest: ExecuteRequest = mockk()
    private val mockWebtrekkImpl: WebtrekkImpl = mockk(relaxed = true)
    private val mockConfig: Config = mockk(relaxed = true)

    private val activeConfig = ActiveConfig(
        trackDomains = "https://example.com",
        trackIds = listOf("1"),
        everId = null,
        everIdMode = GenerationMode.AUTO_GENERATED,
        userAgent = "ua",
        userMatchingId = null,
        anonymousParams = emptySet(),
        logLevel = Logger.Level.NONE,
        requestInterval = 15,
        requestsPerBatch = 2,
        exceptionLogLevel = ExceptionType.NONE,
        appFirstOpen = false,
        isOptOut = false,
        isAnonymous = false,
        isFragmentAutoTracking = false,
        isActivityAutoTracking = false,
        isAutoTracking = false,
        isBatchSupport = true,
        shouldMigrate = false,
        sendVersionInEachRequest = false,
        isUserMatching = false,
        temporarySessionId = null
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic("webtrekk.android.sdk.util.WebtrekkUtilKt")
        mockkObject(AppModule)
        mockkObject(InteractorModule)
        mockkObject(WebtrekkImpl)
        mockkStatic(Log::class)
        every { Log.isLoggable(any(), any()) } returns false
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.wtf(any(), any(), any()) } returns 0

        every { context.applicationContext } returns context

        every { AppModule.dispatchers } returns testDispatchers
        every { AppModule.logger } returns mockLogger

        every { batchSupported } returns true
        every { requestPerBatch } returns 2

        every { InteractorModule.getCachedDataTracks() } returns mockGetCachedDataTracks
        every { InteractorModule.executeRequest() } returns mockExecuteRequest
        every { InteractorModule.executePostRequest() } returns mockExecutePostRequest

        every { WebtrekkImpl.getInstance() } returns mockWebtrekkImpl
        every { mockWebtrekkImpl.config } returns mockConfig
        every { mockConfig.batchSupport } returns true
        every { mockConfig.requestPerBatch } returns 2
        every { mockWebtrekkImpl.getCurrentConfiguration() } returns activeConfig
        justRun { mockWebtrekkImpl.init(any()) }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when batch supported requests are sent oldest to newest`() = runTest(testScheduler) {
        val tracksOutOfOrder = listOf(
            dataTrackWithTimestamp(3),
            dataTrackWithTimestamp(1),
            dataTrackWithTimestamp(2)
        )
        coEvery { mockGetCachedDataTracks.invoke(any()) } returns Result.success(tracksOutOfOrder)

        val capturedBatches = mutableListOf<List<DataTrack>>()
        coEvery { mockExecutePostRequest.invoke(any()) } answers {
            val params = firstArg<ExecutePostRequest.Params>()
            capturedBatches += params.dataTracks
            Result.success(params.dataTracks)
        }

        val worker = TestListenableWorkerBuilder<SendRequestsWorker>(context).build()

        val result = worker.doWork()

        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        assertThat(capturedBatches.map { batch -> batch.map { it.trackRequest.timeStamp } })
            .containsExactly(listOf("1", "2"), listOf("3"))
    }

    private fun dataTrackWithTimestamp(timestamp: Long): DataTrack {
        val trackRequest = TrackRequest(
            name = "name-$timestamp",
            timeStamp = timestamp.toString(),
            forceNewSession = "0",
            appFirstOpen = "0"
        )
        return DataTrack(trackRequest = trackRequest)
    }
}
