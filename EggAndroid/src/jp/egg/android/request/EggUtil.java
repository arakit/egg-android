package jp.egg.android.request;

import android.util.Log;

public class EggUtil {

	public static final void d(String tag, String msg){
		Log.d(tag, msg);
	}



	public static final void d_request(String url){
		Log.d("request", ""+url);
	}

}
