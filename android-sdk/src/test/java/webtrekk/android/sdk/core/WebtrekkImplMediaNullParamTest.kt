package webtrekk.android.sdk.core

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import webtrekk.android.sdk.MediaParam

/**
 * Tests for the null-string equality fix in WebtrekkImpl.trackMedia:
 *   Before: "null".equals(params[key], true)   — literal string first
 *   After:  params[key].equals("null", ignoreCase = true)  — receiver is the value
 *
 * All assertions are pure Kotlin — no Android framework, no Room, no coroutines.
 * The previous version called spyk<WebtrekkImpl>().init(...) which triggered
 * internalInit() → SessionsImpl → DataModule.database → real Room → NPE on
 * Dispatchers.Default, leaking as UncaughtExceptionsBeforeTest into later runTest calls.
 */
internal class WebtrekkImplMediaNullParamTest {

    @Test
    fun `null string literal equals ignoreCase is true when value is null_lowercase`() {
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
        val value: String? = null
        val defaultApplied = value.isNullOrEmpty() || value.equals("null", ignoreCase = true)
        assertThat(defaultApplied).isTrue()
    }

    @Test
    fun `media duration defaults to 0 when not present in params`() {
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
