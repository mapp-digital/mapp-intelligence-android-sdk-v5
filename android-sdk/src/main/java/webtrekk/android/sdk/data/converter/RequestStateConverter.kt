package webtrekk.android.sdk.data.converter

import androidx.room.TypeConverter
import webtrekk.android.sdk.data.entity.TrackRequest

internal class RequestStateConverter {

    @TypeConverter
    fun requestStateToInt(requestState: TrackRequest.RequestState): String = requestState.value

    @TypeConverter
    fun intToRequestState(value: String): TrackRequest.RequestState = TrackRequest.RequestState.valueOf(value)
}
