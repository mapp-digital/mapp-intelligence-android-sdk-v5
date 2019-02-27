package webtrekk.android.sdk.domain

import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.util.generateEverId

internal class Sessions(private val webtrekkSharedPrefs: WebtrekkSharedPrefs) {

    // if first time, generate the ever id alongside setting one = 1 as it's app first start
    fun setEverId() {
        if (!webtrekkSharedPrefs.contains(WebtrekkSharedPrefs.EVER_ID_KEY)) {
            webtrekkSharedPrefs.everId = generateEverId().also { webtrekkSharedPrefs.one = "1" }
        }
    }

    fun getEverId(): String = webtrekkSharedPrefs.let {
        setEverId()

        return webtrekkSharedPrefs.everId
    }

    // after first getting app first start, set it to 0 forever
    fun getAppFirstStart(): String = webtrekkSharedPrefs.one.also { webtrekkSharedPrefs.one = "0" }

    fun startNewSession() {
        webtrekkSharedPrefs.fns = "1"
    }

    fun getCurrentSession(): String = webtrekkSharedPrefs.fns.also { webtrekkSharedPrefs.fns = "0" }

    fun optOut(value: Boolean) {
        webtrekkSharedPrefs.optOut = value
    }

    fun isOptOut(): Boolean {
        return webtrekkSharedPrefs.optOut
    }
}
