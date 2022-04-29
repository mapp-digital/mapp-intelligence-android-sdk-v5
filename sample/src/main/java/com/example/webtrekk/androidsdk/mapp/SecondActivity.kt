package com.example.webtrekk.androidsdk.mapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.webtrekk.androidsdk.R
import java.util.logging.Logger
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.Webtrekk

class SecondActivity : Activity() {
    private val DEEPLINK_SCHEME = "com.appoxee.test"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
//        findViewById<View>(R.id.open_link).setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("http://google.com")
//            startActivity(intent)
//        }
        handleIntent()
    }

    private fun handleIntent(){
        intent?.data?.let {
            Log.d(this::class.java.name,it.toString())
            Webtrekk.getInstance().trackUrl(it, "wt_sm")
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent != null) {
            if (DEEPLINK_SCHEME == intent.action) {
                val uri = intent.data
            }
        }
    }
}