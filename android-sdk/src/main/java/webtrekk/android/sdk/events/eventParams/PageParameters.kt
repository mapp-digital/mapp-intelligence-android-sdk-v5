package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.PageParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageParameters(var details: Map<Int, String> = emptyMap()) : BaseEvent {
    var internalSearch: String = ""
    var category: Map<Int, String> = emptyMap()
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!details.isNullOrEmpty()) {
            details.forEach { (key, value) ->
                map.addNotNull("${PageParam.PAGE_PARAM}$key", value)
            }
        }

        if (!category.isNullOrEmpty()) {
            category.forEach { (key, value) ->
                map.addNotNull("${PageParam.PAGE_CATEGORY}$key", value)
            }
        }
        map.addNotNull(PageParam.INTERNAL_SEARCH, internalSearch)
        return map
    }
}