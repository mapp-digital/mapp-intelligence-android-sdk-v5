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
import androidx.appcompat.app.AppCompatActivity
import com.appoxee.Appoxee
import com.example.webtrekk.androidsdk.mapp.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import webtrekk.android.sdk.*
import java.lang.StringBuilder

@TrackPageDetail(
    contextName = "Main Page",
    trackingParams = [TrackParams(
        paramKey = Param.PAGE_PARAMS.INTERNAL_SEARCH,
        paramVal = "search"
    )]
)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
