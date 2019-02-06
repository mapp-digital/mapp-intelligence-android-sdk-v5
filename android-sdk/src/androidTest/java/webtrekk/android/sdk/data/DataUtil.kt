package webtrekk.android.sdk.data

import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest

internal val trackRequests = listOf(
    TrackRequest(name = "page 1", fns = "1", one = "1").apply { this.id = 1 },
    TrackRequest(name = "page 2", fns = "0", one = "0").apply { this.id = 2 },
    TrackRequest(name = "page 3", fns = "0", one = "0").apply { this.id = 3 },
    TrackRequest(name = "page 4", fns = "0", one = "0").apply { this.id = 4 }
)

internal val customParams = listOf(
    CustomParam(customParamId = 1, trackId = 1, paramKey = "cs", paramValue = "val 1"),
    CustomParam(customParamId = 2, trackId = 1, paramKey = "cd", paramValue = "val 2"),
    CustomParam(customParamId = 3, trackId = 2, paramKey = "cs", paramValue = "val 3")
)

internal val dataTracks = listOf(
    DataTrack(trackRequest = trackRequests[0], customParams = listOf(customParams[0], customParams[1])),
    DataTrack(trackRequest = trackRequests[1], customParams = listOf(customParams[2])),
    DataTrack(trackRequest = trackRequests[2], customParams = emptyList()),
    DataTrack(trackRequest = trackRequests[3], customParams = emptyList())
)
