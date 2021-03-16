package webtrekk.android.sdk.integration

import android.content.Context
import android.content.Intent
import java.lang.Exception

/**
 * Created by Aleksandar Marinkovic on 30/07/2020.
 * Copyright (c) 2020 MAPP.
 */
internal object IntelligenceEvent {
    fun sendEvent(
        context: Context?,
        event: String?,
        data: String?
    ) {
        try {
            if (context == null)
                return
            val intent: Intent = Intent(MappIntelligenceListener.WebtrekkToMapp).setClassName(
                context,
                "com.appoxee.MappIntelligenceListener"
            )
            val packageName = context.applicationContext.packageName
            intent.addCategory(packageName)
            intent.putExtra(MappIntelligenceListener.INTELLIGENCE_DATA, data)
            intent.type = event
            // context.sendBroadcast(intent)
        } catch (ignored: Exception) {
        }
    }
}