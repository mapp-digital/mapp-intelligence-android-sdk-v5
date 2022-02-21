package com.example.webtrekk.androidsdk

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.view.Gravity.CENTER
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import kotlin.random.Random

@SuppressLint("SetTextI18n")
class ResetDialogBuilder(context: Context, okClicked: (Boolean, String?) -> Unit) : Builder(context) {

    init {
        val prefs = Prefs(context)
        val etEverViewId= Random(100000).nextInt()
        //dialog title
        setTitle("Reset SDK")

        // dialog content
        val swBatch = SwitchCompat(context).apply {
            setText("Enable batch sending")
            isChecked = prefs.isBatchEnabled
            setPadding(30, 10, 10, 10)
        }

        val tvTitleEverId=TextView(context).apply {
            setText("Ever ID")
        }

        val etEverId=EditText(context).apply {
            id=etEverViewId
            setText("")
        }

        //dialog root
        val layout = LinearLayout(context).also {
            it.orientation = LinearLayout.VERTICAL
            it.setPadding(10, 10, 10, 10)
            it.addView(swBatch)
            it.addView(tvTitleEverId)
            it.addView(etEverId)
        }.apply { gravity = CENTER }

        //add root view to dialog
        setView(layout)

        // set positive button
        setPositiveButton("OK") { d, p ->
            prefs.isBatchEnabled = swBatch.isChecked
            okClicked(swBatch.isChecked, etEverId.text.toString())
        }

        //set negative button
        setNegativeButton("Cancel") { d, p -> d.dismiss() }

        show()
    }
}