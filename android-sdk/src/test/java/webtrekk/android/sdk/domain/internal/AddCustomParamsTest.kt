package webtrekk.android.sdk.domain.internal

import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.entity.CustomParam
import webtrekk.android.sdk.data.repository.CustomParamRepository
import kotlin.coroutines.CoroutineContext

internal class AddCustomParamsTest : CoroutineScope {

    private lateinit var customParamRepository: CustomParamRepository
    private lateinit var addCustomParams: AddCustomParams

    private var customParams = listOf(
        CustomParam(trackId = 1, paramKey = "cs", paramValue = "val 1"),
        CustomParam(trackId = 1, paramKey = "cd", paramValue = "val 2"),
        CustomParam(trackId = 2, paramKey = "cs", paramValue = "val 3")
    )

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        customParamRepository = mockkClass(CustomParamRepository::class)

        addCustomParams = AddCustomParams(customParamRepository, coroutineContext)
    }

    @After
    fun tearDown() {
        coroutineContext.cancel()
    }

    @Test
    fun `add custom params and return success`() {
        coEvery { customParamRepository.addCustomParams(customParams) } returns Result.success(
            customParams
        )

        launch {
            addCustomParams(customParams)

            assertThat(Result.success(customParams), `is`(addCustomParams.testResult))
        }
    }
}
