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

package webtrekk.android.sdk.core

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import webtrekk.android.sdk.Logger

/**
 * Webtrekk simple logger.
 */
internal class WebtrekkLogger(level: Logger.Level) :
    Logger {

    private var basicMessage: String? = null
    private var logger: Logger.Level = Logger.Level.NONE
    private val _dateFormat = SimpleDateFormat.getDateTimeInstance()
    private val date
        inline get() = _dateFormat.format(Date())

    init {
        logger = level
        basicMessage = when (level) {
            Logger.Level.NONE -> null
            Logger.Level.BASIC -> date.toString()
        }
    }

    override fun setLevel(logLevel: Logger.Level) {
        logger = logLevel
    }

    private fun getBasicMessage() {
        basicMessage = when (logger) {
            Logger.Level.NONE -> null
            Logger.Level.BASIC -> date.toString()
        }
    }

    override fun info(message: String) {
        getBasicMessage()
        basicMessage?.let {
            Log.i(TAG, "$basicMessage -> $message")
        }
    }

    override fun debug(message: String) {
        getBasicMessage()
        basicMessage?.let {
            Log.d(TAG, "$basicMessage -> $message")
        }
    }

    override fun warn(message: String) {
        getBasicMessage()
        basicMessage?.let {
            Log.w(TAG, "$basicMessage -> $message")
        }
    }

    override fun error(message: String) {
        getBasicMessage()
        basicMessage?.let {
            Log.wtf(TAG, "$basicMessage -> $message")
        }
    }

    companion object {
        private const val TAG = "webtrekk"
    }
}
