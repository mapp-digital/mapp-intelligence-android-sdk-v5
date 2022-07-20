package com.example.webtrekk.androidsdk

import android.app.Application
import android.util.Log
import com.appoxee.Appoxee
import com.appoxee.AppoxeeOptions

object EngageSdk {

    /*
    *  buildConfigField "String", "SDK_KEY", "\"181dcae619262c.50504750\""
            buildConfigField "String", "GOOGLE_PROJECT_ID", "\"abc\""
            buildConfigField "String", "CEP_URL", "\"https://jamie.c.shortest-route.com\""
            buildConfigField "String", "APP_ID", "\"310491\""
            buildConfigField "String", "TENANT_ID", "\"60211\""
            buildConfigField "String", "SERVER_INDEX", "\"EMC_US\""
    * */
    private val appoxeeOptions = AppoxeeOptions().apply {
        appID = "310493"
        cepURL = "https://jamie.c.shortest-route.com"
        tenantID = "60211"
        server = AppoxeeOptions.Server.EMC_US
        sdkKey = "182159e4eb562d.86710593"
    }

    private val listener  = Appoxee.OnInitCompletedListener { successful, failReason ->
        Log.d(this::class.java.simpleName, "Successful $successful")
    }

    fun init(application: Application) {
        Appoxee.engage(application, appoxeeOptions)
        Appoxee.instance().addInitListener(listener)
    }
}