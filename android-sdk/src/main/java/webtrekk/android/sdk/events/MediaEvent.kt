package webtrekk.android.sdk.events

import webtrekk.android.sdk.events.eventParams.BaseEvent
import webtrekk.android.sdk.events.eventParams.MediaParameters
import webtrekk.android.sdk.events.eventParams.EventParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters
import webtrekk.android.sdk.events.eventParams.ECommerceParameters

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class MediaEvent(val name: String, var mediaParameters: MediaParameters) : BaseEvent {
    var eventParameters: EventParameters = EventParameters()
    var sessionParameters: SessionParameters = SessionParameters()
    var eCommerceParameters: ECommerceParameters = ECommerceParameters()
    val customParameters = mutableMapOf<String, String>()
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        map.putAll(customParameters)
        map.putAll(mediaParameters.toHasMap())
        map.putAll(sessionParameters.toHasMap())
        map.putAll(eventParameters.toHasMap())
        map.putAll(eCommerceParameters.toHasMap())
        return map
    }
}