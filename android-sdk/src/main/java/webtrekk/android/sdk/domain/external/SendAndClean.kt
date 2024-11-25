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

package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.util.CoroutineDispatchers
import kotlin.coroutines.CoroutineContext

/**
 * The opting out use case, opting out will stop the SDK from tracking data, clearing all the data in the data base, or send them first then clean up, and canceling all work manager workers.
 */
internal class SendAndClean(
    coroutineContext: CoroutineContext,
    private val scheduler: Scheduler
) : ExternalInteractor<SendAndClean.Params> {

    private val _job = SupervisorJob()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        scope.launch {
            scheduler.sendRequestsThenCleanUp()
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param context the application context.
     * @param trackDomain must be set in the configuration,
     * otherwise webtrekk won't send any tracking data.
     * @param trackIds must be set in the configuration,
     * otherwise webtrekk won't send any tracking data.
     */
    data class Params(
        val context: Context,
        val trackDomain: String,
        val trackIds: List<String>,
        val config: Config
    )
}
