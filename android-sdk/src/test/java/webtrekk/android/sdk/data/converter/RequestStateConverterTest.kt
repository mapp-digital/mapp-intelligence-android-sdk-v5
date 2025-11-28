package webtrekk.android.sdk.data.converter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import webtrekk.android.sdk.data.entity.TrackRequest

internal class RequestStateConverterTest {

    private val converter = RequestStateConverter()

    @Test
    fun `convert request state to string and back`() {
        TrackRequest.RequestState.values().forEach { state ->
            val value = converter.requestStateToString(state)
            assertThat(converter.stringToRequestState(value)).isEqualTo(state)
        }
    }
}
