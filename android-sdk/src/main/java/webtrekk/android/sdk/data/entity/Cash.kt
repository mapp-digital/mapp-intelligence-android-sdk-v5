package webtrekk.android.sdk.data.entity

import androidx.annotation.RestrictTo
import webtrekk.android.sdk.events.eventParams.CampaignParameters

/**
 * Created by Aleksandar Marinkovic on 3/16/21.
 * Copyright (c) 2021 MAPP.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal data class Cash(val campaignParametersList: MutableList<CampaignParameters> = mutableListOf()) {

    private fun addInList(campaignParameters: CampaignParameters) {
        if (campaignParametersList.size > 99) {
            campaignParametersList.removeAt(0)

        }
        campaignParametersList.add(campaignParameters)
    }

    fun canContinue(campaignParameters: CampaignParameters?): Boolean {
        campaignParameters?.let {
            if (!campaignParameters.oncePerSession) {
                return true
            }
            if (campaignParametersList.contains(campaignParameters)) {
                return false
            }
            this.addInList(campaignParameters)
            return true
        }
        return true
    }
}