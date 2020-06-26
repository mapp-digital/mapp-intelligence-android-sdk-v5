package com.example.webtrekk.samplejava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import webtrekk.android.sdk.Webtrekk;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        //Uncaught exception
        Button trackUncaught = findViewById(R.id.trackUncaught);
        trackUncaught.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer.parseInt("@!#");
            }
        });

        //Caught exceptions
        Button trackCaught = findViewById(R.id.trackCaught);
        trackCaught.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer.parseInt("@!#");
                } catch (Exception e) {
                    Webtrekk.getInstance().trackException(e);
                }
            }
        });

        //Custom exceptions
        Button trackCustom = findViewById(R.id.trackCustom);
        trackCaught.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer.parseInt("@!#");
                } catch (Exception e) {
                    Webtrekk.getInstance().trackException("Hello", "I am custom exception :)");
                }
            }
        });
    }
}