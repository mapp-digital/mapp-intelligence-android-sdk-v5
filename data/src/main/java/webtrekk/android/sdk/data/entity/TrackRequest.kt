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

package webtrekk.android.sdk.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import webtrekk.android.sdk.core.util.currentApiLevel
import webtrekk.android.sdk.core.util.currentCountry
import webtrekk.android.sdk.core.util.currentDeviceManufacturer
import webtrekk.android.sdk.core.util.currentDeviceModel
import webtrekk.android.sdk.core.util.currentOsVersion
import webtrekk.android.sdk.core.util.currentLanguage
import webtrekk.android.sdk.core.util.currentTimeZone
import webtrekk.android.sdk.core.util.currentTimeStamp
import webtrekk.android.sdk.core.util.currentWebtrekkVersion

@Entity(tableName = "tracking_data")
data class TrackRequest(
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
    @ColumnInfo(name = "fns") val fns: String,
    @ColumnInfo(name = "app_first_start") val one: String,
    @ColumnInfo(name = "webtrekk_version") val webtrekkVersion: String = currentWebtrekkVersion,
    @ColumnInfo(name = "request_state") var requestState: RequestState = RequestState.NEW
) {
    enum class RequestState(val value: String) {
        NEW("NEW"),
        DONE("DONE"),
        FAILED("FAILED")
    }
}
