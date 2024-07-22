package webtrekk.android.sdk.core

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
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

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
internal class SessionsImplTest {
    private lateinit var context: Context

    private val coroutineContext = Dispatchers.Unconfined

    private lateinit var webtrekk: Webtrekk

    private lateinit var database: WebtrekkDatabase

    private lateinit var webtrekkSharedPrefs: WebtrekkSharedPrefs

    @OverrideMockKs
    private lateinit var session: Sessions

    private lateinit var config: Config

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        config = mockk<WebtrekkConfiguration>(relaxed = true)
        mockkStatic(WebtrekkImpl::class)
        WebtrekkImpl.getInstance().init(context, config)
        database = Room.inMemoryDatabaseBuilder(
            context, WebtrekkDatabase::class.java
        ).build()
        webtrekkSharedPrefs = WebtrekkSharedPrefs(context)
        session = SessionsImpl(webtrekkSharedPrefs, database.trackRequestDao(), coroutineContext)
    }

    @After
    fun tearDown() {
        Webtrekk.getInstance().clearSdkConfig()
        unmockkAll()
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
    fun test_02_getEverId() = runBlocking {
        // session.setAnonymous(false)
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
        session.setEverId(userDefinedEverId, true, GenerationMode.USER_GENERATED)

        val mode: GenerationMode? = session.getEverIdMode()
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
    fun test_05_getAppFirstOpen() = runBlocking {
        val firstOpen = session.getAppFirstOpen()
        MatcherAssert.assertThat("App first open equals to 1", "1", equalTo(firstOpen))
        delay(1000)
        val secondOpen = session.getAppFirstOpen()
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
    fun test_17_everId_is_deleted_when_set_anonymous_tracking_to_enabled() = runBlocking {
        session.setEverId(userDefinedEverId, true, GenerationMode.USER_GENERATED)
        Webtrekk.getInstance().anonymousTracking(true, emptySet())
        val currentEverId = session.getEverId()
        MatcherAssert.assertThat("EverID must be set", currentEverId, nullValue())
    }

    @Test
    fun test_18_set_temporary_session_id() {
        Webtrekk.getInstance().anonymousTracking(true, emptySet())
        session.setTemporarySessionId("user-xyz")
        val temporarySessionId = session.getTemporarySessionId()
        MatcherAssert.assertThat(
            "Temporary session id must be set",
            temporarySessionId,
            equalTo("user-xyz")
        )
    }

    @Test
    fun test_19_auto_generate_everId_and_delete_temporary_session_id_when_anonymous_tracking_set_to_disabled() {
        session.setTemporarySessionId("user-xyz")
        Webtrekk.getInstance().anonymousTracking(false, emptySet())
        val temporarySessionId = session.getTemporarySessionId()
        val everId = session.getEverId()
        MatcherAssert.assertThat("Temporary session id is null", temporarySessionId, nullValue())
        MatcherAssert.assertThat("EverId is not null", everId, notNullValue())
    }
}