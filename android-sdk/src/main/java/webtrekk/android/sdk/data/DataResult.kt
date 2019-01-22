package webtrekk.android.sdk.data

import java.lang.Exception

internal inline fun <T> tryToQuery(
    query: () -> DataResult<T>,
    errorMessage: String
): DataResult<T> {
    return try {
        query()
    } catch (exception: Exception) {
        DataResult.Fail(Exception(errorMessage, exception))
    }
}

internal sealed class DataResult<out T> {

    data class Success<out T>(val data: T) : DataResult<T>()
    data class Fail(val exception: Exception) : DataResult<Nothing>()
}
