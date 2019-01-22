package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.tryToQuery

internal class CustomParamRepositoryImpl(private val customParamDao: CustomParamDao) :
    CustomParamRepository {

    override suspend fun addCustomParams(customParams: List<CustomParam>): DataResult<Any> {
        return tryToQuery(
            {
                customParamDao.setCustomParams(customParams)
                DataResult.Success(customParams)
            }, "Failed to add custom params to webtrekk database"
        )
    }

    override suspend fun getCustomParamsByTrackId(trackId: Long): DataResult<List<CustomParam>> {
        return tryToQuery(
            {
                val customParams = customParamDao.getCustomParamsByTrackId(trackId)
                DataResult.Success(customParams)
            }, "Failed to get custom params for specific track id"
        )
    }
}
