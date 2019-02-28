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

package webtrekk.android.sdk

/**
 * A logger interface that should be implemented in the library, to print the logs.
 *
 * The concrete implementation [WebtrekkLogger].
 */
interface Logger {

    /**
     * Enum class represents the log level that will be used in the lib.
     *
     * You can customize the log level in the configurations [WebtrekkConfiguration.logLevel].
     * The default log level in the configuration [DefaultConfiguration.LOG_LEVEL_VALUE].
     */
    enum class Level {
        /**
         * AT this level, will not print any logs.
         */
        NONE,

        /**
         * The basic level of logging, including "webtrekk" tag, date and time of the log message.
         */
        BASIC,
    }

    fun info(message: String)

    fun debug(message: String)

    fun warn(message: String)

    fun error(message: String)
}
