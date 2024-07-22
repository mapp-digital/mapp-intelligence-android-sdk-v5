package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import webtrekk.android.sdk.module.InteractorModule

class EngageIntegrationReceiver : BroadcastReceiver() {
    private val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            context?.let { ctx ->
                val action = it.action
                if (it.component?.packageName.equals(ctx.packageName)) {
                    if (ACTION == action) {
                        it.extras?.getString("dmcUserId")?.let { dmcUserId ->
                            if (dmcUserId.isNotEmpty()) {
                                InteractorModule.sessions.setDmcUserId(dmcUserId)
                            }
                        }
                    }
                }
            }
        }
    }
}