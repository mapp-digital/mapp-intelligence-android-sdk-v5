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

package webtrekk.android.sdk.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import webtrekk.android.sdk.customParams
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.trackRequests
import java.io.IOException

internal abstract class DbTest {

    lateinit var webtrekkDatabase: WebtrekkDatabase
    lateinit var trackRequestDao: TrackRequestDao
    lateinit var customParamDao: CustomParamDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        webtrekkDatabase = Room.inMemoryDatabaseBuilder(
            context, WebtrekkDatabase::class.java
        ).build()

        trackRequestDao = webtrekkDatabase.trackRequestDao()
        customParamDao = webtrekkDatabase.customParamDataDao()

        runBlocking {
            trackRequestDao.setTrackRequests(trackRequests)
            customParamDao.setCustomParams(customParams)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        webtrekkDatabase.close()
    }
}
