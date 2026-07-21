package com.example.webtrekk.androidsdk

import android.app.Application
import android.util.Log
import com.appoxee.Appoxee
import com.appoxee.shared.AppoxeeObserver
import com.appoxee.shared.AppoxeeOptions

object EngageSdk {
    private val appoxeeOptions = AppoxeeOptions(
        server = AppoxeeOptions.Server.L3,
        sdkKey = "183408d0cd3632.83592719",
        appId = "206974",
        tenantId = "5963"
    )

    private val listener  = AppoxeeObserver { successful, failReason ->
        Log.d(this::class.java.simpleName, "Successful $successful")
    }

    fun init(application: Application) {
        Appoxee.engage(application, appoxeeOptions)
        Appoxee.instance().subscribe(listener)
    }
}


