package webtrekk.android.sdk

interface Logger {

    enum class Level {
        NONE,

        BASIC,
    }

    fun info(message: String)

    fun debug(message: String)

    fun error(message: String)
}
