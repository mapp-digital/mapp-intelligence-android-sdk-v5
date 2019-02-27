package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.domain.InternalInteractor
import webtrekk.android.sdk.extension.toCustomParams

internal class CacheTrackRequestWithCustomParams(
    private val trackRequestRepository: TrackRequestRepository,
    private val customParamRepository: CustomParamRepository
) : InternalInteractor<CacheTrackRequestWithCustomParams.Params, DataTrack> {

    override suspend operator fun invoke(invokeParams: Params): Result<DataTrack> {
        return runCatching {
            val cachedTrackRequest =
                trackRequestRepository.addTrackRequest(invokeParams.trackRequest)

            val customParams =
                invokeParams.trackingParams.toCustomParams(cachedTrackRequest.getOrThrow().id)

            val cachedCustomParams = customParamRepository.addCustomParams(customParams)

            DataTrack(cachedTrackRequest.getOrThrow(), cachedCustomParams.getOrThrow())
        }
    }

    data class Params(val trackRequest: TrackRequest, val trackingParams: Map<String, String>)
}
