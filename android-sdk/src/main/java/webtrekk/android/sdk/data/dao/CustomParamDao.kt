package webtrekk.android.sdk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import webtrekk.android.sdk.data.entity.CustomParam

@Dao
internal interface CustomParamDao {

    @Insert
    fun setCustomParams(customParams: List<CustomParam>)

    @Query("SELECT * FROM custom_params")
    fun getCustomParams(): List<CustomParam>

    @Query("SELECT * FROM custom_params WHERE track_id = :trackId")
    fun getCustomParamsByTrackId(trackId: Long): List<CustomParam>
}
