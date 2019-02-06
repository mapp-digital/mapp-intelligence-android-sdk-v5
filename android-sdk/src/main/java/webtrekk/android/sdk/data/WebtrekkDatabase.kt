package webtrekk.android.sdk.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.util.buildRoomDatabase

internal const val DATABASE_NAME = "webtrekk-test-db"

@Database(
    entities = [TrackRequest::class, CustomParam::class],
    version = 3,
    exportSchema = false
)
internal abstract class WebtrekkDatabase : RoomDatabase() {

    abstract fun trackRequestDao(): TrackRequestDao
    abstract fun customParamDataDao(): CustomParamDao
}

private lateinit var INSTANCE: WebtrekkDatabase

internal fun getWebtrekkDatabase(context: Context): WebtrekkDatabase {
    synchronized(WebtrekkDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = buildRoomDatabase(context, DATABASE_NAME, WebtrekkDatabase::class.java)
        }
    }

    return INSTANCE
}
