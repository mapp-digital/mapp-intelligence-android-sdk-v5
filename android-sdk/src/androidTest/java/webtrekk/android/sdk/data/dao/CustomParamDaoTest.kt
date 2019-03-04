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

package webtrekk.android.sdk.data.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import webtrekk.android.sdk.data.DbTest
import webtrekk.android.sdk.data.customParams
import webtrekk.android.sdk.data.trackRequests

@RunWith(AndroidJUnit4::class)
internal class CustomParamDaoTest : DbTest() {

    @Test
    @Throws(Exception::class)
    fun getCustomParams() = runBlocking {
        assertThat(customParamDao.getCustomParams(), `is`(customParams))
    }

    @Test
    @Throws(Exception::class)
    fun getCustomParams_ByTrackId() = runBlocking {
        val customParamsById = customParamDao.getCustomParamsByTrackId(trackRequests[0].id)
        val filteredCustomParams = customParams.filter { it.trackId == trackRequests[0].id }

        assertThat(customParamsById, `is`(filteredCustomParams))
    }

    @Test
    @Throws(Exception::class)
    fun clearCustomParams_WhenTrackIsDeleted() = runBlocking {
        trackRequestDao.clearTrackRequests(listOf(trackRequests[0]))
        val filteredCustomParams = customParams.filter { it.trackId != trackRequests[0].id }

        // Verify that filtered custom params doesn't have the custom param with the deleted track id
        assertThat(customParamDao.getCustomParams(), `is`(filteredCustomParams))

        trackRequestDao.clearTrackRequests(trackRequests)

        // Verify that all custom params are deleted when all tracks are deleted
        assertThat(customParamDao.getCustomParams().size, `is`(0))
    }
}
