package webtrekk.android.sdk.data.model

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView("SELECT * FROM tracking_data LEFT JOIN custom_params ON tracking_data.id = custom_params.track_id")
internal data class DataTrackView(
    @Embedded var trackRequest: TrackRequest,
    @Embedded var customParam: CustomParam?
)

internal typealias DataTrack = DataTrackView
