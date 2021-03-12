package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class CampaignParameters(var campaignId: String = "") : BaseEvent {
    var customParameters: Map<Int, String> = emptyMap()
    var oncePerSession: Boolean = false
    var mediaCode: String = ""
    var action: CampaignAction? = null

    enum class CampaignAction { click, view }

    override suspend fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        return map
    }
}