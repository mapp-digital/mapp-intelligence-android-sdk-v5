package webtrekk.android.sdk.data.model

enum class GenerationMode(val mode: Int) {
    AUTO_GENERATED(0),
    USER_GENERATED(1);

    companion object {
        fun value(mode: Int): GenerationMode {
            return when (mode) {
                0 -> AUTO_GENERATED
                else -> USER_GENERATED
            }
        }
    }
}