package webtrekk.android.sdk.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.module.InteractorModule

class EngageIntegrationReceiver : BroadcastReceiver() {
    private val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"

    private val sessions: Sessions by lazy { InteractorModule.sessions }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            context?.let { ctx ->
                val action = it.action
                if (it.component?.packageName.equals(ctx.packageName)) {
                    if (ACTION == action) {
                        it.extras?.getString("dmcUserId")?.let { dmcUserId ->
                            if (!sessions.isAnonymous() && dmcUserId.isNotEmpty()) {
                                sessions.setDmcUserId(dmcUserId)
                                sessions.setUserUpdated(true)
                            }
                        }
                    }
                }
            }
        }
    }
}