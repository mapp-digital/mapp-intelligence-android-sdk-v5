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

package webtrekk.android.sdk.core

import androidx.work.Constraints

/**
 * An interface contains all the methods that will be used for scheduling the workers by [WorkManager].
 */
internal interface Scheduler {

    /**
     * Schedule a periodic worker to send the requests every [repeatInterval] times with the [constraints] in the [Config].
     *
     * NOTE, that the minimum period is 15 minutes by the [WorkManager], and the maximum should be up to 1 hour, so our servers can show the correct data in the time frames.
     *
     * @param repeatInterval the periodic time that will be used by [WorkManager] to send the requests from the cache to the server.
     * @param constraints the [WorkManager] constraints that will be applied on that worker.
     */
    suspend fun scheduleSendRequests(repeatInterval: Long, constraints: Constraints)

    /**
     * A one time worker that will be used to send all available requests in the cache to the server, then cleaning up the cache. Used for Opt out.
     */
    suspend fun sendRequestsThenCleanUp()

    /**
     * A worker that is scheduled to clean up the requests in the cache that are already sent to the server.
     */
    suspend fun scheduleCleanUp()

    /**
     * Cancel current periodic worker that is used to send the request every n times. Used for Opt out.
     */
    suspend fun cancelScheduleSendRequests()

    /**
     * Delete records about completed or canceled works
     */
    suspend fun pruneWorks()
}