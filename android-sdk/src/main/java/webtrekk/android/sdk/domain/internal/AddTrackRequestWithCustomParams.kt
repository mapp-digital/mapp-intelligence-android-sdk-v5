package webtrekk.android.sdk.domain.internal

import android.util.Log
import kotlinx.coroutines.*
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.model.TrackRequest
import kotlin.coroutines.CoroutineContext

internal class AddTrackRequestWithCustomParams(
    private val addTrackRequest: AddTrackRequest,
    private val addCustomParams: AddCustomParams,
    coroutineContext: CoroutineContext
) {

    var scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    operator fun invoke(
        trackRequest: TrackRequest,
        trackingParams: Map<String, String>
    ) = scope.launch {
        val data = addTrackRequest(trackRequest).await() as? TrackRequest

        data?.let {
            if (trackingParams.isNotEmpty()) {
                val params: MutableList<CustomParam> = ArrayList()

                for ((key, value) in trackingParams) {
                    params.add(
                        CustomParam(
                            data.id,
                            key,
                            value
                        )
                    )
                }

                addCustomParams(params)
            }
        }
    }
}

val handler = CoroutineExceptionHandler { _, exception ->
    Log.wtf("coroutine exception", exception)
}
