package com.example.webtrekk.androidsdk.mapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.webtrekk.androidsdk.R

class DeepLinkActivity : Activity() {
    private val APX_LAUNCH_DEEPLINK_ACTION = "com.appoxee.VIEW_DEEPLINK"
    var tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tv = findViewById<View>(R.id.textView) as TextView
        var uri: Uri? = null
        val link: String?
        if (intent != null) {
            if (APX_LAUNCH_DEEPLINK_ACTION == intent.action) {
                uri = intent.data
            }
            link = uri!!.getQueryParameter("link")
            openDeepLink(uri)
        } else {
            link = null
        }
        findViewById<View>(R.id.handle_deep_link).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun openDeepLink(uri: Uri?) {
        val protocol = uri!!.scheme
        val server = uri.authority
        val path = uri.path
        val query = uri.query
        val link = uri.getQueryParameter("link")
        val messageId = uri.getQueryParameter("message_id")
        if (uri != null && uri.toString() != null) {
            tv!!.text = "DEEPLINK ACTIVITY URI  = $query"
        } else {
            tv!!.text = "DEEPLINK ACTIVITY URI is Null"
        }
    }
}