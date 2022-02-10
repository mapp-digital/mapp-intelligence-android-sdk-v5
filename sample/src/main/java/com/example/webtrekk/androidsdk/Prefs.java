package com.example.webtrekk.androidsdk;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String BATCH_ENABLED="BATCH_ENABLED";

    private final SharedPreferences sp;

    public Prefs(Context context) {
        this.sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
    }

    public void setBatchEnabled(boolean batchEnabled){
        sp.edit().putBoolean(BATCH_ENABLED,batchEnabled).apply();
    }

    public boolean isBatchEnabled(){
        return sp.getBoolean(BATCH_ENABLED,false);
    }
}
