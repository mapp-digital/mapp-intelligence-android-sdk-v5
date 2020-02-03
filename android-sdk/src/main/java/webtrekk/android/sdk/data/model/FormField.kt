package webtrekk.android.sdk.data.model

import webtrekk.android.sdk.extension.toInt

/**
 * Created by Aleksandar Marinkovic on 2020-01-31.
 * Copyright (c) 2020 MAPP.
 */
data class FormField(
    var id: Int = -1,
    var name: String = "0",
    var fieldValue: String = "empty",
    var fieldType: String = "view",
    var lastFocus: Boolean = false
) {
    fun toRequest(): String {
        return "$name.$fieldType|$fieldValue|${lastFocus.toInt()}"
    }
}