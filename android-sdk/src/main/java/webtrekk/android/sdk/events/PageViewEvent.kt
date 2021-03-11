package webtrekk.android.sdk.events

import webtrekk.android.sdk.events.eventParams.ECommerceParameters
import webtrekk.android.sdk.events.eventParams.PageParameters
import webtrekk.android.sdk.events.eventParams.SessionParameters
import webtrekk.android.sdk.events.eventParams.UserCategories
import webtrekk.android.sdk.events.eventParams.CampaignParameters

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class PageViewEvent(val name: String) {
    var pageParameters: PageParameters = PageParameters()
    var sessionParameters: SessionParameters = SessionParameters()
    var userCategories: UserCategories = UserCategories()
    var eCommerceParameters: ECommerceParameters = ECommerceParameters()
    var campaignParameters: CampaignParameters = CampaignParameters()
}