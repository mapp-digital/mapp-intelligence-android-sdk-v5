package com.example.webtrekk.androidsdk

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.webtrekk.webtrekksdk.Webtrekk
import com.webtrekk.webtrekksdk.WebtrekkUserParameters

class MainActivity : AppCompatActivity() {
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionsManager = PermissionsManager(this, permissionCallback)


        findViewById<Button>(R.id.btnTrackRequest).setOnClickListener {
            trackRequest()
        }

    }

    private val permissionCallback = object : PermissionsManager.Callback {
        override fun onPermissionsResult(results: Map<String, Int>) {
            val permissionsGranted = results.get(Manifest.permission.READ_CONTACTS)==PermissionsManager.PERMISSION_GRANTED &&
                    results.get(Manifest.permission.WRITE_CONTACTS)==PermissionsManager.PERMISSION_GRANTED

            if(permissionsGranted){
                Webtrekk.getInstance().initWebtrekk(this@MainActivity.application, R.raw.webrekk_config_normal)
                findViewById<TextView>(R.id.tvEverID).text = "EverID: ${Webtrekk.getInstance().everId}"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        permissionsManager.requestPermissions(
            listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
        )
    }

    private fun trackRequest() {
        Webtrekk.getInstance().track()
        val params = WebtrekkUserParameters()
        params.setEmail("test@abc.com")
        params.setPhone("+1345034593045")
        Webtrekk.getInstance().track(params)
    }
}