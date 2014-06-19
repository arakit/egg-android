package jp.egg.android.util;

import java.util.Map;

import android.util.Log;

public class DUtil {

	public static final void d(String tag, String msg){
		Log.d(tag, msg);
	}
	public static final void d(String tag, String msg, Throwable tr){
		Log.d(tag, msg, tr);
	}



	public static final void e(String tag, String msg){
		Log.e(tag, msg);
	}
	public static final void e(String tag, String msg, Throwable tr){
		Log.e(tag, msg, tr);
	}


	public static final void d_request(String url){
		Log.d("request", "url = "+url);
	}
	public static final void d_request(String url, Map<String, String> params){
		Log.d("request", "url = "+url + " "+params);
	}

}