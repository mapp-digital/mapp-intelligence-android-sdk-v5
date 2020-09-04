package webtrekk.android.sdk.integration

import android.content.Context
import android.content.Intent

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
        if(context==null)
            return
        val intent: Intent = Intent(MappIntelligenceListener.WebtrekkToMapp).setClassName(
            context,
            "webtrekk.android.sdk.integration.MappIntelligenceListener"
        )
        intent.putExtra(MappIntelligenceListener.INTELLIGENCE_DATA, data)
        intent.type = event
        context.sendBroadcast(intent)
    }
}