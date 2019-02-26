package webtrekk.android.sdk.api

import okhttp3.OkHttpClient
import okhttp3.Request
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.extension.executeRequestForResult

internal class SyncRequestsDataSourceImpl(private val okHttpClient: OkHttpClient) :
    SyncRequestsDataSource<DataTrack> {

    override suspend fun sendRequest(request: Request, receiver: DataTrack): Result<DataTrack> {
        return okHttpClient.newCall(request).executeRequestForResult {
            receiver
        }
    }
}
