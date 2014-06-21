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
		if(run == null) return ;
		getHandler().post(run);
	}
	public static void postDelayed(Runnable run, long delayMills){
		if(run == null) return ;
		getHandler().postDelayed(run, delayMills);
	}

}
