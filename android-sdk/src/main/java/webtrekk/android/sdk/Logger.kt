package webtrekk.android.sdk

interface Logger {

    enum class Level {
        /** no logs **/
        NONE,

        /** logs databases responses and network requests **/
        BASIC,

        /** logs everything **/
        ALL
    }
}
