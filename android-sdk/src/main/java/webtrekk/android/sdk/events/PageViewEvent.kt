package webtrekk.android.sdk.events

import webtrekk.android.sdk.events.eventParams.BaseEvent
import webtrekk.android.sdk.events.eventParams.CampaignParameters
import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.PageParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters
import webtrekk.android.sdk.events.eventParams.UserCategories

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageViewEvent(val name: String) : BaseEvent {
    var pageParameters: PageParameters? = null
    var sessionParameters: SessionParameters? = null
    var userCategories: UserCategories? = null
    var eCommerceParameters: ECommerceParameters? = null
    var campaignParameters: CampaignParameters? = null
    val customParameters = mutableMapOf<String, String>()
    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        map.putAll(customParameters)
        pageParameters?.let { map.putAll(it.toHasMap()) }
        sessionParameters?.let { map.putAll(it.toHasMap()) }
        userCategories?.let { map.putAll(it.toHasMap()) }
        eCommerceParameters?.let { map.putAll(it.toHasMap()) }
        campaignParameters?.let { map.putAll(it.toHasMap()) }
        return map
    }
}