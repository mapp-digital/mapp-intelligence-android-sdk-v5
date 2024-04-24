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

package com.example.webtrekk.androidsdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appoxee.Appoxee
import com.example.webtrekk.androidsdk.databinding.ActivityMainBinding
import com.example.webtrekk.androidsdk.mapp.PageRequestsActivity
import com.example.webtrekk.androidsdk.mapp.UserMatchingActivity
import com.example.webtrekk.androidsdk.tracking.OrdersTrackingActivity
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.TrackPageDetail
import webtrekk.android.sdk.TrackParams
import webtrekk.android.sdk.Webtrekk

@Suppress("UNCHECKED_CAST")
@TrackPageDetail(
    contextName = "Main Page",
    trackingParams = [TrackParams(
        paramKey = Param.PAGE_PARAMS.INTERNAL_SEARCH,
        paramVal = "search"
    )]
)
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = Prefs(this)

        title = "${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}"
/*
        val stringIds = BuildConfig.TRACK_IDS
        val domain = BuildConfig.DOMEIN
        val elements: List<String> = stringIds.split(",")
        val webtrekkConfiguration=WebtrekkConfiguration.Builder(elements,domain).build()

        Webtrekk.getInstance().init(this, webtrekkConfiguration)
*/

        binding.buttonPrintActiveConfig.setOnClickListener {
            val activeConfig = Webtrekk.getInstance().getCurrentConfiguration().toString()
            Log.w(this::class.java.name,"ACTIVE CONFIG: $activeConfig")
        }

        binding.buttonSendRequests.setOnClickListener {
            Webtrekk.getInstance().sendRequestsNowAndClean()
        }

        binding.startDetailsActivity.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }

        binding.webViewActivity.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

        binding.buttonCampaignTest.setOnClickListener {
            startActivity(Intent(this, CampaignActivity::class.java))
        }

        binding.formActivity.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        binding.startUserMatchingActivity.setOnClickListener {
            val intent = Intent(this, UserMatchingActivity::class.java)
            startActivity(intent)
        }

        binding.crashActivity.setOnClickListener {
            val intent = Intent(this, CrashActivity::class.java)
            startActivity(intent)
        }

        binding.videoActivity.setOnClickListener {
            val intent = Intent(this, MediaExample::class.java)
            startActivity(intent)
        }

        binding.button4.setOnClickListener {
            val intent = Intent(this, SettingsExample::class.java)
            startActivity(intent)
        }

        binding.button6.setOnClickListener {
            val intent = Intent(this, UrlActivity::class.java)
            startActivity(intent)
        }

        binding.buttonOrdersTracking.setOnClickListener {
            startActivity(Intent(this, OrdersTrackingActivity::class.java))
        }

        binding.button10.setOnClickListener {
            val intent = Intent(this, ObjectTrackingActivityExample::class.java)
            startActivity(intent)
        }

        binding.btnObjectTrackingProductStatus.setOnClickListener {
            val intent=Intent(this, ObjectTrackingProductStatus::class.java)
            startActivity(intent)
        }

        binding.btnResetSdk.setOnClickListener {
            ResetDialog.getInstance(object : ResetCallback {
                override fun resetOnly() {
                    Webtrekk.reset(this@MainActivity)
                }

                override fun resetAndSetNewValues(data: Map<String, Any>) {
                    Webtrekk.reset(this@MainActivity)
                    updateConfig(data)
                }

                override fun onlySetNewValues(data: Map<String, Any>) {
                    updateConfig(data)
                }
            }).show(supportFragmentManager, ResetDialog.TAG)

            /*ResetDialogBuilder(this) { batchEnabled, everId ->
                val oldSdk = Webtrekk.getInstance().toString().split("@")[1]
                val oldEverId = Webtrekk.getInstance().getEverId()
                val oldBatchEnabled = Webtrekk.getInstance().isBatchEnabled()

                val trackIds = BuildConfig.TRACK_IDS.split(",").toList()

                Webtrekk.reset(this)

                Webtrekk.getInstance()
                    .setIdsAndDomain(trackIds = trackIds, trackDomain = BuildConfig.DOMEIN)
                if (!everId.isNullOrEmpty())
                    Webtrekk.getInstance().setEverId(everId)
                Webtrekk.getInstance().setVersionInEachRequest(true)
                Webtrekk.getInstance().setBatchEnabled(batchEnabled)
                Webtrekk.getInstance().setRequestPerBatch(20)
                Webtrekk.getInstance().setExceptionLogLevel(ExceptionType.ALL)

                val newSdk = Webtrekk.getInstance().toString().split("@")[1]
                val newEverId = Webtrekk.getInstance().getEverId()
                val newBatchEnabled = Webtrekk.getInstance().isBatchEnabled()

                val sb = StringBuffer()

                sb.append("Old SDK: $oldSdk").append("\n")
                    .append("Old Ever ID: $oldEverId").append("\n")
                    .append("Old Batch Enabled: $oldBatchEnabled").append("\n")
                    //.append("Old App First Open: $oldAppFirstOpen").append("\n")
                    //.append("Old Current Session: $oldCurrentSession").append("\n")
                    .append("============================").append("\n")
                    .append("New SDK: $newSdk").append("\n")
                    .append("New Ever ID: $newEverId").append("\n")
                    .append("New Batch Enabled: $newBatchEnabled").append("\n")

                //.append("New App First Open: $newAppFirstOpen").append("\n")
                //.append("New Current Session: $newCurrentSession").append("\n")

                val msg = sb.toString()

                AlertDialog.Builder(this)
                    .setMessage("Webtrekk")
                    .setMessage(msg)
                    .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    .show()
            }*/
        }

        binding.buttonTestPageRequest.setOnClickListener {
            val intent = Intent(this, PageRequestsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonGetDmcUserId.setOnClickListener {
            Appoxee.instance().getDeviceInfoDMC()
        }

        val int = intent
        val uri = int.data
        if (uri != null) {
            val result = int.dataString?.removePrefix("mapptest://test?link=")
            val url = Uri.parse(result)
            Webtrekk.getInstance().trackUrl(url)
            Webtrekk.getInstance().trackPage(this)
        }
    }


    private fun updateConfig(data: Map<String, Any>) {
        if (data.containsKey("trackIds") && data.containsKey("trackDomain")) {
            val trackIds: List<String> = data.getValue("trackIds") as List<String>
            val trackDomain: String = data.getValue("trackDomain") as String
            Webtrekk.getInstance()
                .setIdsAndDomain(trackIds = trackIds, trackDomain = trackDomain)
        }

        if (data.containsKey("anonymousTracking")) {
            val anonymousTracking = data.getValue("anonymousTracking") as Boolean
            Webtrekk.getInstance().anonymousTracking(anonymousTracking, emptySet())
        }

        if (data.containsKey("everId")) {
            val everId: String = data.getValue("everId") as String
            Webtrekk.getInstance().setEverId(everId)
        }
        if (data.containsKey("batchEnabled")) {
            val batchEnabled = data.getValue("batchEnabled") as Boolean
            Webtrekk.getInstance().setBatchEnabled(batchEnabled)
        }

        if (data.containsKey("batchRequestSize")) {
            val batchRequestSize = data.getValue("batchRequestSize") as Int
            Webtrekk.getInstance().setRequestPerBatch(batchRequestSize)
        }

        if (data.containsKey("sendAppVersionInRequest")) {
            val sendAppVersionInRequest =
                data.getValue("sendAppVersionInRequest") as Boolean
            Webtrekk.getInstance().setVersionInEachRequest(sendAppVersionInRequest)
        }

        if (data.containsKey("exceptionLogLevel")) {
            val exceptionLogLevel = data.getValue("exceptionLogLevel") as ExceptionType
            Webtrekk.getInstance().setExceptionLogLevel(exceptionLogLevel)
        }

        if (data.containsKey("userMatching")) {
            val userMatching = data.getValue("userMatching") as Boolean
            Webtrekk.getInstance().setUserMatchingEnabled(userMatching)
            if (userMatching)
                Webtrekk.getInstance().getDmcUserId()
            Webtrekk.getInstance().sendRequestsNowAndClean()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}
