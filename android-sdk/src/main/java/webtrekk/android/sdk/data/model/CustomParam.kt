package webtrekk.android.sdk.data.model

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
    @ColumnInfo(name = "track_id") var trackId: Long,
    @ColumnInfo(name = "param_key") var paramKey: String,
    @ColumnInfo(name = "param_value") var paramValue: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "custom_id")
    var customParamId: Long = 0

    override fun toString(): String {
        return "$trackId $paramKey -> $paramValue"
    }
}
