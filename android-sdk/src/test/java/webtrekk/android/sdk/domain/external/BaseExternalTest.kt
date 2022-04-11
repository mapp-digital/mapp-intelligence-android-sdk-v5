package webtrekk.android.sdk.domain.external

import android.content.Context
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.core.WebtrekkImpl
import webtrekk.android.sdk.util.configuration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
open class BaseExternalTest {

    @RelaxedMockK
    lateinit var appContext: Context

    protected val job = SupervisorJob();

    protected val dispatcher = Dispatchers.Unconfined

    protected val coroutineScope = CoroutineScope(dispatcher + job)

    protected val coroutineContext = coroutineScope.coroutineContext

    lateinit var webtrekk: Webtrekk

    @BeforeAll
    open fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        webtrekk = spyk<WebtrekkImpl>()

        webtrekk.init(appContext, configuration)
    }

    @AfterAll
    open fun tearDown() {
        coroutineScope.cancel()
        Dispatchers.resetMain()
        unmockkAll()
    }
}