package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.entity.CustomParam

internal interface CustomParamRepository {

    suspend fun addCustomParams(customParams: List<CustomParam>): Result<List<CustomParam>>
    suspend fun getCustomParamsByTrackId(trackId: Long): Result<List<CustomParam>>
}
