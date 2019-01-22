package webtrekk.android.sdk.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.dao.TrackRequestDao
import java.io.IOException

internal abstract class DbTest {

    lateinit var webtrekkDatabase: WebtrekkDatabase
    lateinit var trackRequestDao: TrackRequestDao
    lateinit var customParamDao: CustomParamDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        webtrekkDatabase = Room.inMemoryDatabaseBuilder(
            context, WebtrekkDatabase::class.java
        ).build()

        trackRequestDao = webtrekkDatabase.trackRequestDao()
        customParamDao = webtrekkDatabase.customParamDataDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        webtrekkDatabase.close()
    }
}
