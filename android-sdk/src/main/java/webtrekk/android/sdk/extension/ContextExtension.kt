package webtrekk.android.sdk.extension

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager

internal val Context.isPortrait
    inline get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

internal fun Context.resolution(): String {
    val displayMetrics = DisplayMetrics()
    val windowManager =
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return String.format("%sx%s", displayMetrics.widthPixels, displayMetrics.heightPixels)
}
