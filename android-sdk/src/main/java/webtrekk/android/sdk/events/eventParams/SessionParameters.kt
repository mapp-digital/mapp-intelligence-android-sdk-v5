package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class SessionParameters(
    var parameters: Map<Int, String> = emptyMap()
) : BaseEvent {
    override suspend fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        return map
    }
}