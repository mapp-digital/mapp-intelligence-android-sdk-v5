package com.example.webtrekk.samplejava;

import android.os.Bundle;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import webtrekk.android.sdk.Webtrekk;
import webtrekk.android.sdk.model.Param;
import webtrekk.android.sdk.model.ParamType;

import static webtrekk.android.sdk.model.ParamTypeKt.createCustomParam;

public class MainActivity extends AppCompatActivity {

    private final Webtrekk webtrekk = Webtrekk.getInstance();
    private static final String BACKGROUND_PARAM = createCustomParam(ParamType.PAGE_CATEGORY, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> params = new LinkedHashMap<>();
        params.put(BACKGROUND_PARAM, "lol");
        params.put(Param.INTERNAL_SEARCH, "lel");

        webtrekk.trackPage("my page", params);
    }
}
