package webtrekk.android.sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal interface LifecycleReceiver<in T> {

    fun onEventReceived(event: T)
}

internal abstract class AppState<T : Any> : LifecycleWrapper() {

    protected lateinit var lifecycleReceiver: LifecycleReceiver<T>

    inline fun startAutoTrack(context: Context, crossinline onReceive: (T) -> Unit) {
        (context as? Application)?.registerActivityLifecycleCallbacks(this)
        lifecycleReceiver = object : LifecycleReceiver<T> {
            override fun onEventReceived(event: T) {
                onReceive(event)
            }
        }
    }

    // check if possible to use suspend
    suspend fun startAutoTrack(context: Context): T = suspendCancellableCoroutine { cont ->
        (context as? Application)?.registerActivityLifecycleCallbacks(this)
        lifecycleReceiver = object : LifecycleReceiver<T> {
            override fun onEventReceived(event: T) {
                cont.resume(event)
            }
        }
    }

    fun disableAutoTrack(context: Context) {
        (context as? Application)?.unregisterActivityLifecycleCallbacks(this)
    }
}

internal open class LifecycleWrapper :
    Application.ActivityLifecycleCallbacks,
    FragmentManager.FragmentLifecycleCallbacks() {

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(
            this
        )
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(
            this
        )
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        (activity as? AppCompatActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
            this,
            true
        )
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentCreated(fm, f, savedInstanceState)
    }
}
