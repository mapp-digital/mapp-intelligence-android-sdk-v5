package webtrekk.android.sdk.integration

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.module.InteractorModule

/**
 * Tests for EngageIntegrationReceiver.onReceive() — validates the merged nested if:
 *   Before: if (packageMatches) { if (ACTION == action) { ... } }
 *   After:  if (packageMatches && ACTION == action) { ... }
 *
 * Both should trigger Webtrekk.init() only when both conditions are true.
 */
internal class EngageIntegrationReceiverTest {

    private val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"
    private val PACKAGE = "com.example.app"

    private lateinit var receiver: EngageIntegrationReceiver
    private lateinit var mockContext: Context
    private lateinit var mockWebtrekk: Webtrekk

    @Before
    fun setUp() {
        receiver = EngageIntegrationReceiver()
        mockContext = mockk(relaxed = true)
        mockWebtrekk = mockk(relaxed = true)
        every { mockContext.packageName } returns PACKAGE
        // Webtrekk.getInstance() is @JvmStatic on the companion — mock the companion object
        mockkObject(Webtrekk.Companion)
        every { Webtrekk.getInstance() } returns mockWebtrekk
        mockkObject(InteractorModule)
        every { InteractorModule.sessions } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    private fun makeIntent(action: String?, packageName: String?, dmcUserId: String? = null): Intent {
        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns action
        val component = if (packageName != null) {
            mockk<ComponentName>().also { every { it.packageName } returns packageName }
        } else null
        every { intent.component } returns component
        val extras = if (dmcUserId != null) {
            mockk<Bundle>().also { every { it.getString("dmcUserId") } returns dmcUserId }
        } else null
        every { intent.extras } returns extras
        return intent
    }

    @Test
    fun `onReceive calls init when package and action both match`() {
        val intent = makeIntent(action = ACTION, packageName = PACKAGE)
        receiver.onReceive(mockContext, intent)
        verify(exactly = 1) { mockWebtrekk.init(mockContext) }
    }

    @Test
    fun `onReceive does NOT call init when action does not match`() {
        val intent = makeIntent(action = "wrong.action", packageName = PACKAGE)
        receiver.onReceive(mockContext, intent)
        verify(exactly = 0) { mockWebtrekk.init(any()) }
    }

    @Test
    fun `onReceive does NOT call init when package does not match`() {
        val intent = makeIntent(action = ACTION, packageName = "com.other.app")
        receiver.onReceive(mockContext, intent)
        verify(exactly = 0) { mockWebtrekk.init(any()) }
    }

    @Test
    fun `onReceive does nothing when both package and action are wrong`() {
        val intent = makeIntent(action = "wrong.action", packageName = "com.other.app")
        receiver.onReceive(mockContext, intent)
        verify(exactly = 0) { mockWebtrekk.init(any()) }
    }

    @Test
    fun `onReceive sets dmcUserId when non-empty`() {
        val mockSessions = mockk<webtrekk.android.sdk.core.Sessions>(relaxed = true)
        every { InteractorModule.sessions } returns mockSessions
        val intent = makeIntent(action = ACTION, packageName = PACKAGE, dmcUserId = "user123")
        receiver.onReceive(mockContext, intent)
        verify(exactly = 1) { mockSessions.setDmcUserId("user123") }
    }

    @Test
    fun `onReceive does not set dmcUserId when empty`() {
        val mockSessions = mockk<webtrekk.android.sdk.core.Sessions>(relaxed = true)
        every { InteractorModule.sessions } returns mockSessions
        val intent = makeIntent(action = ACTION, packageName = PACKAGE, dmcUserId = "")
        receiver.onReceive(mockContext, intent)
        verify(exactly = 0) { mockSessions.setDmcUserId(any()) }
    }

    @Test
    fun `onReceive handles null context gracefully`() {
        val intent = makeIntent(action = ACTION, packageName = PACKAGE)
        // Should not throw
        receiver.onReceive(null, intent)
        verify(exactly = 0) { mockWebtrekk.init(any()) }
    }

    @Test
    fun `onReceive handles null intent gracefully`() {
        // Should not throw
        receiver.onReceive(mockContext, null)
        verify(exactly = 0) { mockWebtrekk.init(any()) }
    }
}
