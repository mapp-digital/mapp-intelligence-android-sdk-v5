package webtrekk.android.sdk.data.entity

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView("SELECT * FROM tracking_data LEFT JOIN custom_params ON tracking_data.id = custom_params.track_id ORDER BY tracking_data.time_stamp")
internal data class DataTrackView(
    @Embedded var trackRequest: TrackRequest,
    @Embedded var customParam: CustomParam?
)

internal typealias DataTrack = DataTrackView
