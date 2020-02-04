package webtrekk.android.sdk

/**
 * Created by Aleksandar Marinkovic on 2020-02-04.
 * Copyright (c) 2020 MAPP.
 *
 * @param formName form name can be activity name or changed
 * @param trackingIds only track specific elements of the view
 * @param renameFields rename specific fields
 * @param confirmButton clicked confirm or cancel button for the form
 * @param anonymous hide content
 * @param changeFieldsValue in some case is good to change value of the specific fields
 */

data class FormTrackingSettings(
    val formName: String = "",
    val trackingIds: List<Int> = emptyList(),
    val renameFields: Map<Int, String> = emptyMap(),
    val changeFieldsValue: Map<Int, String> = emptyMap(),
    val confirmButton: Boolean = true,
    val anonymous: Boolean = false
)