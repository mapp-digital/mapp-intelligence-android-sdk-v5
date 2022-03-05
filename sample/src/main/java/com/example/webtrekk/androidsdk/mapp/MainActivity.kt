package com.example.webtrekk.androidsdk.mapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appoxee.Appoxee
import com.appoxee.Appoxee.OnInitCompletedListener
import com.appoxee.GetAliasCallback
import com.appoxee.GetCustomAttributesCallback
import com.appoxee.internal.inapp.model.APXInboxMessage
import com.appoxee.internal.inapp.model.InAppCallback
import com.appoxee.internal.inapp.model.InAppInboxCallback
import com.appoxee.internal.inapp.model.InAppInboxCallback.onInAppInboxMessagesReceived
import com.appoxee.internal.inapp.model.InAppMessageDismissalCallback
import com.example.webtrekk.androidsdk.R
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class MainActivity : Activity(), OnInitCompletedListener {
    //This is a test commit
    private var pushEnabledSwitch: Switch? = null
    private val deviceRegistrationState: Switch? = null
    private var mMainLayout: LinearLayout? = null
    private var mTextView: TextView? = null
    lateinit var appoxee: Appoxee
    private var set_alias: EditText? = null
    private var set_tag: EditText? = null
    private var remove_tag: EditText? = null
    private var set_attribute_key: EditText? = null
    private var set_attribute_value: EditText? = null
    private var get_attribute: EditText? = null
    private var remove_attribute: EditText? = null
    private var get_custom_attributes: EditText? = null
    var textView: TextView? = null
    private var isInitSpinner = false
    private val runningQOrLater = Build.VERSION.SDK_INT >= 29
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        appoxee = Appoxee.instance()
        set_alias = findViewById(R.id.etxt_set_alias)
        set_tag = findViewById(R.id.etxt_set_tag)
        remove_tag = findViewById(R.id.etxt_remove_tag)
        set_attribute_key = findViewById(R.id.etxt_set_attribute_key)
        set_attribute_value = findViewById(R.id.etxt_set_attribute_value)
        get_attribute = findViewById(R.id.etxt_get_attribute)
        remove_attribute = findViewById(R.id.etxt_remove_attribute)
        get_custom_attributes = findViewById(R.id.etxt_get_custom_attributes)
        init()
        Appoxee.handleRichPush(this, intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Appoxee.handleRichPush(this, intent)
    }

    private fun init() {
        Appoxee.instance().addInitListener(this)
        val inAppCallback = InAppCallback()
        inAppCallback.addInAppMessageReceivedCallback { eventName, eventValue ->
            Log.d("  eventName = ", eventName)
            Log.d("  eventValue = ", eventValue)
            Toast.makeText(
                this@MainActivity,
                "KEY = " + eventName + "VALUE = " + eventValue,
                Toast.LENGTH_LONG
            ).show()
        }
        textView = findViewById<View>(R.id.textView2) as TextView
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val deviceToken = it.result ?: ""
                textView!!.text = deviceToken
                Log.d("token fcm", deviceToken)
            }
        }

        val inAppInboxCallback = InAppInboxCallback()
        inAppInboxCallback.addInAppInboxMessagesReceivedCallback(object :
            onInAppInboxMessagesReceived {
            override fun onInAppInboxMessages(richMessages: List<APXInboxMessage>) {

            }

            override fun onInAppInboxMessage(message: APXInboxMessage) {}
        })
        val inAppMessageDismissalCallback = InAppMessageDismissalCallback()
        inAppMessageDismissalCallback.addOnInAppMessageDismissalCallback { templateId, eventId, isSendStats ->
            Log.v(
                "MainActivity",
                "onInAppMessageDismissalCallback"
            )
        }
        mMainLayout = findViewById<View>(R.id.parentLayout) as LinearLayout
        mTextView = findViewById<View>(R.id.dummyText) as TextView

        findViewById<View>(R.id.device_info).setOnClickListener { //                SharedPreferences sp = getSharedPreferences("test", MODE_PRIVATE);
            Toast.makeText(
                this@MainActivity,
                "Push enabled: " + Appoxee.instance().isPushEnabled,
                Toast.LENGTH_SHORT
            ).show()
        }
        findViewById<View>(R.id.get_alias).setOnClickListener { //                Appoxee appoxee = Appoxee.instance();
//                Set<String> tags = appoxee.getTags();
//                Log.d("APX", "tags: " + new Gson().toJson(tags));
            val getAlias = alias
            createBuilder("", getAlias)
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", getAlias)
            clipboard.setPrimaryClip(clip)
        }
        findViewById<View>(R.id.get_deviceId).setOnClickListener {
            val deviceId =
                Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            createBuilder("", deviceId)
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", deviceId)
            clipboard.setPrimaryClip(clip)
        }
        findViewById<View>(R.id.btn_set_alias).setOnClickListener {
            appoxee.alias = set_alias!!.text.toString()
            createBuilder("New alias", "Added alias: " + set_alias!!.text)
            set_alias!!.setText("")
        }
        findViewById<View>(R.id.second_activity).setOnClickListener {
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
            Toast.makeText(this@MainActivity, "New activity opened", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.geo_fencing).setOnClickListener { startGeo() }
        findViewById<View>(R.id.stop_geo_fencing).setOnClickListener { stopGeoFencing() }
        pushEnabledSwitch = findViewById<View>(R.id.push_enabled) as Switch
        pushEnabledSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            val status = Appoxee.instance().setPushEnabled(isChecked)
        }
        findViewById<View>(R.id.buttonDeviceInfo).setOnClickListener {
            Appoxee.instance().getDeviceInfoDMC()
            val s = appoxee.deviceInfo.toString()
            createBuilder("", s)
        }
        findViewById<View>(R.id.dmcCallInApp).setOnClickListener {
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_open")
        }
        findViewById<View>(R.id.inappModalType).setOnClickListener {
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_feedback")
        }
        findViewById<View>(R.id.inappBannerType).setOnClickListener {
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_discount")
        }
        findViewById<View>(R.id.inappAppPromo).setOnClickListener {
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_promo")
        }
        findViewById<View>(R.id.multipleMessages).setOnClickListener {
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_welcome")
        }
        findViewById<View>(R.id.fcm_token).setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", textView!!.text.toString())
            clipboard.setPrimaryClip(clip)
        }
        findViewById<View>(R.id.btn_get_tags).setOnClickListener {
            val tags = appoxee.tags
            val s = StringBuilder("")
            for (tag in tags) {
                s.append("\n")
                    .append(tag)
            }
            createBuilder("All tags", s.toString())
        }
        findViewById<View>(R.id.btn_set_tag).setOnClickListener {
            if (set_tag!!.text.length == 0) {
                Toast.makeText(this@MainActivity, "Please, filled field above", Toast.LENGTH_SHORT)
                    .show()
            } else {
                appoxee.addTag(set_tag!!.text.toString())
                createBuilder("Set tag", "Setted tag: " + set_tag!!.text)
                set_tag!!.setText("")
            }
        }
        findViewById<View>(R.id.btn_remove_tag).setOnClickListener {
            if (remove_tag!!.text.length == 0) {
                Toast.makeText(this@MainActivity, "Please, filled field above", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val status = appoxee.removeTag(remove_tag!!.text.toString())
                createBuilder("Remove tag", "Removed tag: " + remove_tag!!.text)
                remove_tag!!.setText("")
            }
        }
        findViewById<View>(R.id.btn_set_attribute).setOnClickListener {
            if (set_attribute_key!!.text.length == 0 || set_attribute_value!!.text.length == 0) {
                Toast.makeText(
                    this@MainActivity,
                    "Please, fill key and value field above",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                appoxee.setAttribute(
                    set_attribute_key!!.text.toString(),
                    set_attribute_value!!.text.toString()
                )
                createBuilder(
                    "Set attribute",
                    "Added attribute: " + set_attribute_key!!.text + " - " + set_attribute_value!!.text.toString()
                )
                set_attribute_key!!.setText("")
                set_attribute_value!!.setText("")
            }
        }
        findViewById<View>(R.id.btn_get_attribute).setOnClickListener {
            val s = appoxee.getAttributeStringValue(get_attribute!!.text.toString())
            if (s == null || s == "") {
                Toast.makeText(
                    this@MainActivity,
                    "Doesn't exist this attribute",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                createBuilder("Get attribute", "Get attribute: $s")
                get_attribute!!.setText("")
            }
        }
        findViewById<View>(R.id.btn_remove_attribute).setOnClickListener {
            val s = appoxee.getAttributeStringValue(remove_attribute!!.text.toString())
            if (s == null || s == "") {
                Toast.makeText(
                    this@MainActivity,
                    "Doesn't exist this attribute",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                appoxee.removeAttribute(remove_attribute!!.text.toString())
                createBuilder("Remove attribute", "Removed attribute: $s")
                remove_attribute!!.setText("")
            }
        }
        findViewById<View>(R.id.get_custom_attributes).setOnClickListener {
            if (get_custom_attributes!!.text.length == 0) {
                Toast.makeText(
                    this@MainActivity,
                    "Please, fill text field above",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val getText = get_custom_attributes!!.text.toString().replace("\\s".toRegex(), "")
                val customAttributes = getText.split(",".toRegex()).toTypedArray()
                appoxee.getCustomAttributes(
                    true,
                    Arrays.asList(*customAttributes),
                    object : GetCustomAttributesCallback {
                        override fun onSuccess(customAttributes: Map<String, String>) {
                            runOnUiThread {
                                val stringBuilder = StringBuilder()
                                val iterator = customAttributes.entries.iterator()
                                var i = 1
                                while (iterator.hasNext()) {
                                    val entry = iterator.next()
                                    stringBuilder.append('(')
                                    stringBuilder.append(i)
                                    stringBuilder.append(") ")
                                    stringBuilder.append(entry.key)
                                    stringBuilder.append(": ")
                                    stringBuilder.append(entry.value)
                                    i++
                                    if (iterator.hasNext()) stringBuilder.append("\n")
                                }
                                createBuilder("", stringBuilder.toString())
                                val clipboard =
                                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                    ClipData.newPlainText("label", customAttributes.toString())
                                clipboard.setPrimaryClip(clip)
                            }
                        }

                        override fun onError(exception: String) {
                            runOnUiThread {
                                createBuilder("", exception)
                                val clipboard =
                                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("label", exception)
                                clipboard.setPrimaryClip(clip)
                            }
                        }
                    })
            }
        }
        findViewById<View>(R.id.get_alias_from_server).setOnClickListener {
            appoxee!!.getAliasFromServer(true, object : GetAliasCallback {
                override fun onSuccess(alias: String) {
                    runOnUiThread {
                        createBuilder("", alias)
                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("label", alias)
                        clipboard.setPrimaryClip(clip)
                    }
                }

                override fun onError(exception: String) {
                    runOnUiThread {
                        createBuilder("", exception)
                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("label", exception)
                        clipboard.setPrimaryClip(clip)
                    }
                }
            })
        }
        findViewById<View>(R.id.btn_remove_badge_number).setOnClickListener {
            Appoxee.removeBadgeNumber(this@MainActivity.applicationContext)
            createBuilder("Remove badge", "All badges deleted")
        }
        findViewById<View>(R.id.btn_logout_with_optout).setOnClickListener {
            Appoxee.instance().logOut(application, false)
        }
        findViewById<View>(R.id.btn_logout).setOnClickListener {
            Appoxee.instance().logOut(application, true)
        }
    }

    override fun onInitCompleted(successful: Boolean, failReason: Exception) {
        Log.i("APX", "init completed listener - MainActivity")
        runOnUiThread {
            pushEnabledSwitch!!.isChecked = Appoxee.instance().isPushEnabled
            Appoxee.instance().triggerDMCCallInApp(this@MainActivity, "app_open")
            mTextView!!.text = "App is initialized, Please wait while we display messages..."
        }
    }


    private fun startGeo() {
        if (isGeoPermissionGranted) {
            Appoxee.instance().startGeoFencing()
        } else {
            if (runningQOrLater) {
                askForGeoPermissionWithBackgroundLocation()
            } else {
                askForGeoPermission()
            }
        }
    }

    private val isGeoPermissionGranted: Boolean
        private get() = if (runningQOrLater) {
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun askForGeoPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_ACCESS_FINE_LOCATION
        )
    }

    private fun askForGeoPermissionWithBackgroundLocation() {
        val permissionAccessFineLocationApproved =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        if (permissionAccessFineLocationApproved) {
            val backgroundLocationPermissionApproved = (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)
            if (backgroundLocationPermissionApproved) {
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    MY_PERMISSIONS_ACCESS_FINE_AND_BACKGROUND_LOCATION
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_ACCESS_FINE_AND_BACKGROUND_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (MY_PERMISSIONS_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Appoxee.instance().startGeoFencing()
                Log.w("MainActivity", "startGeoFencing()")
            } else {
                Log.w("MainActivity", "Geo permission not granted")
            }
        } else if (MY_PERMISSIONS_ACCESS_FINE_AND_BACKGROUND_LOCATION == requestCode) {
            if (grantResults.size > 0 && permissions.size == 1 && permissions[0].contains("android.permission.ACCESS_BACKGROUND_LOCATION")
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Appoxee.instance().startGeoFencing()
                Log.w("MainActivity", "startGeoFencing()with background")
            } else if (grantResults.size > 0 && permissions.size == 1 && permissions[0].contains("android.permission.ACCESS_BACKGROUND_LOCATION")
                && grantResults[0] == PackageManager.PERMISSION_DENIED
            ) {
                Appoxee.instance().startGeoFencing()
                Log.w("MainActivity", "startGeoFencing()with foreground")
            } else if (grantResults.size > 0 && permissions.size == 2 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Appoxee.instance().startGeoFencing()
                Log.w("MainActivity", "startGeoFencing() with background")
            } else if (grantResults.size > 0 && permissions.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Appoxee.instance().startGeoFencing()
                Log.w("MainActivity", "startGeoFencing() with foreground")
            } else {
                Log.w("MainActivity", "Geo permission not granted")
            }
        } else {
            Log.w("Main Activity", "some other permission requested? (not geo)")
        }
    }

    private fun stopGeoFencing() {
        Appoxee.instance().stopGeoFencing()
    }

    private fun createBuilder(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setMessage(message).setTitle(title)
        val dialog = alertDialog.create()
        dialog.show()
    }

    val alias: String
        get() = appoxee.alias

    fun getAttribute(attr: String?): String {
        var attribute = appoxee.getAttributeStringValue(attr)
        if (attribute == null || attribute == "") {
            attribute = ""
        } else {
            get_attribute!!.setText("")
        }
        return attribute
    }

    fun removeTag(tag: String?) {
        appoxee.removeTag(tag)
    }


    companion object {
        private const val MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1 shl 3
        private const val MY_PERMISSIONS_ACCESS_FINE_AND_BACKGROUND_LOCATION = 1 shl 4
    }
}