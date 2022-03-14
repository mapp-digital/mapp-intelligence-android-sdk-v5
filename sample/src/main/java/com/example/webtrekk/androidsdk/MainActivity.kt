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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.mapp.PageRequestsActivity
import kotlinx.android.synthetic.main.activity_main.btnResetSdk
import kotlinx.android.synthetic.main.activity_main.button10
import kotlinx.android.synthetic.main.activity_main.button4
import kotlinx.android.synthetic.main.activity_main.button5
import kotlinx.android.synthetic.main.activity_main.button6
import kotlinx.android.synthetic.main.activity_main.buttonSendRequests
import kotlinx.android.synthetic.main.activity_main.buttonTestPageRequest
import kotlinx.android.synthetic.main.activity_main.crashActivity
import kotlinx.android.synthetic.main.activity_main.formActivity
import kotlinx.android.synthetic.main.activity_main.startDetailsActivity
import kotlinx.android.synthetic.main.activity_main.videoActivity
import kotlinx.android.synthetic.main.activity_main.webViewActivity
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.TrackPageDetail
import webtrekk.android.sdk.TrackParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.WebtrekkConfiguration

@TrackPageDetail(
    contextName = "Main Page",
    trackingParams = [TrackParams(
        paramKey = Param.PAGE_PARAMS.INTERNAL_SEARCH,
        paramVal = "search"
    )]
)
class MainActivity : AppCompatActivity() {

    private lateinit var prefs:Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs= Prefs(this);

        setTitle("${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}")
/*
        val stringIds = BuildConfig.TRACK_IDS
        val domain = BuildConfig.DOMEIN
        val elements: List<String> = stringIds.split(",")
        val webtrekkConfiguration=WebtrekkConfiguration.Builder(elements,domain).build()

        Webtrekk.getInstance().init(this, webtrekkConfiguration)
*/

        buttonSendRequests.setOnClickListener {
            Webtrekk.getInstance().sendRequestsNowAndClean()
        }

        startDetailsActivity.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }

        webViewActivity.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

        formActivity.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        crashActivity.setOnClickListener {
            val intent = Intent(this, CrashActivity::class.java)
            startActivity(intent)
        }

        videoActivity.setOnClickListener {
            val intent = Intent(this, MediaExample::class.java)
            startActivity(intent)
        }

        button4.setOnClickListener {
            val intent = Intent(this, SettingsExample::class.java)
            startActivity(intent)
        }
        button5.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button6.setOnClickListener {
            val intent = Intent(this, UrlActivity::class.java)
            startActivity(intent)
        }

        button10.setOnClickListener {
            val intent = Intent(this, ObjectTrackingActivityExample::class.java)
            startActivity(intent)
        }

        btnResetSdk.setOnClickListener {
            ResetDialogBuilder(this) { batchEnabled,everId->
                val oldSdk=Webtrekk.getInstance().toString().split("@")[1]
                val oldEverId=Webtrekk.getInstance().getEverId()
                val oldBatchEnabled=Webtrekk.getInstance().isBatchEnabled()

                val trackIds=BuildConfig.TRACK_IDS.split(",").toList()
                val config=WebtrekkConfiguration.Builder(trackIds, BuildConfig.DOMEIN)
                    .setBatchSupport(batchEnabled)
                    .setEverId(everId)
                    .build()

                Webtrekk.reset(this,config)

                val newSdk=Webtrekk.getInstance().toString().split("@")[1]
                val newEverId=Webtrekk.getInstance().getEverId()
                val newBatchEnabled=Webtrekk.getInstance().isBatchEnabled()

                val sb=StringBuffer()

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

                val msg=sb.toString()

                AlertDialog.Builder(this)
                    .setMessage("Webtrekk")
                    .setMessage(msg)
                    .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

        buttonTestPageRequest.setOnClickListener {
            val intent = Intent(this, PageRequestsActivity::class.java)
            startActivity(intent)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

}
