/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.api.datasource

import okhttp3.OkHttpClient
import okhttp3.Request
import webtrekk.android.sdk.extension.executeRequestForResult
import webtrekk.android.sdk.data.entity.DataTrack

/**
 * The concrete implementation of [SyncRequestsDataSource], depending on [OkHttpClient].
 */
internal class SyncPostRequestsDataSourceImpl(private val okHttpClient: OkHttpClient) :
    SyncPostRequestsDataSource<List<DataTrack>> {

    /**
     * Returns the result of a [Request] encapsulated in a [Result] with the [receiver] of [DataTrack].
     *
     * @param request the request url.
     * @param receiver an instance of [DataTrack] that will be encapsulated within a [Result].
     */
    override suspend fun sendRequest(
        request: Request,
        receiver: List<DataTrack>
    ): Result<List<DataTrack>> {
        return okHttpClient.newCall(request).executeRequestForResult {
            receiver
        }
    }
}
