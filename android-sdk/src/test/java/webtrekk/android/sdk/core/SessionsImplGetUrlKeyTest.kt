package webtrekk.android.sdk.core

import com.google.common.truth.Truth.assertThat
import android.net.Uri
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.CampaignParam
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.dao.TrackRequestDao
import webtrekk.android.sdk.module.AppModule

/**
 * Tests for SessionsImpl.getUrlKey() and the extracted parseUrlParam() helper.
 *
 * Validates:
 *  - String.toUri() KTX replaces Uri.parse() with identical semantics
 *  - Merged nested if (type != null && key == type) works correctly
 *  - Early return when urlString is blank
 *  - Campaign params are extracted correctly
 *  - SharedPrefs is cleared after parsing
 */
internal class SessionsImplGetUrlKeyTest {

    private lateinit var sharedPrefs: WebtrekkSharedPrefs
    private lateinit var trackRequestDao: TrackRequestDao
    private lateinit var sessions: SessionsImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sharedPrefs = mockk(relaxUnitFun = true)
        trackRequestDao = mockk(relaxUnitFun = true)
        mockkObject(AppModule)
        every { AppModule.logger } returns mockk(relaxed = true)
        every { sharedPrefs.anonymousTracking } returns false

        // Uri.parse() is an Android framework call — mock it so tests run on JVM
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } answers {
            val urlStr = firstArg<String>()
            buildMockUri(urlStr)
        }

        sessions = SessionsImpl(sharedPrefs, trackRequestDao, dispatcher)
    }

    @After
    fun tearDown() = unmockkAll()

    /**
     * Build a mock [Uri] whose [Uri.getQueryParameterNames] and [Uri.getQueryParameter]
     * reflect the actual query string in [urlStr], parsed via java.net.URI (pure JVM).
     */
    private fun buildMockUri(urlStr: String): Uri {
        val mockUri = mockk<Uri>(relaxed = true)
        try {
            val javaUri = java.net.URI(urlStr)
            val query = javaUri.rawQuery ?: ""
            val params: Map<String, String> = if (query.isBlank()) emptyMap() else {
                query.split("&").mapNotNull { pair ->
                    val idx = pair.indexOf('=')
                    if (idx < 0) null
                    else pair.substring(0, idx) to java.net.URLDecoder.decode(
                        pair.substring(idx + 1), "UTF-8"
                    )
                }.toMap()
            }
            every { mockUri.queryParameterNames } returns params.keys.toMutableSet()
            every { mockUri.getQueryParameter(any()) } answers {
                params[firstArg<String>()]
            }
        } catch (_: Exception) {
            every { mockUri.queryParameterNames } returns mutableSetOf()
            every { mockUri.getQueryParameter(any()) } returns null
        }
        return mockUri
    }

    @Test
    fun `getUrlKey returns empty map when saveUrlData is blank`() {
        every { sharedPrefs.saveUrlData } returns ""
        assertThat(sessions.getUrlKey()).isEmpty()
    }

    @Test
    fun `getUrlKey returns empty map when saveUrlData is whitespace`() {
        every { sharedPrefs.saveUrlData } returns "   "
        assertThat(sessions.getUrlKey()).isEmpty()
    }

    @Test
    fun `getUrlKey clears saveUrlData after parsing`() {
        every { sharedPrefs.saveUrlData } returns "https://example.com?cc_key=val"
        var saved = "https://example.com?cc_key=val"
        every { sharedPrefs.saveUrlData } answers { saved }
        every { sharedPrefs.saveUrlData = any() } answers { saved = firstArg() }

        sessions.getUrlKey()

        verify { sharedPrefs.saveUrlData = "" }
    }

    @Test
    fun `getUrlKey extracts campaign param starting with cc`() {
        val url = "https://example.com?${CampaignParam.CAMPAIGN_PARAM}mykey=myval"
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        assertThat(result).containsKey("${CampaignParam.CAMPAIGN_PARAM}mykey")
    }

    @Test
    fun `getUrlKey renames WT_CC campaign param to CC prefix`() {
        val url = "https://example.com?${CampaignParam.CAMPAIGN_PARAM_WT_CC}key=value"
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        // WT_CC key should be replaced with CAMPAIGN_PARAM prefix
        val newKey = "${CampaignParam.CAMPAIGN_PARAM}key"
        assertThat(result).containsKey(newKey)
        assertThat(result).doesNotContainKey("${CampaignParam.CAMPAIGN_PARAM_WT_CC}key")
    }

    @Test
    fun `getUrlKey sets MEDIA_CODE_PARAM_EXCHANGER when type param matches key`() {
        val type = "mc_type"
        val value = "encoded_value"
        val url = "https://example.com?webtrekk_type_param=$type&$type=$value"
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        assertThat(result).containsKey(InternalParam.MEDIA_CODE_PARAM_EXCHANGER)
    }

    @Test
    fun `getUrlKey does not set MEDIA_CODE_PARAM_EXCHANGER when type is null`() {
        // No webtrekk_type_param in URL
        val url = "https://example.com?${CampaignParam.CAMPAIGN_PARAM}key=val"
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        assertThat(result).doesNotContainKey(InternalParam.MEDIA_CODE_PARAM_EXCHANGER)
    }

    @Test
    fun `getUrlKey does not set MEDIA_CODE_PARAM_EXCHANGER when key does not match type`() {
        val type = "mc_type"
        val url = "https://example.com?webtrekk_type_param=$type&other_key=val"
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        assertThat(result).doesNotContainKey(InternalParam.MEDIA_CODE_PARAM_EXCHANGER)
    }

    @Test
    fun `getUrlKey skips blank param values`() {
        val url = "https://example.com?${CampaignParam.CAMPAIGN_PARAM}key="
        every { sharedPrefs.saveUrlData } returns url
        every { sharedPrefs.saveUrlData = any() } returns Unit

        val result = sessions.getUrlKey()
        assertThat(result).doesNotContainKey("${CampaignParam.CAMPAIGN_PARAM}key")
    }
}
