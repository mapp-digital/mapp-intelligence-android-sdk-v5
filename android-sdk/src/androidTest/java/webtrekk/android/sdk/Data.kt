package webtrekk.android.sdk

import webtrekk.android.sdk.data.entity.TrackRequest
import java.util.Date

val trackDomain = "www.webtrekk.com"
val trackIds = listOf<String>("123456789")
val userDefinedEverId = "0123456789"
val webtrekkConfigurationBuilder = WebtrekkConfiguration.Builder(trackIds, trackDomain)
internal val trackRequest = TrackRequest(
    id = 1000L,
    name = "0",
    apiLevel = "34",
    osVersion = "Android 14",
    deviceManufacturer = "Samsung",
    deviceModel = "Samsung s22+",
    country = "Serbia",
    language = "English",
    screenResolution = "1920x1080",
    timeZone = "Belgrade (+1)",
    timeStamp = Date().time.toString(),
    forceNewSession = "0",
    appFirstOpen = "0",
    webtrekkVersion = "5.1.11",
    appVersionName = "1.0.0",
    appVersionCode = "1",
    requestState = TrackRequest.RequestState.NEW,
    everId = null
)