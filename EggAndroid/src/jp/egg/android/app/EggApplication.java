package jp.egg.android.app;

import jp.egg.android.EggAndroid;
import android.app.Application;

public class EggApplication extends Application{



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
