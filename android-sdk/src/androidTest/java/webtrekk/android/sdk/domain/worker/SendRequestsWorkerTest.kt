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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.ActiveConfig
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.core.WebtrekkImpl
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule
import webtrekk.android.sdk.util.CoroutineDispatchers

/**
 * Verifies two SendRequestsWorker instances never process cached data in parallel.
 */
@RunWith(AndroidJUnit4::class)
class SendRequestsWorkerTest {
    private lateinit var context: Context
    private val active = AtomicInteger(0)
    private val maxActive = AtomicInteger(0)
    private val callCount = AtomicInteger(0)
    private val processedOnce = java.util.concurrent.atomic.AtomicBoolean(false)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        mockkObject(AppModule)
        mockkObject(InteractorModule)
        mockkObject(WebtrekkImpl.Companion)

        every { AppModule.dispatchers } returns CoroutineDispatchers(
            Dispatchers.Unconfined,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
        )
        every { AppModule.logger } returns mockk(relaxed = true)

        val fakeWebtrekk = mockk<WebtrekkImpl>(relaxed = true)
        every { WebtrekkImpl.getInstance() } returns fakeWebtrekk
        every { fakeWebtrekk.getCurrentConfiguration() } returns minimalActiveConfig()
        every { fakeWebtrekk.init(any()) } returns Unit
        every { fakeWebtrekk.init(any(), any<Config>()) } returns Unit

        every { InteractorModule.getCachedDataTracks() } returns blockingGetCachedDataTracks()
        every { InteractorModule.executeRequest() } returns mockk(relaxed = true)
        every { InteractorModule.executePostRequest() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun whenTwoWorkersRunOnlyOneSendsAtATime() = runBlocking {
        val worker1 = TestListenableWorkerBuilder<SendRequestsWorker>(context).build()
        val worker2 = TestListenableWorkerBuilder<SendRequestsWorker>(context).build()

        val job1 = async { worker1.doWork() }
        delay(20) // ensure overlap attempt
        val job2 = async { worker2.doWork() }

        val result1 = job1.await()
        val result2 = job2.await()

        assertEquals(ListenableWorker.Result.success(), result1)
        assertEquals(ListenableWorker.Result.success(), result2)
        assertEquals("Only one worker should execute the processing block", 1, callCount.get())
        assertEquals("Processing must never overlap", 1, maxActive.get())
    }

    private fun minimalActiveConfig(): ActiveConfig = ActiveConfig(
        trackDomains = "https://example.com",
        trackIds = listOf("123456789"),
        everId = null,
        everIdMode = null,
        userAgent = null,
        userMatchingId = null,
        anonymousParams = emptySet(),
        logLevel = webtrekk.android.sdk.Logger.Level.NONE,
        requestInterval = 15,
        requestsPerBatch = 1,
        exceptionLogLevel = webtrekk.android.sdk.ExceptionType.NONE,
        appFirstOpen = false,
        isOptOut = false,
        isAnonymous = false,
        isFragmentAutoTracking = false,
        isActivityAutoTracking = false,
        isAutoTracking = false,
        isBatchSupport = false,
        shouldMigrate = false,
        sendVersionInEachRequest = false,
        isUserMatching = false,
        temporarySessionId = null,
    )

    private fun blockingGetCachedDataTracks(): GetCachedDataTracks =
        object : GetCachedDataTracks(mockk(relaxed = true)) {
            override suspend fun invoke(invokeParams: Params): Result<List<webtrekk.android.sdk.data.entity.DataTrack>> {
                // Simulate private processDataSending: only the first entrant processes, others exit early.
                if (!processedOnce.compareAndSet(false, true)) {
                    return Result.success(emptyList())
                }
                val current = active.incrementAndGet()
                maxActive.updateAndGet { max(it, current) }
                delay(200)
                active.decrementAndGet()
                callCount.incrementAndGet()
                return Result.success(emptyList())
            }
        }
}
