package jp.egg.android.util;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtil {

	private static Handler sHandler;

    public static Handler getHandler(){
        if(sHandler==null) sHandler = new Handler(Looper.getMainLooper());
        return sHandler;
    }

    public static void post(Runnable run){
        postDelayed(run, 0);
    }
    public static void postDelayed(Runnable run, long delayMills){
        if(run == null) return ;
        getHandler().postDelayed(run, delayMills);
    }

    public static void postBackgroundAndUi(final Runnable background1, final Runnable ui2){
        postBackground(new Runnable() {
            @Override
            public void run() {
                if (background1!=null) background1.run();
                if (ui2!=null) post(ui2);
            }
        });
    }


    public static void postBackground(final Runnable run){
        postBackgroundDelayed(run, 0);
    }
    public static void postBackgroundDelayed(final Runnable run, long delayMills){
        if(run == null) return ;
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread(run);
                thread.start();
            }
        }, delayMills);
    }

}
