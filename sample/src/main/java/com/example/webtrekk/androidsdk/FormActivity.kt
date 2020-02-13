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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.form_main.*
import webtrekk.android.sdk.FormTrackingSettings
import webtrekk.android.sdk.Webtrekk


class FormActivity : AppCompatActivity() {
    var annoimus = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_main)

        cancel.setOnClickListener {
            val form = FormTrackingSettings()
            form.confirmButton = false
            form.formName = "test123"
            form.anonymous = annoimus
            Webtrekk.getInstance().formTracking(this, formTrackingSettings = form)
        }

        confirm.setOnClickListener {
            val form = FormTrackingSettings()
            form.confirmButton = true
            form.formName = "test123"
            form.anonymous = annoimus
            Webtrekk.getInstance().formTracking(this, formTrackingSettings = form)
        }

        anonymous.setOnCheckedChangeListener { buttonView, isChecked ->
            annoimus = isChecked
        }












    }
}