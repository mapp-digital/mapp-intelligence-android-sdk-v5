package webtrekk.android.sdk.events.eventParams

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import webtrekk.android.sdk.MediaParam

/**
 * Tests for MediaParameters.toHasMap() — validates fun interface Code and
 * that commented-out code removal didn't change the output.
 */
internal class MediaParametersTest {

    @Test
    fun `toHasMap contains media action`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 120.0
        )
        assertThat(params.toHasMap()[MediaParam.MEDIA_ACTION]).isEqualTo("play")
    }

    @Test
    fun `toHasMap contains position and duration`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PAUSE.code(),
            position = 30.0,
            duration = 120.0
        )
        val map = params.toHasMap()
        assertThat(map[MediaParam.MEDIA_POSITION]).isNotNull()
        assertThat(map[MediaParam.MEDIA_DURATION]).isNotNull()
    }

    @Test
    fun `toHasMap does not contain NAME param (removed commented code)`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 60.0
        )
        // The old commented-out line was: map.addNotNull(MediaParam.NAME, name)
        // After removal, NAME should not appear as a mapped key
        val map = params.toHasMap()
        assertThat(map.containsKey(MediaParam.NAME)).isFalse()
    }

    @Test
    fun `toHasMap includes mute when soundIsMuted is set`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 60.0
        ).apply { soundIsMuted = true }
        assertThat(params.toHasMap()[MediaParam.MUTE]).isEqualTo("1")
    }

    @Test
    fun `toHasMap mute is 0 when sound is not muted`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 60.0
        ).apply { soundIsMuted = false }
        assertThat(params.toHasMap()[MediaParam.MUTE]).isEqualTo("0")
    }

    @Test
    fun `toHasMap omits mute when soundIsMuted is null`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 60.0
        )
        assertThat(params.toHasMap().containsKey(MediaParam.MUTE)).isFalse()
    }

    @Test
    fun `toHasMap includes custom categories`() {
        val params = MediaParameters(
            name = "video1",
            action = MediaParameters.Action.PLAY.code(),
            position = 0.0,
            duration = 60.0
        ).apply { customCategories = mapOf(1 to "sports") }
        assertThat(params.toHasMap()["${MediaParam.MEDIA_CATEGORY}1"]).isEqualTo("sports")
    }

    @Test
    fun `Code fun interface implementation returns correct code`() {
        // Validate the fun interface Code can be implemented as a lambda
        val code: MediaParameters.Code = MediaParameters.Code { "test_code" }
        assertThat(code.code()).isEqualTo("test_code")
    }

    @Test
    fun `Action enum implements Code and returns expected code string`() {
        assertThat(MediaParameters.Action.PLAY.code()).isEqualTo("play")
        assertThat(MediaParameters.Action.PAUSE.code()).isEqualTo("pause")
        assertThat(MediaParameters.Action.STOP.code()).isEqualTo("stop")
        assertThat(MediaParameters.Action.INIT.code()).isEqualTo("init")
    }
}
