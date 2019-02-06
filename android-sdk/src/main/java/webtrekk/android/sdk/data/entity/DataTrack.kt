package webtrekk.android.sdk.data.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class DataTrack(
    @Embedded var trackRequest: TrackRequest,
    @Relation(
        parentColumn = "id",
        entityColumn = "track_id"
    ) var customParams: List<CustomParam> = arrayListOf()
)
