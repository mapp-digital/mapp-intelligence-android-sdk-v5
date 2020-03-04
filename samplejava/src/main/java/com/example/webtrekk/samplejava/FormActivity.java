package com.example.webtrekk.samplejava;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.LinkedHashMap;

import webtrekk.android.sdk.FormTrackingSettings;
import webtrekk.android.sdk.Webtrekk;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean anonymousField = false;
    private Button cancel;
    private Button confirm;
    private Switch anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_main);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormTrackingSettings form = new FormTrackingSettings();
                form.setConfirmButton(false);
                form.setFormName("test123");
                form.setAnonymous(anonymousField);
                form.setAnonymousSpecificFields(Arrays.asList(R.id.editText, R.id.editText3, R.id.switch1));
                form.setFullContentSpecificFields(Arrays.asList(R.id.editText2));
                Webtrekk.getInstance().formTracking(FormActivity.this, null, form);
            }
        });

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormTrackingSettings form = new FormTrackingSettings();
                form.setConfirmButton(true);
                form.setFormName("test123");
                form.setAnonymous(anonymousField);
                form.setAnonymousSpecificFields(Arrays.asList(R.id.editText2));
                form.setFullContentSpecificFields(Arrays.asList(R.id.editText, R.id.editText3, R.id.switch1));
                Webtrekk.getInstance().formTracking(FormActivity.this, null, form);
            }
        });

        anonymous = findViewById(R.id.anonymous);
        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                anonymousField = isChecked;
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
