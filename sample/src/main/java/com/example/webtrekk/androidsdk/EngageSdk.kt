package com.example.webtrekk.androidsdk

import android.app.Application
import android.util.Log
import com.appoxee.Appoxee
import com.appoxee.AppoxeeOptions

object EngageSdk {

    private val appoxeeOptions = AppoxeeOptions().apply {
        appID = "206974"
        tenantID = "5963"
        server = AppoxeeOptions.Server.L3
        sdkKey = "183408d0cd3632.83592719"
    }

    private val listener  = Appoxee.OnInitCompletedListener { successful, failReason ->
        Log.d(this::class.java.simpleName, "Successful $successful")
    }

    fun init(application: Application) {
        Appoxee.engage(application, appoxeeOptions)
        Appoxee.instance().addInitListener(listener)
    }
}