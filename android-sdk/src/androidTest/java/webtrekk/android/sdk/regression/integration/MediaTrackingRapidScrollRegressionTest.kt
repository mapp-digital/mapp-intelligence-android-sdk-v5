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
 */

package webtrekk.android.sdk.regression.integration

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.events.eventParams.MediaParameters
import webtrekk.android.sdk.module.DataModule
import webtrekk.android.sdk.regression.RegressionTestBase

/**
 * Regression tests for fast media switching scenarios (e.g. rapid feed scrolling).
 *
 * The goal is to ensure each media item gets exactly one INIT/PLAY/STOP sequence
 * and no duplicate media actions are cached by the SDK for the same media item.
 */
class MediaTrackingRapidScrollRegressionTest : RegressionTestBase() {

    @Test
    fun regression_rapid_video_switching_has_no_duplicate_media_actions() = runBlocking {
        val webtrekk = Webtrekk.getInstance()
        webtrekk.init(context, config)
        waitForAsyncOperations(300)

        DataModule.trackRequestDao.clearAllTrackRequests()

        val pageName = "RapidScrollFeed"
        val mediaPrefix = "rapid-video-"
        val videoCount = 80
        val expectedEvents = videoCount * 3

        repeat(videoCount) { index ->
            val mediaName = "$mediaPrefix$index"
            webtrekk.trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(MediaParameters.Action.INIT, position = 0, duration = 12)
            )
            webtrekk.trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(MediaParameters.Action.PLAY, position = 1, duration = 12)
            )
            webtrekk.trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(MediaParameters.Action.STOP, position = 2, duration = 12)
            )
        }

        waitForAsyncOperations(500)

        val mediaEvents = waitForMediaEvents(
            mediaPrefix = mediaPrefix,
            expectedCount = expectedEvents,
            timeoutMs = 15_000L
        )

        val countsByMediaAction = mediaEvents.groupingBy { it }.eachCount()
        val actionsDistribution = mediaEvents.groupingBy { it.action }.eachCount()

        assertThat(
            "Expected exact number of media events for rapid switching scenario. " +
                "Actions distribution: $actionsDistribution",
            mediaEvents.size,
            equalTo(expectedEvents)
        )

        repeat(videoCount) { index ->
            val mediaName = "$mediaPrefix$index"
            assertThat(
                "INIT must be tracked exactly once for $mediaName",
                countsByMediaAction[MediaAction(mediaName, MediaParameters.Action.INIT.code())] ?: 0,
                equalTo(1)
            )
            assertThat(
                "PLAY must be tracked exactly once for $mediaName",
                countsByMediaAction[MediaAction(mediaName, MediaParameters.Action.PLAY.code())] ?: 0,
                equalTo(1)
            )
            assertThat(
                "STOP must be tracked exactly once for $mediaName",
                countsByMediaAction[MediaAction(mediaName, MediaParameters.Action.STOP.code())] ?: 0,
                equalTo(1)
            )
        }
    }

    private suspend fun waitForMediaEvents(
        mediaPrefix: String,
        expectedCount: Int,
        timeoutMs: Long
    ): List<MediaAction> {
        val deadline = System.currentTimeMillis() + timeoutMs
        var latest = readMediaActions(mediaPrefix)

        while (latest.size < expectedCount && System.currentTimeMillis() < deadline) {
            delay(100)
            latest = readMediaActions(mediaPrefix)
        }

        return latest
    }

    private suspend fun readMediaActions(mediaPrefix: String): List<MediaAction> {
        return DataModule.trackRequestDao.getTrackRequests()
            .mapNotNull { dataTrack ->
                val params = dataTrack.customParams.associate { it.paramKey to it.paramValue }
                val mediaName = params[UrlParams.MEDIA_NAME] ?: return@mapNotNull null
                val action = params[MediaParam.MEDIA_ACTION] ?: return@mapNotNull null
                if (!mediaName.startsWith(mediaPrefix)) return@mapNotNull null
                MediaAction(mediaName, action)
            }
    }

    private fun mediaParams(
        action: MediaParameters.Action,
        position: Int,
        duration: Int
    ): TrackingParams {
        return TrackingParams().apply {
            putAll(
                mapOf(
                    MediaParam.MEDIA_ACTION to action.code(),
                    MediaParam.MEDIA_POSITION to position.toString(),
                    MediaParam.MEDIA_DURATION to duration.toString()
                )
            )
        }
    }

    private data class MediaAction(
        val mediaName: String,
        val action: String
    )
}
