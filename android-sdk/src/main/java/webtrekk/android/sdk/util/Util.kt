package webtrekk.android.sdk.util

import android.os.Build
import java.util.*
import kotlin.random.Random

internal const val PLATFORM_NAME = "Android"

internal val currentOsVersion
    inline get() = Build.VERSION.RELEASE

internal val currentApiLevel
    inline get() = Build.VERSION.SDK_INT

internal val currentDeviceManufacturer
    inline get() = Build.MANUFACTURER

internal val currentDeviceModel
    inline get() = Build.MODEL

internal val currentCountry
    inline get() = Locale.getDefault().country

internal val currentLanguage
    inline get() = Locale.getDefault().language

internal val currentTimeZone
    inline get() = TimeZone.getDefault().rawOffset / 1000 / 60 / 60

internal val currentTimeStamp
    inline get() = System.currentTimeMillis()

internal fun generateEverId(): String {
    val date = currentTimeStamp / 1000
    val random = Random
    return "6${String.format("%010d%08d", date, random.nextLong(100000000))}"
}
