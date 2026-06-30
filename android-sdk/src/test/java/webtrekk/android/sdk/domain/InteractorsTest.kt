package webtrekk.android.sdk.domain

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Tests for InternalInteractor fun interface — validates SAM conversion still works
 * after the `fun interface` keyword was added.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class InteractorsTest {

    @Test
    fun `InternalInteractor fun interface can be lambda-implemented`() = runTest {
        val interactor: InternalInteractor<String, Int> =
            InternalInteractor { param -> Result.success(param.length) }

        val result = interactor.invoke("hello")
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(5)
    }

    @Test
    fun `InternalInteractor fun interface can return failure`() = runTest {
        val interactor: InternalInteractor<String, Int> =
            InternalInteractor { _ -> Result.failure(RuntimeException("error")) }

        val result = interactor.invoke("test")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `InternalInteractor anonymous class still compiles and runs`() = runTest {
        val interactor = object : InternalInteractor<Int, String> {
            override suspend fun invoke(invokeParams: Int): Result<String> =
                Result.success(invokeParams.toString())
        }
        assertThat(interactor.invoke(42).getOrNull()).isEqualTo("42")
    }
}
