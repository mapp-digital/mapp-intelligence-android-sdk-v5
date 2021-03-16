package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.SessionParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class SessionParameters(
    var parameters: Map<Int, String> = emptyMap()
) : BaseEvent {
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!parameters.isNullOrEmpty()) {
            parameters.forEach { (key, value) ->
                map.addNotNull("${SessionParam.SESSION_PARAM}$key", value)
            }
        }
        return map
    }
}