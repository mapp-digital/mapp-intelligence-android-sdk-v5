package webtrekk.android.sdk.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import webtrekk.android.sdk.logError

internal val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
    logError("Caught coroutine exception $exception")
}

internal data class CoroutineDispatchers(
    val mainDispatcher: CoroutineDispatcher,
    val defaultDispatcher: CoroutineDispatcher,
    val ioDispatcher: CoroutineDispatcher
)
