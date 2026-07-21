package webtrekk.android.sdk.integration

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import webtrekk.android.sdk.data.WebtrekkSharedPrefs

internal class EngageIntegrationReceiverTest {

    private val receiver = EngageIntegrationReceiver()
    private val context = mockk<Context>()
    private val preferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)

    @Before
    fun setUp() {
        every { context.applicationContext } returns context
        every {
            context.getSharedPreferences(WebtrekkSharedPrefs.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        } returns preferences
        every {
            context.getSharedPreferences(WebtrekkSharedPrefs.PREVIOUS_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        } returns preferences
        every { preferences.edit() } returns editor
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `onReceive persists dmc user ID for expected action`() {
        val intent = intent(action = ACTION, dmcUserId = "user123")

        receiver.onReceive(context, intent)

        verify(exactly = 1) {
            editor.putString(WebtrekkSharedPrefs.DMC_USER_ID, "user123")
            editor.apply()
        }
    }

    @Test
    fun `onReceive accepts package restricted intent without explicit component`() {
        val intent = intent(action = ACTION, dmcUserId = "user123")
        every { intent.component } returns null
        every { intent.`package` } returns "com.example.app"

        receiver.onReceive(context, intent)

        verify(exactly = 1) {
            editor.putString(WebtrekkSharedPrefs.DMC_USER_ID, "user123")
        }
    }

    @Test
    fun `onReceive ignores unexpected action`() {
        receiver.onReceive(context, intent(action = "wrong.action", dmcUserId = "user123"))

        verify(exactly = 0) { preferences.edit() }
    }

    @Test
    fun `onReceive ignores empty dmc user ID`() {
        receiver.onReceive(context, intent(action = ACTION, dmcUserId = ""))

        verify(exactly = 0) { preferences.edit() }
    }

    @Test
    fun `onReceive ignores missing dmc user ID`() {
        receiver.onReceive(context, intent(action = ACTION, dmcUserId = null))

        verify(exactly = 0) { preferences.edit() }
    }

    @Test
    fun `onReceive handles null context`() {
        receiver.onReceive(null, intent(action = ACTION, dmcUserId = "user123"))

        verify(exactly = 0) { preferences.edit() }
    }

    @Test
    fun `onReceive handles null intent`() {
        receiver.onReceive(context, null)

        verify(exactly = 0) { preferences.edit() }
    }

    private fun intent(action: String?, dmcUserId: String?): Intent =
        mockk<Intent>().also { intent ->
            every { intent.action } returns action
            every { intent.getStringExtra(DMC_USER_ID) } returns dmcUserId
        }

    private companion object {
        const val ACTION = "webtrekk.android.sdk.integration.MappIntelligenceListener"
        const val DMC_USER_ID = "dmcUserId"
    }
}
