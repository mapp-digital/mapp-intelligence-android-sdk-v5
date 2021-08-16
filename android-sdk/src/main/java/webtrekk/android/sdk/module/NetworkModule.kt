package webtrekk.android.sdk.module

import android.content.Context
import androidx.work.Constraints
import okhttp3.OkHttpClient
import webtrekk.android.sdk.Config

object NetworkModule {

    val okHttpClient: OkHttpClient by lazy {
        provideOkHttpClient(
            config = LibraryModule.configuration
        )
    }

    val workManagerConstraints: Constraints by lazy {
        provideWorkManagerConstraints(
            config = LibraryModule.configuration
        )
    }

    private fun provideWorkManagerConstraints(
        config: Config
    ): Constraints = config.workManagerConstraints

    private fun provideOkHttpClient(
        config: Config
    ): OkHttpClient = config.okHttpClient
}

object TrackingModule {

    val dashPathEffect = DataModule
}

object DataModule

object InternalInteractionModule {
//    SessionsImpl(get())
//    SchedulerImpl(get())
//
//    CacheTrackRequest(get())
//    GetCachedDataTracks(get())
//    CacheTrackRequestWithCustomParams(get(), get())
//    ExecuteRequest(get(), get())
//    ExecutePostRequest(get(), get())
//    ClearTrackRequests(get())
}

object LibraryModule {

    @Volatile
    lateinit var application: Context

    @Volatile
    lateinit var configuration: Config

    fun initializeDI(app: Context, config: Config) {
        if (!::application.isInitialized) {
            synchronized(this) {
                application = app
                configuration = config
            }
        }
    }
}