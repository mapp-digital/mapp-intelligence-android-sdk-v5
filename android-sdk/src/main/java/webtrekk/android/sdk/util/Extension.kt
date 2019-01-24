package webtrekk.android.sdk.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.fragment.app.Fragment
import webtrekk.android.sdk.data.model.TrackRequest

internal inline fun <reified T : Any> T?.nullOrEmptyIsError(propertyName: T): T {
    when (this) {
        is String? -> if (this.isNullOrBlank())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")

        is List<*>? -> if (this.isNullOrEmpty())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")
    }

    return this as T
}

internal fun <T : Any> List<T>?.validateList(propertyName: Any): List<T> {
    this.nullOrEmptyIsError(propertyName)

    this?.forEach {
        it.nullOrEmptyIsError(propertyName)
    }

    return this as List<T>
}

internal val Context.isPortrait
    inline get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

internal fun Context.resolution(): String {
    val displayMetrics = DisplayMetrics()
    val windowManager =
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return String.format("%sx%s", displayMetrics.widthPixels, displayMetrics.heightPixels)
}

internal fun Activity.toTrackRequest(): TrackRequest = TrackRequest(name = this.localClassName, screenResolution = this.resolution())

internal fun Fragment.toTrackRequest(): TrackRequest = TrackRequest(name = this.toString(), screenResolution = this.context?.resolution())
