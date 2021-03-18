package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.CampaignParam
import webtrekk.android.sdk.InternalParam
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.extension.addNotNull
import webtrekk.android.sdk.extension.encodeToUTF8

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class CampaignParameters
@JvmOverloads
constructor(var campaignId: String = "") : BaseEvent {
    var customParameters: Map<Int, String> = emptyMap()
    var oncePerSession: Boolean = false
    var mediaCode: String = "wt_mc"
    var action: CampaignAction = CampaignAction.CLICK

    enum class CampaignAction { CLICK, VIEW }

    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        if (!customParameters.isNullOrEmpty()) {
            customParameters.forEach { (key, value) ->
                map["${CampaignParam.CAMPAIGN_PARAM}$key"] = value
            }
        }
        map.addNotNull(
            CampaignParam.CAMPAIGN_ACTION_PARAM,
            if (action == CampaignAction.CLICK) "c" else "v"
        )

        if (campaignId.isNotBlank())
            map.addNotNull(
                InternalParam.MEDIA_CODE_PARAM_EXCHANGER,
                ("$mediaCode=").encodeToUTF8() + campaignId
            ) else {
            map.addNotNull(
                Param.MEDIA_CODE,
                mediaCode
            )
        }
        return map
    }
}