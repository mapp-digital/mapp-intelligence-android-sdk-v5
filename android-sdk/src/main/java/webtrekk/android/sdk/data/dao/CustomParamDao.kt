package webtrekk.android.sdk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import webtrekk.android.sdk.data.entity.CustomParam

@Dao
internal interface CustomParamDao {

    @Insert
    suspend fun setCustomParams(customParams: List<CustomParam>): List<Long>

    @Query("SELECT * FROM custom_params")
    suspend fun getCustomParams(): List<CustomParam>

    @Query("SELECT * FROM custom_params WHERE track_id = :trackId")
    suspend fun getCustomParamsByTrackId(trackId: Long): List<CustomParam>
}
