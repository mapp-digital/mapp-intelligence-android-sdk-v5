package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class ProductParameters(
    var name: String = ""
) : BaseEvent {
    var categories: Map<Int, String> = emptyMap()
    var cost: Number? = null
    var quantity: String? = null
    override suspend fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        return map
    }
}