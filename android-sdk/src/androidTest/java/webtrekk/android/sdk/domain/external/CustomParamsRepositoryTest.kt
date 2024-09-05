package webtrekk.android.sdk.domain.external

import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.repository.CustomParamRepository

internal class CustomParamsRepositoryTest : CustomParamRepository {
    private val params = mutableListOf<CustomParam>()

    override suspend fun addCustomParams(customParams: List<CustomParam>): Result<List<CustomParam>> {
        params.addAll(customParams)
        return Result.success(params)
    }

    override suspend fun getCustomParamsByTrackId(trackId: Long): Result<List<CustomParam>> {
        val data = params.filter { it.trackId == trackId }
        return Result.success(data)
    }
}