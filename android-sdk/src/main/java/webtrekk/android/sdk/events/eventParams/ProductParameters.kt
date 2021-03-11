package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class ProductParameters(
    var name: String = ""
) {
    var categories: Map<Int, String> = emptyMap()
    var cost: Number? = null
    var quantity: String? = null
}