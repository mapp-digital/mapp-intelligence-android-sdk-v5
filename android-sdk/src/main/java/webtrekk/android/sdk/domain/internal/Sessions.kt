package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.SharedPrefs
import webtrekk.android.sdk.util.generateEverId

internal class Sessions(private val sharedPrefs: SharedPrefs) {

    fun setEverId() {
        if (!sharedPrefs.contains(sharedPrefs.EVER_ID_KEY)) {
            sharedPrefs.everId = generateEverId().also { sharedPrefs.one = "1" }
        }
    }

    fun getEverId(): String = sharedPrefs?.let {
        setEverId()

        return sharedPrefs.everId
    }

    fun getAppFirstStart(): String = sharedPrefs.one.also { sharedPrefs.one = "0" }

    fun startNewSession() {
        sharedPrefs.fns = "1"
    }

    fun getCurrentSession(): String = sharedPrefs.fns.also { sharedPrefs.fns = "0" }
}
