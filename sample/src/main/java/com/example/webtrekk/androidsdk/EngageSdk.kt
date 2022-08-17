package com.example.webtrekk.androidsdk

import android.app.Application
import android.util.Log
import com.appoxee.Appoxee
import com.appoxee.AppoxeeOptions

object EngageSdk {

    private val appoxeeOptions = AppoxeeOptions().apply {
        appID = "310498"
        cepURL = "https://jamie.c.shortest-route.com"
        tenantID = "60211"
        server = AppoxeeOptions.Server.EMC_US
        sdkKey = "18288464ebf62f.59797060"
    }

    private val listener  = Appoxee.OnInitCompletedListener { successful, failReason ->
        Log.d(this::class.java.simpleName, "Successful $successful")
    }

    fun init(application: Application) {
        Appoxee.engage(application, appoxeeOptions)
        Appoxee.instance().addInitListener(listener)
    }
}