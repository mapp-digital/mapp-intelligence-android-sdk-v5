package webtrekk.android.sdk.domain.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import webtrekk.android.sdk.data.model.CustomParam
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
    internal var testResult: Result<Any>? = null

    operator fun invoke(customParamList: List<CustomParam>) = scope.launch {
        customParamRepository.addCustomParams(customParamList)
            .onSuccess { logDebug("Added custom params: $it") }
            .onFailure { logError("Error while appending custom param: $it") }
            .also { testResult = it }
    }
}
