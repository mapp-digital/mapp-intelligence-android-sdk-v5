package com.example.webtrekk.androidsdk

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat

@SuppressLint("SetTextI18n")
class ResetDialogBuilder(context: Context, okClicked: (Boolean) -> Unit) : Builder(context) {

    init {
        val prefs = Prefs(context)

        //dialog title
        setTitle("Reset SDK")

        // dialog content
        val swBatch = SwitchCompat(context).apply {
            setText("Enable batch sending")
            isChecked = prefs.isBatchEnabled
            setPadding(30, 10, 10, 10)
        }

        //dialog root
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
            addView(swBatch)
        }

        //add root view to dialog
        setView(layout)

        // set positive button
        setPositiveButton("OK") { d, p ->
            prefs.isBatchEnabled = swBatch.isChecked
            okClicked(swBatch.isChecked)
        }

        //set negative button
        setNegativeButton("Cancel") { d, p -> d.dismiss() }

        show()
    }
}