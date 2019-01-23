package webtrekk.android.sdk.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import webtrekk.android.sdk.util.*

@Entity(tableName = "tracking_data")
internal data class TrackRequest(
    @ColumnInfo(name = "context_name") var name: String,
    @ColumnInfo(name = "api_level") var apiLevel: String? = currentApiLevel.toString(),
    @ColumnInfo(name = "os_version") var osVersion: String? = currentOsVersion,
    @ColumnInfo(name = "device_manufacturer") var deviceManufacturer: String? = currentDeviceManufacturer,
    @ColumnInfo(name = "device_model") var deviceModel: String? = currentDeviceModel,
    @ColumnInfo(name = "country") var country: String? = currentCountry,
    @ColumnInfo(name = "language") var language: String? = currentLanguage,
    @ColumnInfo(name = "screen_resolution") var screenResolution: String = "",
    @ColumnInfo(name = "time_zone") var timeZone: String? = currentTimeZone.toString(),
    @ColumnInfo(name = "time_stamp") var timeStamp: String? = currentTimeStamp.toString(),
    @ColumnInfo(name = "fns") var fns: String? = currentSession,
    @ColumnInfo(name = "one") var one: String? = appFirstStart
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    override fun toString() =
        "$id $name $apiLevel $osVersion $deviceManufacturer $deviceModel $country $language $screenResolution $timeZone $timeStamp $fns $one"
}
