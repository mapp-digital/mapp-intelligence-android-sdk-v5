package webtrekk.android.sdk.domain.external

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.api.UrlParams
import java.io.File

/**
 * Tests for TrackUncaughtException — validates the extracted readExceptionParams() helper
 * and the refactored createListParamsFromFile() produce the same result as the original.
 *
 * These tests exercise the file-parsing logic through createListParamsFromFile by
 * writing well-formed exception files and verifying the parsed output.
 */
internal class TrackUncaughtExceptionLogicTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    // ── constants mirrored from CrashTrackingUtil ────────────────────────────
    private val START = "wte_start"
    private val END = "wte_end"
    private val SEP = "wte_item"

    private fun writeExceptionFile(
        crashName: String = "java.lang.NullPointerException",
        crashMessage: String = "null ref",
        causeMessage: String = "",
        crashStack: String = "at Foo.bar(Foo.kt:42)",
        causeStack: String = ""
    ): File {
        val file = tmpFolder.newFile("exception.txt")
        file.writeText(buildString {
            appendLine(START)
            appendLine(crashName)
            appendLine(SEP)
            appendLine(crashMessage)
            appendLine(SEP)
            appendLine(causeMessage)
            appendLine(SEP)
            appendLine(crashStack)
            appendLine(SEP)
            appendLine(causeStack)
            appendLine(SEP)
            appendLine(END)
        })
        return file
    }

    @Test
    fun `readExceptionParams via file sets CRASH_TYPE to UNCAUGHT`() {
        // We test behaviour indirectly via a well-formed file
        // TrackUncaughtException is internal; use reflection to invoke createListParamsFromFile
        val file = writeExceptionFile()
        val instance = makeInstance()
        val result = invokeCreateList(instance, file)

        assertThat(result).hasSize(1)
        assertThat(result[0][UrlParams.CRASH_TYPE]).isEqualTo(ExceptionType.UNCAUGHT.type)
    }

    @Test
    fun `parsed params contain crash name and message`() {
        val file = writeExceptionFile(
            crashName = "java.lang.NullPointerException",
            crashMessage = "null ref"
        )
        val result = invokeCreateList(makeInstance(), file)

        assertThat(result[0][UrlParams.CRASH_NAME]).isEqualTo("java.lang.NullPointerException")
        assertThat(result[0][UrlParams.CRASH_MESSAGE]).isEqualTo("null ref")
    }

    @Test
    fun `parsed params omit empty crash message`() {
        val file = writeExceptionFile(crashMessage = "")
        val result = invokeCreateList(makeInstance(), file)

        assertThat(result[0].containsKey(UrlParams.CRASH_MESSAGE)).isFalse()
    }

    @Test
    fun `parsed params contain cause message when present`() {
        val file = writeExceptionFile(causeMessage = "root cause")
        val result = invokeCreateList(makeInstance(), file)

        assertThat(result[0][UrlParams.CRASH_CAUSE_MESSAGE]).isEqualTo("root cause")
    }

    @Test
    fun `parsed params omit cause message when empty`() {
        val file = writeExceptionFile(causeMessage = "")
        val result = invokeCreateList(makeInstance(), file)

        assertThat(result[0].containsKey(UrlParams.CRASH_CAUSE_MESSAGE)).isFalse()
    }

    @Test
    fun `parsed params contain stack trace`() {
        val file = writeExceptionFile(crashStack = "at Foo.bar(Foo.kt:10)")
        val result = invokeCreateList(makeInstance(), file)

        assertThat(result[0][UrlParams.CRASH_STACK]).isEqualTo("at Foo.bar(Foo.kt:10)")
    }

    @Test
    fun `file is deleted after parsing`() {
        val file = writeExceptionFile()
        invokeCreateList(makeInstance(), file)
        assertThat(file.exists()).isFalse()
    }

    @Test
    fun `multiple exception blocks parsed into multiple entries`() {
        val file = tmpFolder.newFile("multi.txt")
        file.writeText(buildString {
            repeat(3) { i ->
                appendLine(START)
                appendLine("ExClass$i")
                appendLine(SEP)
                appendLine("msg$i")
                appendLine(SEP)
                appendLine("")
                appendLine(SEP)
                appendLine("stackline$i")
                appendLine(SEP)
                appendLine("")
                appendLine(SEP)
                appendLine(END)
            }
        })
        val result = invokeCreateList(makeInstance(), file)
        assertThat(result).hasSize(3)
        assertThat(result[1][UrlParams.CRASH_NAME]).isEqualTo("ExClass1")
    }

    @Test
    fun `malformed file returns empty list and does not throw`() {
        val file = tmpFolder.newFile("bad.txt")
        file.writeText("NOT_A_VALID_START\nsome data\n")
        val result = invokeCreateList(makeInstance(), file)
        assertThat(result).isEmpty()
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun makeInstance(): TrackUncaughtException {
        io.mockk.mockkObject(webtrekk.android.sdk.module.AppModule)
        io.mockk.every { webtrekk.android.sdk.module.AppModule.logger } returns io.mockk.mockk(relaxed = true)
        val cacheInteractor = io.mockk.mockk<webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams>(relaxed = true)
        return TrackUncaughtException(
            coroutineContext = kotlinx.coroutines.Dispatchers.Unconfined,
            cacheTrackRequestWithCustomParams = cacheInteractor
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeCreateList(
        instance: TrackUncaughtException,
        file: File
    ): MutableList<MutableMap<String, String>> {
        val method = TrackUncaughtException::class.java.getDeclaredMethod(
            "createListParamsFromFile",
            File::class.java
        )
        method.isAccessible = true
        return method.invoke(instance, file) as MutableList<MutableMap<String, String>>
    }
}
