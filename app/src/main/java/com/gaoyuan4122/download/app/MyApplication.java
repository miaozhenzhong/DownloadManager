package com.gaoyuan4122.download.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by GAOYUAN on 2015/6/6.
 */
public class MyApplication  extends Application{
    private static Context mCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;
    }

    public static Context getContext() {
        return mCtx;
    }
}
