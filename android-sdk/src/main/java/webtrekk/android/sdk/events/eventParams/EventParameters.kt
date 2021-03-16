package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.EventParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class EventParameters(
    var customParameters: Map<Int, String> = emptyMap()
) : BaseEvent {
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!customParameters.isNullOrEmpty()) {
            customParameters.forEach { (key, value) ->
                map.addNotNull("${EventParam.EVENT_PARAM}$key", value)
            }
        }
        return map
    }
}