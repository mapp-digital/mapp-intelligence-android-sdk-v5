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

package webtrekk.android.sdk.domain

import kotlinx.coroutines.CoroutineScope
import webtrekk.android.sdk.util.CoroutineDispatchers

/**
 * Internal interactors are the use cases handling the communication with the data layer.
 */
internal interface InternalInteractor<in T, R : Any> {

    /**
     * A suspendable function which returns [R] wrapped in a [Result] with either success or failed state to be handled in external interactors.
     *
     * @param invokeParams the params that is associated per use case.
     */
    suspend operator fun invoke(invokeParams: T): Result<R>
}

/**
 * External interactors are the business use cases of the SDK, they have the main logic of the use cases, and they responsible of launching coroutines and handling the asynchronous. They are built on top of internal interactors rather than communicating with the data layer directly and handling the results coming from internal interactors.
 */
internal interface ExternalInteractor<in T> {

    /**
     * Every external interactor has its own [CoroutineScope] derived from the parent scope in [WebtrekkImpl], so canceling the scope in [WebtrekkImpl] will lead to canceling the coroutines running in external interactors. You can override [scope] with your own [Job] or [CoroutineScope], but most importantly, you have to pass the [CoroutineScope] in [WebtrekkImpl] to external interactors.
     */
    val scope: CoroutineScope

    /**
     * Execute the logic inside [invoke], giving [invokeParams] and [coroutineDispatchers].
     *
     * @param invokeParams the params that is associated per use case.
     * @param coroutineDispatchers the custom coroutine dispatcher for changing the dispatcher depends on the use case.
     */
    operator fun invoke(invokeParams: T, coroutineDispatchers: CoroutineDispatchers)
}