package webtrekk.android.sdk.data.dao

import androidx.room.*
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

@Dao
internal interface TrackRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTrackRequest(trackRequest: TrackRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTrackRequests(trackRequests: List<TrackRequest>)

    @Transaction
    @Query("SELECT * FROM tracking_data ORDER BY time_stamp")
    suspend fun getTrackRequests(): List<DataTrack>

    @Transaction
    @Query("SELECT * FROM tracking_data WHERE request_state IN (:requestStates) ORDER BY time_stamp")
    suspend fun getTrackRequestsByState(requestStates: List<String>): List<DataTrack>

    @Update
    suspend fun updateTrackRequests(vararg trackRequests: TrackRequest)

    @Delete
    suspend fun clearTrackRequests(trackRequests: List<TrackRequest>)
}
