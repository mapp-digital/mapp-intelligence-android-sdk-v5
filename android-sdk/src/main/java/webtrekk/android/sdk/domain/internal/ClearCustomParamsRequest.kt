package webtrekk.android.sdk.domain.internal

import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.domain.InternalInteractor

internal class ClearCustomParamsRequest(private val customParamRepository: CustomParamRepository) :
    InternalInteractor<String?, Boolean> {
    override suspend fun invoke(invokeParams: String?): Result<Boolean> {
        return customParamRepository.deleteAllCustomParams()
    }
}