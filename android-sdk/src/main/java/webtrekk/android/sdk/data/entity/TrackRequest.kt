package webtrekk.android.sdk.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import webtrekk.android.sdk.util.*

@Entity(tableName = "tracking_data")
internal data class TrackRequest(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "context_name") val name: String,
    @ColumnInfo(name = "api_level") val apiLevel: String? = currentApiLevel.toString(),
    @ColumnInfo(name = "os_version") val osVersion: String? = currentOsVersion,
    @ColumnInfo(name = "device_manufacturer") val deviceManufacturer: String? = currentDeviceManufacturer,
    @ColumnInfo(name = "device_model") val deviceModel: String? = currentDeviceModel,
    @ColumnInfo(name = "country") val country: String? = currentCountry,
    @ColumnInfo(name = "language") val language: String? = currentLanguage,
    @ColumnInfo(name = "screen_resolution") val screenResolution: String? = "0",
    @ColumnInfo(name = "time_zone") val timeZone: String? = currentTimeZone.toString(),
    @ColumnInfo(name = "time_stamp") val timeStamp: String? = currentTimeStamp.toString(),
    @ColumnInfo(name = "fns") val fns: String = currentSession,
    @ColumnInfo(name = "one") val one: String = appFirstStart,
    @ColumnInfo(name = "webtrekk_version") val webtrekkVersion: String = currentWebtrekkVersion
)
