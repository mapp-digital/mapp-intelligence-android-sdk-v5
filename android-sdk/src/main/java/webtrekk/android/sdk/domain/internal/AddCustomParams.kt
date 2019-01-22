package webtrekk.android.sdk.domain.internal

import android.util.Log
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.repository.CustomParamRepository
import kotlin.coroutines.CoroutineContext

internal class AddCustomParams(
    private val customParamRepository: CustomParamRepository,
    coroutineContext: CoroutineContext
) {

    var scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    // for testing
    internal lateinit var testResult: DataResult<Any>

    operator fun invoke(customParamList: List<CustomParam>) {
        scope.launch {
            val result = customParamRepository.addCustomParams(customParamList)
            testResult = result

            when (result) {
                is DataResult.Success
                -> Log.wtf("Custom Params", "Added ${result.data} to the database")
                is DataResult.Fail -> Log.wtf("Custom Params", result.exception)
            }
        }
    }
}
