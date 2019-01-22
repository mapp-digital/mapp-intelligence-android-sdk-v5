package webtrekk.android.sdk

import android.content.Context

abstract class Webtrekk protected constructor() {

    companion object {

        @JvmStatic
        fun getInstance(): Webtrekk {
            return WebtrekkImpl.getInstance()
        }
    }

    abstract fun init(context: Context, config: Config)

    // we can ignore for now
    abstract fun track(context: Context, customPageName: String? = null)

    abstract fun setCustomPageName(context: Context, customName: String)

    // every request might have params, replacment for manual tracking
    // todo make trackingParams optional
    abstract fun trackPage(pageName: String, trackingParams: Map<String, String>)

    // we may use event name or latest activity name or latest page name
    // only difference between page and event is event has CT
    abstract fun trackEvent(eventName: String, trackingParams: Map<String, String>)

    // if opt out, clear everything
    abstract fun optOut() // more info

    // fns = 1 at every time the app starts
    // one = 1 if generated ever ID
    // investigate if the user comes from app fresh install by SDK or later without SDK, if so then send ecommerece param
    abstract fun getEverId(): String

    abstract fun clear() // maybe
}
