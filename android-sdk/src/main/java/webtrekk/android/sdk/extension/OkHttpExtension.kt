package webtrekk.android.sdk.extension

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { cont ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (cont.isCancelled) return

                cont.resumeWithException(e)
            }
        })
    }
}

suspend inline fun <T> Call.executeRequestForResult(block: () -> T): Result<T> {
    var response: Response? = null

    return try {
        response = this.await()
        if (response.isSuccessful) {
            Result.success(block())
        } else {
            Result.failure(IOException("Unexpected response $response"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    } finally {
        response?.close()
    }
}
