package webtrekk.android.sdk.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.data.model.DataTrackView
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.model.TrackRequest

internal const val DATABASE_NAME = "webtrekk-test-db"

// todo : implement migration
@Database(
    entities = [TrackRequest::class, CustomParam::class],
    version = 2,
    exportSchema = false,
    views = arrayOf(DataTrackView::class)
)
internal abstract class WebtrekkDatabase : RoomDatabase() {

    abstract fun trackRequestDao(): TrackRequestDao
    abstract fun customParamDataDao(): CustomParamDao

    companion object {

        @Volatile
        private var INSTANCE: WebtrekkDatabase? = null

        fun getInstance(context: Context): WebtrekkDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): WebtrekkDatabase {
            return Room.databaseBuilder(context, WebtrekkDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}
