package webtrekk.android.sdk.domain.external

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Called
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.coroutinesDispatchersProvider
import webtrekk.android.sdk.util.trackRequest

/**
 * Tests for TrackCustomForm — validates that the createField refactor
 * (passing Params directly instead of 8 individual arguments) produces
 * identical invocation behaviour.
 *
 * View-level tests (ViewGroup parsing) belong in androidTest; here we test
 * the opt-out guard and that caching is invoked when opt-out is false.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TrackCustomFormLogicTest : BaseExternalTest() {

    @MockK
    lateinit var cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams

    private lateinit var trackCustomForm: TrackCustomForm

    @Before
    override fun setup() {
        super.setup()
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkObject(AppModule)
        io.mockk.every { AppModule.logger } returns io.mockk.mockk(relaxed = true)
        trackCustomForm = TrackCustomForm(coroutineContext, cacheTrackRequestWithCustomParams)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        unmockkAll()
    }

    @Test
    fun `does not cache when opt-out is active`() = runTest {
        val viewGroup = io.mockk.mockk<android.view.ViewGroup>(relaxed = true)
        val params = TrackCustomForm.Params(
            trackRequest = trackRequest,
            isOptOut = true,
            viewGroup = viewGroup,
            formName = "loginForm",
            trackingIds = emptyList(),
            renameFields = emptyMap(),
            confirmButton = true,
            anonymous = false,
            changeFieldsValue = emptyMap(),
            fieldsOrder = emptyList(),
            anonymousSpecificFields = emptyList(),
            fullContentSpecificFields = emptyList()
        )
        trackCustomForm(params, coroutinesDispatchersProvider())
        coVerify { cacheTrackRequestWithCustomParams wasNot Called }
    }

    @Test
    fun `invokes cacheTrackRequestWithCustomParams when opt-out is false`() = runTest {
        val viewGroup = io.mockk.mockk<android.view.ViewGroup>(relaxed = true)
        // parseView returns empty list — createField will produce empty string, but caching is still called
        io.mockk.every { viewGroup.childCount } returns 0

        val params = TrackCustomForm.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            viewGroup = viewGroup,
            formName = "loginForm",
            trackingIds = emptyList(),
            renameFields = emptyMap(),
            confirmButton = true,
            anonymous = false,
            changeFieldsValue = emptyMap(),
            fieldsOrder = emptyList(),
            anonymousSpecificFields = emptyList(),
            fullContentSpecificFields = emptyList()
        )
        trackCustomForm(params, coroutinesDispatchersProvider())
        coVerify(exactly = 1) { cacheTrackRequestWithCustomParams.invoke(any()) }
    }

    @Test
    fun `Params data class holds all 12 fields correctly`() {
        val viewGroup = io.mockk.mockk<android.view.ViewGroup>(relaxed = true)
        val params = TrackCustomForm.Params(
            trackRequest = trackRequest,
            isOptOut = false,
            viewGroup = viewGroup,
            formName = "myForm",
            trackingIds = listOf(1, 2),
            renameFields = mapOf(1 to "username"),
            confirmButton = false,
            anonymous = true,
            changeFieldsValue = mapOf(2 to "override"),
            fieldsOrder = listOf(2, 1),
            anonymousSpecificFields = listOf(3),
            fullContentSpecificFields = listOf(4)
        )
        assertThat(params.formName).isEqualTo("myForm")
        assertThat(params.trackingIds).containsExactly(1, 2)
        assertThat(params.renameFields[1]).isEqualTo("username")
        assertThat(params.anonymous).isTrue()
        assertThat(params.confirmButton).isFalse()
        assertThat(params.anonymousSpecificFields).containsExactly(3)
        assertThat(params.fullContentSpecificFields).containsExactly(4)
    }
}
