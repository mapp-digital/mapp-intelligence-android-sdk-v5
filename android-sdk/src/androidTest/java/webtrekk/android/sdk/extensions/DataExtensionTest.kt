package webtrekk.android.sdk.extensions

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.ActiveConfig
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.customTrackRequest
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.exceptionRequest
import webtrekk.android.sdk.extension.buildUrl
import webtrekk.android.sdk.extension.toCustomParams
import webtrekk.android.sdk.trackRequest
import webtrekk.android.sdk.webtrekkConfigurationBuilder

class DataExtensionTest {
    private lateinit var activeConfig: ActiveConfig
    private lateinit var config: Config
    private lateinit var context: Context
    private lateinit var webtrekk: Webtrekk

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        config = webtrekkConfigurationBuilder.build()
        Webtrekk.getInstance().init(context, config)
        webtrekk = spyk(Webtrekk.getInstance())
        activeConfig = webtrekk.getCurrentConfiguration()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_simple_url_tracking_request() {
        val dataTrack = DataTrack(trackRequest, emptyList())
        val url = dataTrack.buildUrl(activeConfig)
        Log.d("SimpleUrlTracking URL", url)
        val uri = Uri.parse(url)
        Truth.assertThat(url).isNotNull()
        Truth.assertThat(uri.getQueryParameter("p")?.split(",")).hasSize(10)
        Truth.assertThat(uri.getQueryParameter("ct")).isNull()
        Truth.assertThat(uri.getQueryParameter("la")).isEqualTo("English")
        Truth.assertThat(uri.getQueryParameter("eid")).isNotNull()
        Truth.assertThat(uri.getQueryParameter("X-WT-UA")).contains(trackRequest.webtrekkVersion)
        Truth.assertThat(uri.getQueryParameter("cs801")).isEqualTo(trackRequest.webtrekkVersion)
    }

    @Test
    fun test_custom_page_request() {
        val dataTrack = DataTrack(
            customTrackRequest.trackRequest, customTrackRequest.trackingParams.toCustomParams(
                customTrackRequest.trackRequest.id
            )
        )

        val url = dataTrack.buildUrl(activeConfig)
        val uri = Uri.parse(url)
        Log.d("CustomPageTracking URL", url)

        Truth.assertThat(uri.getQueryParameter(UrlParams.EVENT_NAME)).isEqualTo("Page 1")
    }

    @Test
    fun test_exception_tracking_request() {
        val dataTrack = DataTrack(
            exceptionRequest.trackRequest,
            mapOf("ct" to "webtrekk_ignore").toCustomParams(
                exceptionRequest.trackRequest.id
            ),
        )

        val url = dataTrack.buildUrl(activeConfig)
        val uri = Uri.parse(url)
        Log.d("ExceptionTracking URL", url)

        Truth.assertThat(uri.getQueryParameter(UrlParams.EVENT_NAME)).isEqualTo("webtrekk_ignore")
    }
}