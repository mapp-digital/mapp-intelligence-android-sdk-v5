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

package webtrekk.android.sdk.module

import org.koin.dsl.module.module
import webtrekk.android.sdk.api.datasource.SyncPostRequestsDataSource
import webtrekk.android.sdk.api.datasource.SyncPostRequestsDataSourceImpl
import webtrekk.android.sdk.core.Scheduler
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSource
import webtrekk.android.sdk.api.datasource.SyncRequestsDataSourceImpl
import webtrekk.android.sdk.data.entity.DataTrack
import webtrekk.android.sdk.data.repository.CustomParamRepository
import webtrekk.android.sdk.data.repository.CustomParamRepositoryImpl
import webtrekk.android.sdk.data.repository.TrackRequestRepository
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import webtrekk.android.sdk.core.SchedulerImpl
import webtrekk.android.sdk.core.SessionsImpl
import webtrekk.android.sdk.domain.internal.CacheTrackForm
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.domain.internal.ClearTrackRequests
import webtrekk.android.sdk.domain.internal.ExecutePostRequest
import webtrekk.android.sdk.domain.internal.ExecuteRequest
import webtrekk.android.sdk.domain.internal.GetCachedDataTracks

/**
 * Module for all dependencies of the data layer (internal repositories and api data sources) injected in [WebtrekkImpl].
 */
internal val dataModule = module {
    single<TrackRequestRepository> {
        TrackRequestRepositoryImpl(
            get()
        )
    }
    single<CustomParamRepository> {
        CustomParamRepositoryImpl(
            get()
        )
    }
    single<SyncRequestsDataSource<DataTrack>> {
        SyncRequestsDataSourceImpl(
            get()
        )
    }

    single<SyncPostRequestsDataSource<List<DataTrack>>> {
        SyncPostRequestsDataSourceImpl(
            get()
        )
    }
}

/**
 * Module for all the internal interactors in the domain layer injected in [WebtrekkImpl].
 */
internal val internalInteractorsModule = module {
    single<Sessions> { SessionsImpl(get()) }
    single<Scheduler> { SchedulerImpl(get()) }

    factory { CacheTrackRequest(get()) }
    factory { GetCachedDataTracks(get()) }
    factory { CacheTrackRequestWithCustomParams(get(), get()) }
    factory { CacheTrackForm(get(), get()) }
    factory { ExecuteRequest(get(), get()) }
    factory { ExecutePostRequest(get(), get()) }
    factory { ClearTrackRequests(get()) }
}
