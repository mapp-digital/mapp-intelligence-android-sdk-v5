package com.example.webtrekk.androidsdk

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityCrashBinding
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Webtrekk


class CrashActivity : AppCompatActivity() {

    var isFirstTime = true
    private lateinit var binding: ActivityCrashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter: ArrayAdapter<Enum<ExceptionType>> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, ExceptionType.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.chooseExceptionType.adapter = adapter
        binding.chooseExceptionType.prompt = "Choose ExceptionType"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("Sample Application", Context.MODE_PRIVATE)
                ?: return
        sharedPreferences.getString("ExceptionType", ExceptionType.ALL.toString())
            ?.let { ExceptionType.valueOf(it).ordinal }
            ?.let { binding.chooseExceptionType.setSelection(it) }

        binding.chooseExceptionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        binding.trackUncaught.setOnClickListener {
            Integer.parseInt("@!#")
        }

        binding.trackCaught.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException(e)
            }
        }

        binding.trackCustom.setOnClickListener {
            try {
                Integer.parseInt("@!#")
            } catch (e: Exception) {
                Webtrekk.getInstance().trackException("Hello", "I am custom exception :)")
            }
        }
    }
}
