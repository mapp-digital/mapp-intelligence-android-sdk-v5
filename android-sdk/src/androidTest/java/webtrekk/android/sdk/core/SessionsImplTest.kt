package webtrekk.android.sdk.core

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import webtrekk.android.sdk.Config
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration
import webtrekk.android.sdk.data.WebtrekkDatabase
import webtrekk.android.sdk.data.WebtrekkSharedPrefs
import webtrekk.android.sdk.data.model.GenerationMode
import webtrekk.android.sdk.userDefinedEverId
import webtrekk.android.sdk.util.appFirstOpen

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
internal class SessionsImplTest {
    private val context = InstrumentationRegistry.getInstrumentation().context

    private val coroutineContext = Dispatchers.Unconfined

    private val database = Room.inMemoryDatabaseBuilder(
        context, WebtrekkDatabase::class.java
    ).build()

    private lateinit var webtrekkSharedPrefs: WebtrekkSharedPrefs

    private lateinit var session: Sessions

    private val config: Config = mockk<WebtrekkConfiguration>(relaxed = true)

    @Before
    fun setUp() {
        webtrekkSharedPrefs = WebtrekkSharedPrefs(context)
        session = SessionsImpl(
            webtrekkSharedPrefs = webtrekkSharedPrefs,
            database.trackRequestDao(),
            coroutineContext
        )

        Webtrekk.getInstance().init(context, config)
    }

    @After
    fun tearDown() {
        Webtrekk.reset(context)
        webtrekkSharedPrefs.sharedPreferences.edit().clear().apply()
    }

    @Test
    fun test_01_setEverId() = runBlocking {
        session.setAnonymous(false)
        session.setEverId(userDefinedEverId, true, GenerationMode.USER_GENERATED)
        val sessionEverId = session.getEverId()
        MatcherAssert.assertThat(
            "User defined set ever id can be updated",
            userDefinedEverId,
            equalTo(sessionEverId)
        )
    }

    @Test
    fun test_02_getEverId() {
        session.setAnonymous(false)
        session.setEverId(userDefinedEverId, true, GenerationMode.USER_GENERATED)
        val currentEverId = session.getEverId()
        MatcherAssert.assertThat("EverID must be set", currentEverId, notNullValue())
        MatcherAssert.assertThat(
            "EverID has user defined value", currentEverId, equalTo(
                userDefinedEverId
            )
        )
    }

    @Test
    fun test_03_getEverIdMode() {
        session.setAnonymous(false)
        session.setEverId("2222", true, GenerationMode.USER_GENERATED)
        val mode = session.getEverIdMode()
        MatcherAssert.assertThat(
            "EverID mode is user defined",
            mode,
            equalTo(GenerationMode.USER_GENERATED)
        )
    }

    @Test
    fun test_04_getUserAgent() {
        val userAgent = session.getUserAgent()
        MatcherAssert.assertThat("User agent is not null", userAgent, notNullValue())
    }

    @Test
    fun test_05_getAppFirstOpen() = runBlocking{
        val firstOpen = session.getAppFirstOpen()
        MatcherAssert.assertThat("App first open equals to 1", "1", equalTo(firstOpen))

        delay(1000)
        val secondOpen=session.getAppFirstOpen()
        MatcherAssert.assertThat("Every next open should return 0", "0", equalTo(secondOpen))
    }

    @Test
    fun test_06_startNewSession() {
        session.startNewSession()
        val sessionValue = session.getCurrentSession()
        MatcherAssert.assertThat(
            "New session is started - value is equal to 1",
            sessionValue,
            equalTo("1")
        )
    }

    @Test
    fun test_07_getCurrentSession() {
        session.getCurrentSession()
        val sessionValue = session.getCurrentSession()
        MatcherAssert.assertThat(
            "When called second, and every other time - value is equal to 0",
            sessionValue,
            equalTo("0")
        )
    }

    @Test
    fun test_08_test_when_user_optOut() {
        session.optOut(true)
        val isOptOut = session.isOptOut()
        MatcherAssert.assertThat("User is opt out", isOptOut, equalTo(true))
    }

    @Test
    fun test_09_test_when_user_optIn() {
        session.optOut(false)
        val isOptOut = session.isOptOut()
        MatcherAssert.assertThat("User is opt in", isOptOut, equalTo(false))
    }

    @Test
    fun test_10_getUrlKey() {
    }

    @Test
    fun test_11_setUrl() {
    }

    @Test
    fun test_12_isAnonymous() {
    }

    @Test
    fun test_13__enable_anonymous_tracking() {
        session.setAnonymous(true)
        val isAnonymous = session.isAnonymous()
        MatcherAssert.assertThat(
            "When anonymous tracking enabled, isAnonymous must be true",
            isAnonymous,
            equalTo(true)
        )
    }

    @Test
    fun test_14_disable_anonymous_tracking() {
        session.setAnonymous(false)
        val isAnonymous = session.isAnonymous()
        MatcherAssert.assertThat(
            "When anonymous tracking disabled, isAnonymous must be false",
            isAnonymous,
            equalTo(false)
        )
    }

    @Test
    fun test_15_isAnonymousParam() {
        val params = setOf("paramOne", "paramTwo")
        session.setAnonymous(true)
        session.setAnonymousParam(params)
        val currentParams = session.isAnonymousParam()
        MatcherAssert.assertThat(
            "Set of anonymous params must be equals to",
            currentParams,
            equalTo(params)
        )
    }

    @Test
    fun test_16_setAnonymousParam() {
        val params = setOf("paramOne", "paramTwo")
        session.setAnonymous(false)
        session.setAnonymousParam(params)
        val currentParams = session.isAnonymousParam()
        MatcherAssert.assertThat(
            "Set of anonymous params must be equals to",
            currentParams,
            equalTo(params)
        )
    }

    @Test
    fun test_17_setTemporarySessionId() {
    }

    @Test
    fun test_18_getTemporarySessionId() {
    }
}