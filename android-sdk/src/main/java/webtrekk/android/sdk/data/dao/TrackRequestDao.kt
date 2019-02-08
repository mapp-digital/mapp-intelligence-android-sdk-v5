package webtrekk.android.sdk.data.dao

import androidx.room.*
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@Dao
internal interface TrackRequestDao {

    @Insert
    fun setTrackRequest(trackRequest: TrackRequest): Long

    @Insert
    fun setTrackRequests(trackRequests: List<TrackRequest>)

    @Transaction
    @Query("SELECT * FROM tracking_data ORDER BY time_stamp")
    fun getTrackRequests(): List<DataTrack>

    @Delete
    fun clearTrackRequests(trackRequests: List<TrackRequest>)
}
