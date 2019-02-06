package webtrekk.android.sdk.api

internal enum class UrlParams(val value: String) {

    WEBTREKK_PARAM("p"),

    EVER_ID("eid"),

    FORCE_NEW_SESSION("fns"),

    APP_FIRST_START("one"),

    TIME_ZONE("tz"),

    USER_AGENT("X-WT-UA"),

    LANGUAGE("la"),

    EVENT_NAME("ct")
}
