package webtrekk.android.sdk.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import webtrekk.android.sdk.data.DaoProvider
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.repository.TrackRequestRepositoryImpl
import kotlin.coroutines.CoroutineContext

internal const val KEY_RESULT = "result"

internal class DataWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters),
    CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val trackRequestRepository =
        TrackRequestRepositoryImpl(DaoProvider.provideTrackRequestDao(applicationContext))

    override fun doWork(): Result {
        Log.wtf("Inside work manager", "Doing some work here!")

        launch(Dispatchers.IO) {
            // sendToServer()
            val result = trackRequestRepository.getTrackRequests()
            when (result) {
                is DataResult.Success -> Log.wtf("data inside work manager", result.data.toString())
                is DataResult.Fail -> Log.wtf("exception", result.exception)
            }
        }

        return Result.success()
    }

    private fun sendToServer() {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("https://q3.webtrekk.net/385255285199574/wt?p=111,TestingActivity15,0,1080x1794,32,0,1543312394347,0,0,0&eid=6154331084660314219&fns=0&one=0&tz=1&la=US&ps=0&X-WT-UA=Tracking+Library+1.1.1+%28Android+9%3B+Google+Android+SDK+built+for+x86%3B+en_US%29&cp783=portrait&cp784=2&cs804=1.0&cs805=1&cs807=WIFI&cs814=28&cs815=0&cs816=false&eor=1")
            .build()
        val response = okHttpClient.newCall(request).execute()
        Log.wtf("response", response.body()?.string())
    }
}
