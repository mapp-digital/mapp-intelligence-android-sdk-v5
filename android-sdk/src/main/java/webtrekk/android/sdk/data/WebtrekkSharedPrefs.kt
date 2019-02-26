package webtrekk.android.sdk.data

import android.content.Context

internal const val SHARED_PREFS_NAME = "webtrekk_sharedPref"

internal class WebtrekkSharedPrefs(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    var everId: String
        inline get() = sharedPreferences.getString(EVER_ID_KEY, "") ?: ""
        set(value) = sharedPreferences.edit().putString(EVER_ID_KEY, value).apply()

    var one: String
        inline get() = sharedPreferences.getString(ONE_KEY, "0") ?: "0"
        set(value) = sharedPreferences.edit().putString(ONE_KEY, value).apply()

    var fns: String
        inline get() = sharedPreferences.getString(NEW_SESSION_KEY, "0") ?: "0"
        set(value) = sharedPreferences.edit().putString(NEW_SESSION_KEY, value).apply()

    var optOut: Boolean
        inline get() = sharedPreferences.getBoolean(USER_OPT_OUT, false)
        set(value) = sharedPreferences.edit().putBoolean(USER_OPT_OUT, value).apply()

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    companion object {

        const val EVER_ID_KEY = "everId"
        const val ONE_KEY = "one"
        const val NEW_SESSION_KEY = "fns"
        const val USER_OPT_OUT = "optOut"
    }
}
