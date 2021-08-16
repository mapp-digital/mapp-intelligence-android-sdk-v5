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

package com.example.webtrekk.samplejava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import webtrekk.android.sdk.TrackPageDetail;

import webtrekk.android.sdk.TrackParams;

import webtrekk.android.sdk.Webtrekk;
import webtrekk.android.sdk.ParamType;
import webtrekk.android.sdk.events.PageViewEvent;
import webtrekk.android.sdk.events.eventParams.MediaParameters;
import webtrekk.android.sdk.events.eventParams.PageParameters;
import webtrekk.android.sdk.events.eventParams.UserCategories;

import static webtrekk.android.sdk.ParamTypeKt.createCustomParam;

@TrackPageDetail(
        contextName = "Main Page",
        trackingParams = {@TrackParams(paramKey = "is", paramVal = "search")}
)
public class MainActivity extends AppCompatActivity {

    private final Webtrekk webtrekk = Webtrekk.getInstance();
    private static final String BACKGROUND_PARAM = createCustomParam(ParamType.PAGE_CATEGORY, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button formActivity = findViewById(R.id.formActivity);
        formActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                startActivity(intent);
            }
        });

        Button crashActivity = findViewById(R.id.crashActivity);
        crashActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CrashActivity.class);
                startActivity(intent);
            }
        });

        //page properties
        Map<Integer, String> params = new HashMap<>();
        params.put(20, "cp20Override");

        Map<Integer, String> categories = new HashMap<>();
        categories.put(10, "test");

        String searchTerm = "testSearchTerm";
        PageParameters pageParameters =
                new PageParameters(params, searchTerm, categories);
        UserCategories userCategories = new UserCategories();
        userCategories.setCity("Paris");
        userCategories.setCountry("Germany");
        userCategories.setBirthday(new UserCategories.Birthday(22,12,2016));
        userCategories.setGender(UserCategories.Gender.FEMALE);

        PageViewEvent pageEvent = new PageViewEvent("the custom name of page");
        pageEvent.setPageParameters(pageParameters);
        pageEvent.setUserCategories(userCategories);
      //  pageEvent.setSessionParameters(sessionParameters);

        Webtrekk.getInstance().trackPage(pageEvent);
        MediaParameters md = new MediaParameters("sds", "sdsa", 2.2, 3.455123);
    }
}
