package com.example.webtrekk.androidsdk

import android.app.Application
import android.util.Log
import com.appoxee.Appoxee
import com.appoxee.AppoxeeOptions

object EngageSdk {

    private val appoxeeOptions = AppoxeeOptions().apply {
        appID = "264157"
        tenantID = "33"
        server = AppoxeeOptions.Server.TEST
        sdkKey = "6017dd65b8d820.37698249"
    }

    private val listener  = Appoxee.OnInitCompletedListener { successful, failReason ->
        Log.d(this::class.java.simpleName, "Successful $successful")
    }

    fun init(application: Application) {
        Appoxee.engage(application, appoxeeOptions)
        Appoxee.instance().addInitListener(listener)
    }
}