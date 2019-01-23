package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.logDebug
import webtrekk.android.sdk.logError
import kotlin.coroutines.CoroutineContext

internal class AddCustomParams(
    private val customParamRepository: CustomParamRepository,
    coroutineContext: CoroutineContext
) {

    private val scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    // for testing
    internal lateinit var testResult: DataResult<Any>

    operator fun invoke(customParamList: List<CustomParam>) = scope.launch {
        val result = customParamRepository.addCustomParams(customParamList)
        testResult = result

        when (result) {
            is DataResult.Success -> logDebug("Added custom params: ${result.data}")
            is DataResult.Fail -> logError("Error while appending custom param: ${result.exception}")
        }
    }
}
