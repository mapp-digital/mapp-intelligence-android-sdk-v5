package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Aleksandar Marinkovic on 30/07/2020.
 * Copyright (c) 2020 MAPP.
 */
internal class MappIntelligenceListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WebtrekkListener", intent.action)
    }

    companion object {
        const val MappToWebtrekk = "mapp.to.webtrekk.event"
        const val WebtrekkToMapp = "webtrekk.to.mapp.event"
        const val INTELLIGENCE_DATA = "INTELLIGENCE_DATA"
    }
}