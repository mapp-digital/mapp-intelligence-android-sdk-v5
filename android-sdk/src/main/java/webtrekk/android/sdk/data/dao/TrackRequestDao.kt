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

package webtrekk.android.sdk.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

/**
 * Dao interface has [TrackRequest] database interactions.
 */
@Dao
internal interface TrackRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTrackRequest(trackRequest: TrackRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTrackRequests(trackRequests: List<TrackRequest>)

    @Transaction
    @Query("SELECT * FROM tracking_data ORDER BY time_stamp, ever_id")
    suspend fun getTrackRequests(): List<DataTrack>

    @Transaction
    @Query("SELECT * FROM tracking_data WHERE request_state IN (:requestStates) ORDER BY time_stamp, ever_id")
    suspend fun getTrackRequestsByState(requestStates: List<String>): List<DataTrack>

    @Transaction
    @Query("UPDATE tracking_data SET ever_id=:everId")
    suspend fun updateEverId(everId:String?)

    @Update
    suspend fun updateTrackRequests(vararg trackRequests: TrackRequest)

    @Update
    suspend fun updateTrackRequests(trackRequests: List<TrackRequest>)

    @Delete
    suspend fun clearTrackRequests(trackRequests: List<TrackRequest>)

    @Query("DELETE FROM tracking_data")
    suspend fun clearAllTrackRequests()
}
