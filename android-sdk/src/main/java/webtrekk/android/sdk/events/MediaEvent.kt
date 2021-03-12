package webtrekk.android.sdk.events

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import webtrekk.android.sdk.events.eventParams.BaseEvent
import webtrekk.android.sdk.events.eventParams.MediaParameters
import webtrekk.android.sdk.events.eventParams.EventParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters
import webtrekk.android.sdk.events.eventParams.ECommerceParameters

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class MediaEvent(val name: String) : BaseEvent {
    var mediaParameters: MediaParameters = MediaParameters()
    var eventParameters: EventParameters = EventParameters()
    var sessionParameters: SessionParameters = SessionParameters()
    var eCommerceParameters: ECommerceParameters = ECommerceParameters()
    val customParameters = mutableMapOf<String, String>()
    override suspend fun toHasMap(): MutableMap<String, String> =
        coroutineScope {
            val map = mutableMapOf<String, String>()
            val map1 = async { mediaParameters.toHasMap() }
            val map2 = async { sessionParameters.toHasMap() }
            val map3 = async { eventParameters.toHasMap() }
            val map4 = async { eCommerceParameters.toHasMap() }
            map.putAll(customParameters)
            map.putAll(map1.await())
            map.putAll(map2.await())
            map.putAll(map3.await())
            map.putAll(map4.await())
            return@coroutineScope map
        }
}