package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.dao.CustomParamDao
import webtrekk.android.sdk.data.entity.CustomParam

internal class CustomParamRepositoryImpl(private val customParamDao: CustomParamDao) :
    CustomParamRepository {

    override suspend fun addCustomParams(customParams: List<CustomParam>): Result<List<CustomParam>> {
        return runCatching {
            customParamDao.setCustomParams(customParams).run { customParams }
        }
    }

    override suspend fun getCustomParamsByTrackId(trackId: Long): Result<List<CustomParam>> {
        return runCatching {
            customParamDao.getCustomParamsByTrackId(trackId)
        }
    }
}
