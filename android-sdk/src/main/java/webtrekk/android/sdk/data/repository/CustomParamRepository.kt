package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.CustomParam

internal interface CustomParamRepository {

    suspend fun addCustomParams(customParams: List<CustomParam>): DataResult<Any>
    suspend fun getCustomParamsByTrackId(trackId: Long): DataResult<List<CustomParam>>
}
