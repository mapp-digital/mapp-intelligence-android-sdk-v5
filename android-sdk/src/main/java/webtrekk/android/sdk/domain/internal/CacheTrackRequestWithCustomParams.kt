package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.*
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import kotlin.coroutines.CoroutineContext

internal class CacheTrackRequestWithCustomParams(
    trackRequestRepository: TrackRequestRepository,
    customParamRepository: CustomParamRepository,
    coroutineContext: CoroutineContext
) {

    private val job = Job()
    private val scope = CoroutineScope(coroutineContext + job)

    private val cacheTrackRequest =
        CacheTrackRequest(trackRequestRepository, scope.coroutineContext)
    private val addCustomParams = AddCustomParams(customParamRepository, scope.coroutineContext)

    operator fun invoke(
        trackRequest: TrackRequest,
        trackingParams: Map<String, String>
    ) = scope.launch {
        val cachedTrackRequest = cacheTrackRequest(trackRequest).await() as? TrackRequest

        cachedTrackRequest?.let {
            if (trackingParams.isNotEmpty()) {
                val customTrackingParams =
                    trackingParams.map { param ->
                        CustomParam(
                            cachedTrackRequest.id,
                            param.key,
                            param.value
                        )
                    }

                addCustomParams(customTrackingParams)
            }
        }
    }
}
