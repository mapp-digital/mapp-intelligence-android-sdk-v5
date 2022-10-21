package com.example.webtrekk.androidsdk

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

/**
 * [PermissionsManager] deals with android permissions
 */
class PermissionsManager : DefaultLifecycleObserver {
    interface Callback {
        fun onPermissionsResult(results: Map<String, Int>)
    }

    private var lifecycle: Lifecycle?
    private var context: Context?
    private var permissionCallback: Callback?
    private var requestPermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var resultCaller: ActivityResultCaller

    /**
     * Primary constructor for initializing instance with a lifecycle of a [FragmentActivity]
     */
    constructor(
        activity: FragmentActivity,
        permissionCallback: Callback
    ) {
        resultCaller = activity
        context = activity
        this.permissionCallback = permissionCallback
        lifecycle = activity.lifecycle
        lifecycle!!.addObserver(this)
    }

    /**
     * Secondary constructor for getting activity from a fragment and calling primary constructor
     */
    constructor(
        fragment: Fragment,
        permissionCallback: Callback
    ) {
        resultCaller = fragment
        context = fragment.context
        this.permissionCallback = permissionCallback
        lifecycle = fragment.lifecycle
        lifecycle!!.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        registerPermissionsResultCallback()
    }

    /**
     * Create requestPermissionLauncher instance. It holds permissionResultCallback.
     */
    private fun registerPermissionsResultCallback() {
        /*
         * Use of a new Permission API for requesting runtime permissions
         * Attach forActivityResult for passed activity
         */
        requestPermissionLauncher = resultCaller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback { result: Map<String, Boolean> ->
                /*
                 * user choice on permission dialog is accepted
                 * process acceptance of a permissions and forward result via callback
                 */
                val results = processResult(result)
                permissionCallback?.onPermissionsResult(results)
            } as ActivityResultCallback<Map<String, Boolean>>
        )
    }

    /**
     * Helper method for checking status of a single permission
     */
    private fun checkPermission(permission: String): Map<String, Int> {
        return checkPermissions(listOf(permission))
    }

    /**
     * Checks if permissions are [.PERMISSION_GRANTED] or [.PERMISSION_DENIED]
     *
     * @param permissions list of danger permissions to check status
     * @return Map of permissions with statuses
     */
    private fun checkPermissions(permissions: List<String>?): Map<String, Int> {
        val results: MutableMap<String, Int> = HashMap()
        if (permissions == null || permissions.isEmpty()) {
            return results
        }
        if (osVersion < Build.VERSION_CODES.M) {
            // if Android version is bellow Android 6 (API 23) runtime permissions not exists, so we mark all as granted.
            for (permission in permissions) {
                results[permission] = PackageManager.PERMISSION_GRANTED
            }
        } else {
            // Android version 6 and higher requires runtime permissions
            for (permission in permissions) {
                if (context != null) {
                    val result = ContextCompat.checkSelfPermission(context!!, permission)
                    results[permission] = result
                }
            }
        }
        return results
    }

    /**
     * Helper method for requesting single permission
     */
    fun requestPermission(permission: String) {
        requestPermissions(listOf(permission))
    }

    /**
     * Request runtime permissions
     *
     * @param permissions list of danger permissions to request access
     */
    fun requestPermissions(permissions: List<String>) {
        if (osVersion >= Build.VERSION_CODES.M) {
            // store list of non granted permissions
            val notGrantedPermissions: MutableList<String> = ArrayList()

            /*
             * Checks if permissions are already granted and filter non granted permissions from required permissions,
             * and populate into nonGrantedPermissions
             */for (permission in permissions) {
                val granted = ContextCompat.checkSelfPermission(context!!, permission)
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    notGrantedPermissions.add(permission)
                }
            }

            // if non granted permissions exists, request those permissions
            if (notGrantedPermissions.isNotEmpty()) {
                requestPermissionLauncher!!.launch(notGrantedPermissions.toTypedArray())
                return
            }
        }

        // if Android version not support RUNTIME permissions, return all permissions as granted.
        val results = checkPermissions(permissions)
        permissionCallback?.onPermissionsResult(results)
    }

    /**
     * Process result of a requested permissions
     *
     * @param permissions returned permission result from activityCallback
     * @return mapped permissions with three state {[.PERMISSION_GRANTED], {[.PERMISSION_DENIED], {[.PERMISSION_PERMANENTLY_DENIED]}}}
     */
    private fun processResult(permissions: Map<String, Boolean>): Map<String, Int> {
        val results: MutableMap<String, Int> = HashMap()
        for ((key, value) in permissions) {
            var granted = if (value) PERMISSION_GRANTED else PERMISSION_DENIED

            // shouldShowRequestPermissionRationale is only available from Android M (23)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (granted == PERMISSION_DENIED) {
                    // if permission is denied, and showRationale is false, then permission is permanently denied.
                    var showRationale = false
                    if (resultCaller is AppCompatActivity) {
                        showRationale =
                            (resultCaller as AppCompatActivity).shouldShowRequestPermissionRationale(
                                key
                            )
                    } else if (resultCaller is Fragment) {
                        val activity = (resultCaller as Fragment).activity
                        if (activity != null) showRationale =
                            activity.shouldShowRequestPermissionRationale(
                                key
                            )
                    }
                    if (!showRationale) granted = PERMISSION_PERMANENTLY_DENIED
                }
            }
            results[key] = granted
        }
        return results
    }

    /**
     * Checks if foreground location permission is granted [Manifest.permission.ACCESS_FINE_LOCATION] or [Manifest.permission.ACCESS_COARSE_LOCATION]
     *
     * @return {[.PERMISSION_GRANTED] if ether permission is granted, otherwise returns {[.PERMISSION_DENIED]}}
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun permissionForegroundLocationStatus(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PERMISSION_GRANTED
        } else {
            val permissions = checkPermissions(
                Arrays.asList(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            var fineLocation = PERMISSION_DENIED
            var coarseLocation = PERMISSION_DENIED
            for ((key, value) in permissions) {
                if (Manifest.permission.ACCESS_FINE_LOCATION == key) fineLocation = value
                if (Manifest.permission.ACCESS_COARSE_LOCATION == key) coarseLocation = value
            }
            if (fineLocation == PERMISSION_GRANTED || coarseLocation == PERMISSION_GRANTED) {
                PERMISSION_GRANTED
            } else {
                PERMISSION_DENIED
            }
        }
    }

    /**
     * Checks if background location permission is granted or not. Can't determine if permanently denied.
     *
     * @return {[.PERMISSION_GRANTED] or {[.PERMISSION_DENIED]}}
     */
    fun permissionBackgroundLocationStatus(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) permissionForegroundLocationStatus() else {
            val permissions = checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            var backgroundLocation = PERMISSION_DENIED
            for ((key, value) in permissions) {
                if (Manifest.permission.ACCESS_BACKGROUND_LOCATION == key) backgroundLocation =
                    value
            }
            backgroundLocation
        }
    }

    /**
     * Show alert dialog to ask a user to manually open settings page and to grant required permissions
     *
     * @param listener callback to be executed when positive button is clicked
     */
    fun showQuestionDialog(
        title: String?,
        question: String?,
        listener: DialogInterface.OnClickListener?
    ) {
        val positiveButtonText = context!!.getString(android.R.string.ok)
        val negativeButtonText = context!!.getString(android.R.string.cancel)
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle(title)
            .setMessage(question)
            .setPositiveButton(positiveButtonText, listener)
            .setNegativeButton(negativeButtonText, null)
            .create()
            .show()
    }

    /**
     * Open system settings page for application.
     * Used to navigate a user to the system settings to manually grant required permissions.
     */
    fun openPermissionSettings() {
        val uri = Uri.fromParts("package", context!!.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        intent.data = uri
        context!!.startActivity(intent)
    }

    /**
     * Release resources
     */
    override fun onDestroy(owner: LifecycleOwner) {
        lifecycle!!.removeObserver(this)
        requestPermissionLauncher!!.unregister()
        context = null
        lifecycle = null
        permissionCallback = null
    }

    companion object {
        const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
        const val PERMISSION_DENIED = PackageManager.PERMISSION_DENIED
        const val PERMISSION_PERMANENTLY_DENIED = 1
        val osVersion = Build.VERSION.SDK_INT
    }
}