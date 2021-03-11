package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageParameters(var details: Map<Int, String> = emptyMap()) {
    var internalSearch: String = ""
    var groups: Map<Int, String> = emptyMap()
}