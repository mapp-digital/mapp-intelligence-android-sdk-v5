package webtrekk.android.sdk.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DataExtensionClass {

    @Test
    fun testWebtrekkAppVersionSplitting() {
        val version = "5.1.4.1"

        val trimmedVersion = version.split(".")
            .subList(0, 3)
            .joinToString(separator = "")
            .substring(0, 3)

        assertThat(trimmedVersion).isEqualTo("514")
    }
}
