package webtrekk.android.sdk.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "custom_params",
    foreignKeys = [ForeignKey(
        entity = TrackRequest::class,
        parentColumns = ["id"],
        childColumns = ["track_id"],
        onDelete = CASCADE
    )],
    indices = [Index("track_id")]
)
internal data class CustomParam(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "custom_id") var customParamId: Long = 0,
    @ColumnInfo(name = "track_id") val trackId: Long,
    @ColumnInfo(name = "param_key") val paramKey: String,
    @ColumnInfo(name = "param_value") val paramValue: String
)
