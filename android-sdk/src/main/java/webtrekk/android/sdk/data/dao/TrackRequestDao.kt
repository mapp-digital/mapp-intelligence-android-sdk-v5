package webtrekk.android.sdk.data.dao

import androidx.room.*
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@Dao
internal interface TrackRequestDao {

    @Insert
    suspend fun setTrackRequest(trackRequest: TrackRequest): Long

    @Insert
    suspend fun setTrackRequests(trackRequests: List<TrackRequest>)

    @Transaction
    @Query("SELECT * FROM tracking_data ORDER BY time_stamp")
    suspend fun getTrackRequests(): List<DataTrack>

    @Delete
    suspend fun clearTrackRequests(trackRequests: List<TrackRequest>)
}
