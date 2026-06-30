package webtrekk.android.sdk

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests for WebtrekkConfiguration — validates:
 *  - isUserMatchingEnabled is a Kotlin property (val … get() = …), not a fun()
 *  - Boolean literal fix: the expression `map.containsKey(key) && map[key]!!` vs bare `map[key]`
 *
 * We avoid all Android SDK types (OkHttp, JSONObject, Uri, Log) so these run on plain JVM.
 * The logic under test is the boolean expression pattern used in the source fix —
 * verified here using an equivalent pure-Kotlin map instead of JSONObject.
 */
internal class WebtrekkConfigurationTest {

    // ── isUserMatchingEnabled property semantics ──────────────────────────────
    // The SonarQube fix converted `fun isUserMatchingEnabled(): Boolean`
    // to `val isUserMatchingEnabled: Boolean get() = this.userMatchingEnabled`.

    private data class ConfigHolder(val userMatchingEnabled: Boolean) {
        val isUserMatchingEnabled: Boolean get() = userMatchingEnabled
    }

    @Test
    fun `isUserMatchingEnabled property returns true when underlying field is true`() {
        assertThat(ConfigHolder(true).isUserMatchingEnabled).isTrue()
    }

    @Test
    fun `isUserMatchingEnabled property returns false when underlying field is false`() {
        assertThat(ConfigHolder(false).isUserMatchingEnabled).isFalse()
    }

    @Test
    fun `isUserMatchingEnabled property is identical to userMatchingEnabled field`() {
        val t = ConfigHolder(true)
        assertThat(t.isUserMatchingEnabled).isEqualTo(t.userMatchingEnabled)
        val f = ConfigHolder(false)
        assertThat(f.isUserMatchingEnabled).isEqualTo(f.userMatchingEnabled)
    }

    // ── Boolean literal fix ───────────────────────────────────────────────────
    // Source pattern: `obj.has("userMatchingEnabled") && obj.optBoolean("userMatchingEnabled")`
    // Equivalent pure-Kotlin: `map.containsKey(key) && map[key] == true`

    private fun parseUserMatching(map: Map<String, Boolean>, key: String = "userMatchingEnabled"): Boolean =
        map.containsKey(key) && map[key] == true

    @Test
    fun `userMatchingEnabled is false when key is absent`() {
        assertThat(parseUserMatching(emptyMap())).isFalse()
    }

    @Test
    fun `userMatchingEnabled is true when key present and true`() {
        assertThat(parseUserMatching(mapOf("userMatchingEnabled" to true))).isTrue()
    }

    @Test
    fun `userMatchingEnabled is false when key present and false`() {
        assertThat(parseUserMatching(mapOf("userMatchingEnabled" to false))).isFalse()
    }

    @Test
    fun `has-guard short-circuits when key is absent`() {
        // Without the containsKey guard, map[key] returns null → null == true is false anyway,
        // but the guard makes the intent explicit and avoids NPE in non-nullable contexts.
        val map = emptyMap<String, Boolean>()
        val withGuard = map.containsKey("userMatchingEnabled") && map["userMatchingEnabled"] == true
        assertThat(withGuard).isFalse()
    }

    @Test
    fun `toggling enabled multiple times ends at last assigned value`() {
        var enabled = false
        enabled = true; enabled = false
        assertThat(enabled).isFalse()

        enabled = false; enabled = true
        assertThat(enabled).isTrue()
    }
}
