package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageParameters(var details: Map<Int, String> = emptyMap()) : BaseEvent {
    var internalSearch: String = ""
    var groups: Map<Int, String> = emptyMap()
    override suspend fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        return map
    }
}