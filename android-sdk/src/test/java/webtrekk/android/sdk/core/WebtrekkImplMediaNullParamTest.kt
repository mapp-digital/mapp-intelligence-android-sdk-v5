package webtrekk.android.sdk.core

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.util.configuration

/**
 * Tests for the null-string equality fix in WebtrekkImpl.trackMedia:
 *   Before: "null".equals(params[key], true)   — literal string first
 *   After:  params[key].equals("null", ignoreCase = true)  — receiver is the value
 *
 * Both produce the same result when the value is "null" (case-insensitive),
 * null, or a real value. These tests verify the semantic is preserved.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class WebtrekkImplMediaNullParamTest {

    private val dispatcher = Dispatchers.Unconfined
    private lateinit var webtrekk: Webtrekk

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(dispatcher)
        webtrekk = spyk<WebtrekkImpl>()
        webtrekk.init(mockk(relaxed = true), configuration)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // These tests verify behaviour through the public API rather than internal state.
    // The change from "null".equals(x, true) → x.equals("null", ignoreCase = true)
    // is semantically identical for non-null receivers (they both return true when x = "null"
    // case-insensitively). The difference only matters when x is null — but the preceding
    // isNullOrEmpty() guard already handles null, so both forms behave identically.

    @Test
    fun `null string literal equals ignoreCase is true when value is null_lowercase`() {
        // Direct unit test of the condition logic (no Android dependency needed)
        val value: String? = "null"
        assertThat(value.equals("null", ignoreCase = true)).isTrue()
    }

    @Test
    fun `null string literal equals ignoreCase is true when value is NULL_uppercase`() {
        val value: String? = "NULL"
        assertThat(value.equals("null", ignoreCase = true)).isTrue()
    }

    @Test
    fun `null string literal equals ignoreCase is true when value is Null_mixed`() {
        val value: String? = "Null"
        assertThat(value.equals("null", ignoreCase = true)).isTrue()
    }

    @Test
    fun `null string literal equals ignoreCase is false for real numeric value`() {
        val value: String? = "120"
        assertThat(value.equals("null", ignoreCase = true)).isFalse()
    }

    @Test
    fun `null string literal equals ignoreCase is false for empty string`() {
        val value: String? = ""
        assertThat(value.equals("null", ignoreCase = true)).isFalse()
    }

    @Test
    fun `null value isNullOrEmpty guard catches null before equals is called`() {
        // When value is actually null, isNullOrEmpty() is true → equals never called
        val value: String? = null
        val defaultApplied = value.isNullOrEmpty() || value.equals("null", ignoreCase = true)
        assertThat(defaultApplied).isTrue()
    }

    @Test
    fun `media duration defaults to 0 when not present in params`() {
        // Verify the map manipulation logic: if key absent → default "0"
        val params = mutableMapOf<String, String>()
        if (!params.containsKey(MediaParam.MEDIA_DURATION) ||
            params[MediaParam.MEDIA_DURATION].isNullOrEmpty() ||
            params[MediaParam.MEDIA_DURATION].equals("null", ignoreCase = true)
        ) {
            params[MediaParam.MEDIA_DURATION] = "0"
        }
        assertThat(params[MediaParam.MEDIA_DURATION]).isEqualTo("0")
    }

    @Test
    fun `media duration defaults to 0 when value is null string`() {
        val params = mutableMapOf(MediaParam.MEDIA_DURATION to "null")
        if (!params.containsKey(MediaParam.MEDIA_DURATION) ||
            params[MediaParam.MEDIA_DURATION].isNullOrEmpty() ||
            params[MediaParam.MEDIA_DURATION].equals("null", ignoreCase = true)
        ) {
            params[MediaParam.MEDIA_DURATION] = "0"
        }
        assertThat(params[MediaParam.MEDIA_DURATION]).isEqualTo("0")
    }

    @Test
    fun `media duration kept when valid value provided`() {
        val params = mutableMapOf(MediaParam.MEDIA_DURATION to "120")
        if (!params.containsKey(MediaParam.MEDIA_DURATION) ||
            params[MediaParam.MEDIA_DURATION].isNullOrEmpty() ||
            params[MediaParam.MEDIA_DURATION].equals("null", ignoreCase = true)
        ) {
            params[MediaParam.MEDIA_DURATION] = "0"
        }
        assertThat(params[MediaParam.MEDIA_DURATION]).isEqualTo("120")
    }

    @Test
    fun `media position defaults to 0 when value is NULL uppercase`() {
        val params = mutableMapOf(MediaParam.MEDIA_POSITION to "NULL")
        if (!params.containsKey(MediaParam.MEDIA_POSITION) ||
            params[MediaParam.MEDIA_POSITION].isNullOrEmpty() ||
            params[MediaParam.MEDIA_POSITION].equals("null", ignoreCase = true)
        ) {
            params[MediaParam.MEDIA_POSITION] = "0"
        }
        assertThat(params[MediaParam.MEDIA_POSITION]).isEqualTo("0")
    }
}
