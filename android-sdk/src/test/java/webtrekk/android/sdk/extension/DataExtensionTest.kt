package webtrekk.android.sdk.extension

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import webtrekk.android.sdk.CampaignParam
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.data.entity.CustomParam

/**
 * Tests for DataExtension.buildCustomParams() — validates the merged if-statement
 * (media code param handling) produces identical behaviour to the original nested ifs.
 */
internal class DataExtensionTest {

    private fun customParam(key: String, value: String, id: Long = 1L) =
        CustomParam(trackId = id, paramKey = key, paramValue = value)

    @Test
    fun `buildCustomParams appends regular param`() {
        val params = listOf(customParam("cs", "value1"))
        val result = params.buildCustomParams()
        assertThat(result).contains("cs=value1")
    }

    @Test
    fun `buildCustomParams multiple params all appended`() {
        val params = listOf(
            customParam("cs", "v1"),
            customParam("cd", "v2")
        )
        val result = params.buildCustomParams()
        assertThat(result).contains("cs=v1")
        assertThat(result).contains("cd=v2")
    }

    @Test
    fun `buildCustomParams renames MEDIA_CODE_PARAM_EXCHANGER key to mc`() {
        val params = listOf(customParam(InternalParam.MEDIA_CODE_PARAM_EXCHANGER, "wt_mc=abc"))
        val result = params.buildCustomParams()
        // Key should be renamed to CampaignParam.MEDIA_CODE = "mc"
        assertThat(result).contains("mc=")
        assertThat(result).doesNotContain(InternalParam.MEDIA_CODE_PARAM_EXCHANGER)
    }

    @Test
    fun `buildCustomParams prefixes mc value with wt_mc= when no = sign present`() {
        val params = listOf(customParam(CampaignParam.MEDIA_CODE, "somevalue"))
        val result = params.buildCustomParams()
        // Value should be prefixed since it contains no "=" or encoded "="
        assertThat(result).contains("wt_mc")
    }

    @Test
    fun `buildCustomParams does not double-prefix mc value when = already present`() {
        val params = listOf(customParam(CampaignParam.MEDIA_CODE, "wt_mc=somevalue"))
        val result = params.buildCustomParams()
        // Already has "=", should not add another prefix
        val mcCount = result.split("wt_mc").size - 1
        assertThat(mcCount).isEqualTo(1)
    }

    @Test
    fun `buildCustomParams does not double-prefix mc value when %3D present`() {
        val params = listOf(customParam(CampaignParam.MEDIA_CODE, "wt_mc%3Dsomevalue"))
        val result = params.buildCustomParams()
        val mcCount = result.split("wt_mc").size - 1
        assertThat(mcCount).isEqualTo(1)
    }

    @Test
    fun `buildCustomParams does not double-prefix mc value when %253D present`() {
        val params = listOf(customParam(CampaignParam.MEDIA_CODE, "wt_mc%253Dsomevalue"))
        val result = params.buildCustomParams()
        val mcCount = result.split("wt_mc").size - 1
        assertThat(mcCount).isEqualTo(1)
    }

    @Test
    fun `buildCustomParams filters anonymous params when anonymous is true`() {
        val params = listOf(
            customParam("cs", "public"),
            customParam("secret", "hidden")
        )
        val result = params.buildCustomParams(anonymous = true, anonymousParam = setOf("secret"))
        assertThat(result).contains("cs=public")
        assertThat(result).doesNotContain("secret")
    }

    @Test
    fun `buildCustomParams includes all params when anonymous is false`() {
        val params = listOf(
            customParam("cs", "public"),
            customParam("secret", "hidden")
        )
        val result = params.buildCustomParams(anonymous = false, anonymousParam = setOf("secret"))
        assertThat(result).contains("cs=public")
        assertThat(result).contains("secret=hidden")
    }

    @Test
    fun `buildCustomParams returns empty string for empty list`() {
        assertThat(emptyList<CustomParam>().buildCustomParams()).isEmpty()
    }
}
