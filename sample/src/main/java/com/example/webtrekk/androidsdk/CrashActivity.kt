package com.example.webtrekk.androidsdk

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.chooseExceptionType
import kotlinx.android.synthetic.main.activity_crash.trackCaught
import kotlinx.android.synthetic.main.activity_crash.trackCustom
import kotlinx.android.synthetic.main.activity_crash.trackUncaught
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Webtrekk


class CrashActivity : AppCompatActivity() {

    var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        val adapter: ArrayAdapter<Enum<ExceptionType>> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, ExceptionType.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseExceptionType.adapter = adapter
        chooseExceptionType.prompt = "Choose ExceptionType"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("Sample Application", Context.MODE_PRIVATE)
                ?: return
        sharedPreferences.getString("ExceptionType", ExceptionType.ALL.toString())
            ?.let { ExceptionType.valueOf(it).ordinal }
            ?.let { chooseExceptionType.setSelection(it) }

        chooseExceptionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                with(sharedPreferences.edit()) {
                    putString("ExceptionType", ExceptionType.values()[position].toString())
                    commit()
                }
                if (isFirstTime)
                    isFirstTime = false
                else
                    Toast.makeText(
                        this@CrashActivity, "Exit the app, kill it in memory and start again " +
                            "to init Webtrekk with new ExceptionType", Toast.LENGTH_SHORT
                    ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        trackUncaught.setOnClickListener {
            Integer.parseInt("@!#")
        }

        trackCaught.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException(e)
            }
        }

        trackCustom.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException("Hello", "I am custom exception :)")
            }
        }
    }
}
