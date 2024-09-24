package webtrekk.android.sdk

import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.external.TrackCustomPage
import webtrekk.android.sdk.domain.external.TrackException
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
    webtrekkVersion = BuildConfig.LIBRARY_VERSION,
    appVersionName = "1.0.0",
    appVersionCode = "1",
    requestState = TrackRequest.RequestState.NEW,
    everId = null
)

internal val exceptionRequest = TrackException.Params(
    trackRequest = trackRequest,
    isOptOut = false,
    exception = RuntimeException("Test Exception"),
    exceptionType = ExceptionType.CAUGHT,
    context = null
)

internal val customTrackRequest = TrackCustomPage.Params(
    trackRequest,
    mapOf(UrlParams.EVENT_NAME to "Page 1"),
    isOptOut = false,
    context = null
)

internal val trackRequests = listOf(
    TrackRequest(name = "page 1", forceNewSession = "1", appFirstOpen = "1", everId = "1234").apply { this.id = 1 },
    TrackRequest(name = "page 2", forceNewSession = "0", appFirstOpen = "0", everId = "1234").apply { this.id = 2 },
    TrackRequest(name = "page 3", forceNewSession = "0", appFirstOpen = "0", everId = "1234").apply { this.id = 3 },
    TrackRequest(name = "page 4", forceNewSession = "0", appFirstOpen = "0", everId = "1234").apply { this.id = 4 }
)

internal val customParams = listOf(
    CustomParam(
        customParamId = 1,
        trackId = 1,
        paramKey = "cs",
        paramValue = "val 1"
    ),
    CustomParam(
        customParamId = 2,
        trackId = 1,
        paramKey = "cd",
        paramValue = "val 2"
    ),
    CustomParam(
        customParamId = 3,
        trackId = 2,
        paramKey = "cs",
        paramValue = "val 3"
    )
)

internal val dataTracks = listOf(
    DataTrack(
        trackRequest = trackRequests[0],
        customParams = listOf(customParams[0], customParams[1])
    ),
    DataTrack(
        trackRequest = trackRequests[1],
        customParams = listOf(customParams[2])
    ),
    DataTrack(
        trackRequest = trackRequests[2],
        customParams = emptyList()
    ),
    DataTrack(trackRequest = trackRequests[3], customParams = emptyList())
)
