package webtrekk.android.sdk.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import webtrekk.android.sdk.data.model.DataTrack
import webtrekk.android.sdk.data.model.TrackRequest

@Dao
internal interface TrackRequestDao {

    @Insert
    fun setTrackRequest(trackRequest: TrackRequest): Long

    @Insert
    fun setTrackRequests(trackRequests: List<TrackRequest>)

    @Query("SELECT * FROM DataTrackView")
    fun getTrackRequests(): List<DataTrack>

    @Delete
    fun clearTrackRequests(trackRequests: List<TrackRequest>)
}
