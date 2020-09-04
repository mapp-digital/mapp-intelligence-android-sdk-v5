package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import webtrekk.android.sdk.util.alias

/**
 * Created by Aleksandar Marinkovic on 30/07/2020.
 * Copyright (c) 2020 MAPP.
 */
internal class MappIntelligenceListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.type == SET_ALIAS) {
            alias = intent.getStringExtra(INTELLIGENCE_DATA) ?: ""
        }
    }

    companion object {
        const val MappToWebtrekk = "mapp.to.webtrekk.event"
        const val WebtrekkToMapp = "webtrekk.to.mapp.event"
        const val INTELLIGENCE_DATA = "INTELLIGENCE_DATA"
        const val GET_ALIAS = "GET_ALIAS"
        const val SET_ALIAS = "SET_ALIAS"
        const val PAGE = "PAGE"
        const val EVENT = "EVENT"
        const val MEDIA = "MEDIA"
        const val CRASH = "CRASH"
        const val FORM = "FORM"
    }
}