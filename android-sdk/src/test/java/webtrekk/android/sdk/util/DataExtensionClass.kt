package webtrekk.android.sdk.util

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class DataExtensionClass {

    @Test
    fun testWebtrekkAppVersionSplittin() {
        val version = "5.1.4.1"

        val trimmedVersion = version.split(".")
            .subList(0, 3)
            .joinToString(separator = "")
            .substring(0, 3)

        Assertions.assertEquals(trimmedVersion, "514")
    }
}