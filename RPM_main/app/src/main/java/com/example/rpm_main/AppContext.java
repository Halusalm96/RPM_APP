package com.example.rpm_main;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        AppContext.context = getApplicationContext();
    }

    public static Context getContext() {
        return AppContext.context;
    }
}