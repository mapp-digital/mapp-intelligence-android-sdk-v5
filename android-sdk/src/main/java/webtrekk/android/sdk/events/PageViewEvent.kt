package webtrekk.android.sdk.events

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    var pageParameters: PageParameters = PageParameters()
    var sessionParameters: SessionParameters = SessionParameters()
    var userCategories: UserCategories = UserCategories()
    var eCommerceParameters: ECommerceParameters = ECommerceParameters()
    var campaignParameters: CampaignParameters = CampaignParameters()
    val customParameters = mutableMapOf<String, String>()
    override suspend fun toHasMap(): MutableMap<String, String> =
        coroutineScope {
            val map = mutableMapOf<String, String>()
            val map1 = async { pageParameters.toHasMap() }
            val map2 = async { sessionParameters.toHasMap() }
            val map3 = async { userCategories.toHasMap() }
            val map4 = async { eCommerceParameters.toHasMap() }
            val map5 = async { campaignParameters.toHasMap() }
            map.putAll(customParameters)
            map.putAll(map1.await())
            map.putAll(map2.await())
            map.putAll(map3.await())
            map.putAll(map4.await())
            map.putAll(map5.await())
            return@coroutineScope map
        }
}