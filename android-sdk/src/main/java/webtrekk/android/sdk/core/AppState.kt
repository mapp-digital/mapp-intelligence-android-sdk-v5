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

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

/**
 * An abstract class, used for listening to the events coming from [LifecycleWrapper] and propagate their events data.
 */
internal abstract class AppState<T : Any> : LifecycleWrapper() {

    open lateinit var sessions: Sessions

    /**
     * The callback which is used in children classes whenever they receive an event and propagate the values to the lambda.
     */
    lateinit var lifecycleReceiver: LifecycleReceiver<T>

    /**
     * Register to the application life cycle, to auto track the current activities and fragments.
     *
     * @param context the application context.
     * @param onReceive the lambda that will be used on the caller whenever there is an event comes from the lifecycle and the children propagate that event.
     */
    inline fun listenToLifeCycle(context: Context, crossinline onReceive: (T) -> Unit) {
        (context as? Application)?.registerActivityLifecycleCallbacks(this)?.let {
            lifecycleReceiver = object : LifecycleReceiver<T> {
                override fun onLifecycleEventReceived(
                    event: T,
                    trackingEnabled: Boolean
                ) {
                    if (trackingEnabled)
                        onReceive(event)
                }
            }
        }
    }

    /**
     * Used to unregister the activity lifecycle callbacks.
     *
     * @param context the application context.
     */
    fun disable(context: Context) {
        (context as? Application)?.unregisterActivityLifecycleCallbacks(this)
    }
}

/**
 * An interface used as a receiver, so whenever the parent class receives an event from [LifecycleWrapper], the [onLifecycleEventReceived] is used to pass that event to the caller.
 */
internal interface LifecycleReceiver<in T> {

    /**
     * A callback used to pass the events coming from receiver to be used in the caller side.
     *
     * @param event
     */
    fun onLifecycleEventReceived(
        event: T,
        trackingEnabled: Boolean = true
    )
}

/**
 * A wrapper class merging the activity lifecycle callbacks and fragments lifecycle callbacks in one class.
 */
internal open class LifecycleWrapper :
    Application.ActivityLifecycleCallbacks,
    FragmentManager.FragmentLifecycleCallbacks() {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
            this,
            true
        )
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(
            this
        )
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(
            this
        )
    }
}
