package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.PageParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageParameters @JvmOverloads
constructor(
    var parameters: Map<Int, String> = emptyMap(),
    var search: String = "",
    var pageCategory: Map<Int, String> = emptyMap()
) : BaseEvent {

    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!parameters.isNullOrEmpty()) {
            parameters.forEach { (key, value) ->
                map.addNotNull("${PageParam.PAGE_PARAM}$key", value)
            }
        }

        if (!pageCategory.isNullOrEmpty()) {
            pageCategory.forEach { (key, value) ->
                map.addNotNull("${PageParam.PAGE_CATEGORY}$key", value)
            }
        }
        map.addNotNull(PageParam.INTERNAL_SEARCH, search)
        return map
    }
}