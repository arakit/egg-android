package jp.egg.android.app;

import android.app.Application;

import jp.egg.android.EggAndroid;

public class EggApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        EggAndroid.initialize(EggApplication.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        EggAndroid.terminate();

    }


}
