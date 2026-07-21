package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.core.WebtrekkLogger
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import kotlin.math.log

class EngageIntegrationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val applicationContext = context?.applicationContext ?: return

        if (intent?.action != ACTION) return

        intent.getStringExtra(DMC_USER_ID)
            ?.takeIf(String::isNotEmpty)
            ?.let { dmcUserId ->
                WebtrekkSharedPrefs(applicationContext).dmcUserId = dmcUserId
            }
    }

    private companion object {
        const val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"
        const val DMC_USER_ID = "dmcUserId"
    }
}
