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
import io.kotlintest.Spec
import io.kotlintest.specs.FeatureSpec
import io.mockk.mockkClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineContext
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.EmptyLogger
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.core.MyKoinContext
import webtrekk.android.sdk.util.configuration
import webtrekk.android.sdk.util.loggerModule
import kotlin.coroutines.CoroutineContext

internal abstract class AbstractExternalInteractor : KoinComponent, CoroutineScope, FeatureSpec() {

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext
    private val appContext = mockkClass(Context::class, relaxed = true)

    override fun beforeSpec(spec: Spec) {
        Webtrekk.getInstance().init(appContext, configuration)
        startKoin {
            modules(
                listOf(loggerModule
                ))
            EmptyLogger()
        }
    }

    override fun afterSpec(spec: Spec) {
        MyKoinContext.koinApp?.close()
        stopKoin()
        coroutineContext.cancel()
    }
}
