package com.example.owner.winez;

import android.app.Application;
import android.content.Context;

/**
 * Created by owner on 01-Feb-17.
 */

public class MyApplication extends Application {
    private static Context context;
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
