package jp.egg.android.util;


/**
 * Created by chikara on 2014/07/10.
 */
public class Log {

    private static boolean sIsDebug = false;

    public static final void setDebug(boolean debug){
        sIsDebug = debug;
    }

    public static final void d(String tag, String msg){
        if(sIsDebug) android.util.Log.d(tag, msg);
    }
    public static final void d(String tag, String msg, Throwable throwable){
        if(sIsDebug) android.util.Log.d(tag, msg, throwable);
    }



    public static final void e(String tag, String msg){
        if(sIsDebug) android.util.Log.e(tag, msg);
    }
    public static final void e(String tag, String msg, Throwable throwable){
        if(sIsDebug) android.util.Log.e(tag, msg, throwable);
    }

}
