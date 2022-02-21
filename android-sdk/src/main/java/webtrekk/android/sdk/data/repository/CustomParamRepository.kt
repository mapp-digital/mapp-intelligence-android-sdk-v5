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

package webtrekk.android.sdk.data.repository

import webtrekk.android.sdk.data.entity.CustomParam

/**
 * A repository interface that represents the [CustomParam] operations in the data layer. The domain layer interacts ONLY with repositories interfaces and not directly with Room.
 *
 * All the methods are suspendable and use Coroutines under the hood for the I/O operations.
 * All the methods return the object encapsulated in a [Result], and handling the success or the failure of the results are done in the domain layer.
 */
internal interface CustomParamRepository {

    /**
     * Returns list of [CustomParam] after adding in the database encapsulated in a [Result].
     *
     * @param customParams list of [CustomParam] to be added in the database.
     */
    suspend fun addCustomParams(customParams: List<CustomParam>): Result<List<CustomParam>>

    /**
     * Returns list of [CustomParam] for a [TrackRequest] by track request id, encapsulated in a [Result].
     *
     * @param trackId the [TrackRequest] Id.
     */
    suspend fun getCustomParamsByTrackId(trackId: Long): Result<List<CustomParam>>

    /**
     * Deletes all custom params from database
     */
    suspend fun deleteAllCustomParams(): Result<Boolean>
}
