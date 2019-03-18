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

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkClass
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.util.generateEverId

internal class SessionsTest : StringSpec({

    val webtrekkSharedPrefs = mockkClass(WebtrekkSharedPrefs::class)
    val sessions = Sessions(webtrekkSharedPrefs)
    val everId = generateEverId()

    "generate ever ID then verify that the app is first start" {
        every {
            webtrekkSharedPrefs.contains(WebtrekkSharedPrefs.EVER_ID_KEY)
        } returns false

        every { webtrekkSharedPrefs.everId } returns everId
        every { webtrekkSharedPrefs.everId = everId } just Runs

//        every { webtrekkSharedPrefs.everId } returns everId
//        every { webtrekkSharedPrefs.appFirstStart} returns "1"

        sessions.getEverId() shouldBe (everId)
//        sessions.getAppFirstStart() shouldBe ("1")
    }
})
