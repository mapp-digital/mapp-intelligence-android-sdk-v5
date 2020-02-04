/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.extension

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.ToggleButton
import android.widget.Switch
import android.widget.CheckBox
import android.widget.RatingBar

import webtrekk.android.sdk.data.model.FormField

internal fun List<View>.notTrackedView(trackingIds: List<Int>): List<View> {
    if (trackingIds.isNotEmpty()) {
        return this
    }
    val listOfViews = mutableListOf<View>()
    forEach { view ->
        if (trackingIds.contains(view.id))
            listOfViews.add(view)
    }
    return listOfViews
}

internal fun ViewGroup.parseView(array: MutableList<View>): MutableList<View> {
    val count: Int = this.childCount
    for (i in 0 until count) {
        val view: View = this.getChildAt(i)
        if (view is ViewGroup) view.parseView(array) else {
            array.add(view)
        }
    }
    return array
}

internal fun View.toFormField(
    name: String? = null,
    anonymous: Boolean = false,
    value: String? = null
): FormField {
    val formField = FormField()
    if (this.id != View.NO_ID) {
        formField.id = this.id
        formField.name = name ?: resources.getResourceEntryName(this.id)
    }
    formField.lastFocus = this.isFocused

    when (this) {
        is EditText -> {
            formField.fieldValue = if (this.text != null) this.text.toString() else "empty"
            if (formField.fieldValue.isEmpty()) {
                formField.fieldValue = "empty"
            }
            if (anonymous and (formField.fieldValue != "empty")) {
                formField.fieldValue = "filled_out"
            }
            formField.fieldType = this.getInputTypeString()
        }
        is SearchView -> {
            formField.fieldValue = if (this.query != null) this.query.toString() else "empty"
            if (formField.fieldValue.isEmpty()) {
                formField.fieldValue = "empty"
            }
            if (anonymous and (formField.fieldValue != "empty")) {
                formField.fieldValue = "filled_out"
            }
            formField.fieldType = "SearchView"
        }
        is RadioButton -> {
            formField.fieldValue = isChecked.toInt().toString()
            formField.fieldType = "RadioButton"
        }
        is ToggleButton -> {
            formField.fieldValue = isChecked.toInt().toString()
            formField.fieldType = "ToggleButton"
        }
        is Switch -> {
            formField.fieldValue = isChecked.toInt().toString()
            formField.fieldType = "Switch"
        }
        is CheckBox -> {
            formField.fieldValue = isChecked.toInt().toString()
            formField.fieldType = "CheckBox"
        }
        is RatingBar -> {
            formField.fieldValue = rating.toString()
            formField.fieldType = "RatingBar"
        }
    }
    if (value != null) {
        formField.fieldValue = value
    }
    return formField
}

internal fun View.isTracable(): Boolean {
    return when (this) {
        is EditText -> true
        is SearchView -> true
        is RadioButton -> true
        is ToggleButton -> true
        is Switch -> true
        is CheckBox -> true
        is RatingBar -> true
        else -> false
    }
}

internal fun EditText.getInputTypeString(): String {
    return when (this.inputType) {
        InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_CLASS_TEXT -> {
            "Name"
        }
        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT -> {
            "Password"
        }
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
            "Email"
        }
        InputType.TYPE_CLASS_PHONE -> {
            "PhoneNumber"
        }
        InputType.TYPE_CLASS_DATETIME -> {
            "Data"
        }
        InputType.TYPE_CLASS_NUMBER -> {
            "Number"
        }
        InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS -> {
            "Address"
        }
        else -> {
            "EditText"
        }
    }
}
