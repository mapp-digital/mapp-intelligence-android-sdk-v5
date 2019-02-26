package webtrekk.android.sdk.api

import okhttp3.Request

internal interface SyncRequestsDataSource<R> {

    suspend fun sendRequest(request: Request, receiver: R): Result<R>
}
