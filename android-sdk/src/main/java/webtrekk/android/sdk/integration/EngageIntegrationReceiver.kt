package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.module.AppModule

class EngageIntegrationReceiver : BroadcastReceiver() {
    private val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"

    private val sharedPrefs: WebtrekkSharedPrefs by lazy { AppModule.webtrekkSharedPrefs }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            context?.let { ctx ->
                val action = it.action
                if (it.component?.packageName.equals(ctx.packageName)) {
                    if (ACTION == action) {
                        it.extras?.getString("dmcUserId")?.let { dmcUserId ->
                            if (dmcUserId.isNotEmpty())
                                sharedPrefs.dmcUserId = dmcUserId
                        }
                    }
                }
            }
        }
    }
}