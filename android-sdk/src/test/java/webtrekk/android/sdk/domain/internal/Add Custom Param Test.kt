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
import webtrekk.android.sdk.data.DataResult
import webtrekk.android.sdk.data.model.CustomParam
import webtrekk.android.sdk.data.repository.CustomParamRepository
import kotlin.coroutines.CoroutineContext

internal class `Add Custom Param Test` : CoroutineScope {

    private lateinit var customParamRepository: CustomParamRepository
    private lateinit var customParams: List<CustomParam>
    private lateinit var addCustomParams: AddCustomParams

    private val job = SupervisorJob()
    private val testCoroutineContext = TestCoroutineContext()
    override val coroutineContext: CoroutineContext
        get() = job + testCoroutineContext

    @Before
    fun tearUp() {
        customParamRepository = mockkClass(CustomParamRepository::class)

        customParams = listOf(
            CustomParam(trackId = 1, paramKey = "cs", paramValue = "val 1"),
            CustomParam(trackId = 1, paramKey = "cd", paramValue = "val 2"),
            CustomParam(trackId = 2, paramKey = "cs", paramValue = "val 3")
        )

        addCustomParams = AddCustomParams(customParamRepository, coroutineContext)
    }

    @After
    fun tearDown() {
        coroutineContext.cancel()
    }

    @Test
    fun `insert custom params into database and return success`() {
        coEvery { customParamRepository.addCustomParams(customParams) } returns DataResult.Success(
            customParams
        )

        launch {
            addCustomParams(customParams)

            assertThat(DataResult.Success(customParams), `is`(addCustomParams.testResult))
        }
    }
}
