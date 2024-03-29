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

package webtrekk.android.sdk

import android.content.Context
import io.kotlintest.Spec
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import webtrekk.android.sdk.domain.external.Optout

internal class WebtrekkTest : StringSpec() {

    private val webtrekk = Webtrekk.getInstance()
    private val config = mockkClass(Config::class)
    private val appContext = mockkClass(Context::class, relaxed = true)

    private val optOut = mockk<Optout>(relaxed = true)

    override fun beforeSpec(spec: Spec) {
    }

    override fun afterSpec(spec: Spec) {
    }

    init {
        ("throw IllegalStateException if init() not called first before invoking any other method") {
            every { optOut.isActive() } returns true

            shouldThrow<IllegalStateException> {
                webtrekk.hasOptOut()
            }
        }
    }
}
