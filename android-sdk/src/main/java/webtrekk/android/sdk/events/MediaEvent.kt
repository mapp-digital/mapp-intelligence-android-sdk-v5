package webtrekk.android.sdk.events

import webtrekk.android.sdk.events.eventParams.BaseEvent
import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.EventParameters
import webtrekk.android.sdk.events.eventParams.MediaParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */

data class MediaEvent(val pageName: String, var parameters: MediaParameters) : BaseEvent {
    var eventParameters: EventParameters? = null
    var sessionParameters: SessionParameters? = null
    var eCommerceParameters: ECommerceParameters? = null
    val customParameters = mutableMapOf<String, String>()
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        map.putAll(customParameters)
        map.putAll(parameters.toHasMap())
        sessionParameters?.let { map.putAll(it.toHasMap()) }
        eventParameters?.let { map.putAll(it.toHasMap()) }
        eCommerceParameters?.let { map.putAll(it.toHasMap()) }
        return map
    }
}