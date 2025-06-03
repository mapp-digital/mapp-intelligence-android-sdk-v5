/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

@file:Suppress("DEPRECATION")

package webtrekk.android.sdk.extension

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * [Context] extension functions.
 */
val Context.isPortrait
    inline get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Context.resolution(): String {
    val displayMetrics = DisplayMetrics()
    val windowManager =
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return String.format("%sx%s", displayMetrics.widthPixels, displayMetrics.heightPixels)
}

val Context.appVersionName: String
    inline get() = this.packageManager.getPackageInfo(this.packageName, 0).versionName ?: "1.0.0"

val Context.appVersionCode: String
    inline get() {
        return if (Build.VERSION.SDK_INT >= 28) {
            this.packageManager.getPackageInfo(this.packageName, 0).longVersionCode.toString()
        } else {
            this.packageManager.getPackageInfo(this.packageName, 0).versionCode.toString()
        }
    }
