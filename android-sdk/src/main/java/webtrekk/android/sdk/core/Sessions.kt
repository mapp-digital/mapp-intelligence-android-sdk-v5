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

/**
 * An interface contains all the methods that are used by SharedPreferences in the SDK.
 */
internal interface Sessions {

    /**
     * Generate and set the ever id that uniquely associated per user.
     */
    fun setEverId()

    /**
     * Set the ever id provided by the client.
     */
    fun setEverId(everId: String)

    /**
     * Returns the ever id which is stored in the SharedPreferences.
     */
    fun getEverId(): String

    /**
     * Returns user agent created for the mobile device.
     */
    fun getUserAgent(): String

    /**
     * Returns "1" if the app is first used by the SDK, after that always returns "0".
     */
    fun getAppFirstOpen(): String

    /**
     * Starting a new session when the app is freshly opened, setting its value to "1".
     */
    fun startNewSession()

    /**
     * Returns "1: when the app is freshly opened indicating that that app has a new session, otherwise returns "0".
     */
    fun getCurrentSession(): String

    /**
     * Set the opt out value in SharedPreferences, true means to opt out, false to stop opting out.
     *
     * @param value the opt out is true, false to stop opt out.
     */
    fun optOut(value: Boolean)

    /**
     * Returns true if opt out is active, false otherwise.
     */
    fun isOptOut(): Boolean

    /**
     * Returns true if the app is updated, by comparing the current app version number to the previously stored app version number. If the app is updated, returns true and replace the old app version number with the current new one. Return false if the app is not updated.
     *
     * @param currentAppVersion the current app version number.
     */
    fun isAppUpdated(currentAppVersion: String): Boolean

    /**
     * Set the ever id and one parameter values from sdk version 4 SharedPreferences, if the value exists. Whether the migration succeed or not, migration is only tried once and stored as boolean flag in SharedPreferences.
     */
    fun migrate()
}