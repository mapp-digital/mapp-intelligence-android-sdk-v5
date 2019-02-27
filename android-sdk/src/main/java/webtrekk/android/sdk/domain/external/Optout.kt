package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.*
import webtrekk.android.sdk.AppState
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.Scheduler
import webtrekk.android.sdk.domain.Sessions
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler

internal class Optout(
    coroutineDispatchers: CoroutineDispatchers,
    private val sessions: Sessions,
    private val scheduler: Scheduler,
    private val appState: AppState<TrackRequest>,
    private val clearTrackRequests: ClearTrackRequests
) : ExternalInteractor<Optout.Params> {

    private val _job = Job()
    override val scope = CoroutineScope(coroutineDispatchers.ioDispatcher + _job)

    override fun invoke(invokeParams: Params) {
        sessions.optOut(invokeParams.optOutValue)

        if (invokeParams.optOutValue) {
            appState.disableAutoTrack(invokeParams.context)
            scheduler.cancelSendRequests()

            scope.launch(context = coroutineExceptionHandler, start = CoroutineStart.ATOMIC) {
                clearTrackRequests(ClearTrackRequests.Params(trackRequests = emptyList()))
                    .onSuccess { logDebug("Cleared all track requests") }
                    .onFailure { logError("Failed to clear the track requests while opting out") }
            }
        }
    }

    fun isActive(): Boolean = sessions.isOptOut()

    data class Params(val context: Context, val optOutValue: Boolean)
}
