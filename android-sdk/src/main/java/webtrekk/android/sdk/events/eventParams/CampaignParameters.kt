package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class CampaignParameters(var campaignId: String = "") {
    var customParameters: Map<Int, String> = emptyMap()
    var oncePerSession: Boolean = false
    var mediaCode: String = ""
    var action: CampaignAction? = null

    enum class CampaignAction { click, view }
}