package com.example.webtrekk.androidsdk.mapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.webtrekk.androidsdk.R

/**
 * Created by Varun on 4/7/2018.
 */
class CustomDeeplinkActivity : Activity() {
    private val APX_LAUNCH_CUSTOM_ACTION = "com.appoxee.VIEW_CUSTOM_LINKS"
    var tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tv = findViewById<View>(R.id.textView) as TextView
        findViewById<View>(R.id.open_link).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://google.com")
            startActivity(intent)
        }
        var uri: Uri? = null
        if (intent != null) {
            if (APX_LAUNCH_CUSTOM_ACTION == intent.action) {
                uri = intent.data
                uri?.let { openDeepLink(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun openDeepLink(uri: Uri?) {
        val deeplinkValue = uri!!.getQueryParameter("link")
        val mesageId = uri.getQueryParameter("message_id")
        if (uri != null && uri.toString() != null) {
            tv!!.text = "DEEPLINK ACTIVITY URI  = $deeplinkValue\nMessageId = $mesageId"
        } else {
            tv!!.text = "DEEPLINK ACTIVITY URI is Null"
        }
    }
}