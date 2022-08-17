package com.example.webtrekk.androidsdk.mapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.webtrekk.androidsdk.R
import kotlinx.android.synthetic.main.activity_second.tvLink
import webtrekk.android.sdk.Webtrekk

class SecondActivity : Activity() {
    private val DEEPLINK_SCHEME = "com.appoxee.test"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<View>(R.id.handle_deep_link).setOnClickListener {
            //Webtrekk.getInstance().trackPage(PageViewEvent("SecondActivity"))
            handleIntent()
        }
        handleIntent()
    }

    private fun handleIntent(){
        intent?.data?.let {
            Log.d(this::class.java.name,it.toString())
            tvLink.text=it.toString()
            Webtrekk.getInstance().trackUrl(it, "wt_sm")
            Webtrekk.getInstance().trackPage(this)
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