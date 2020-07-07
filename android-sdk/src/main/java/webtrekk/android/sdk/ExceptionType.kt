package webtrekk.android.sdk

enum class ExceptionType(val type: String) {
    NONE("0"),
    UNCAUGHT("1"),
    CAUGHT("2"),
    CUSTOM("3"),
    ALL("4"),
    UNCAUGHT_AND_CUSTOM("5"),
    UNCAUGHT_AND_CAUGHT("6"),
    CUSTOM_AND_CAUGHT("7")
}