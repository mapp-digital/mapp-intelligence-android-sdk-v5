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
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import webtrekk.android.sdk.data.WebtrekkDatabase.Companion.DATABASE_NAME
import webtrekk.android.sdk.data.converter.RequestStateConverter
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.TrackRequest

@Database(
    entities = [TrackRequest::class, CustomParam::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(RequestStateConverter::class)
internal abstract class WebtrekkDatabase : RoomDatabase() {

    abstract fun trackRequestDao(): TrackRequestDao
    abstract fun customParamDataDao(): CustomParamDao

    companion object {
        const val DATABASE_NAME = "webtrekk-db"
    }
}

private lateinit var INSTANCE: WebtrekkDatabase

internal fun getWebtrekkDatabase(context: Context): WebtrekkDatabase {
    synchronized(WebtrekkDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = webtrekk.android.sdk.util.buildRoomDatabase(
                context,
                DATABASE_NAME,
                WebtrekkDatabase::class.java
            )
        }
    }

    return INSTANCE
}
