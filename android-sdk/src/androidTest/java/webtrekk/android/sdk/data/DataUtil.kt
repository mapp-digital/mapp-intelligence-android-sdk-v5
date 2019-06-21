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
