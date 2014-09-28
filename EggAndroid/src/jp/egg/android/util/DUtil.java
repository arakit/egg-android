package jp.egg.android.util;

import java.util.List;
import java.util.Map;

import android.util.Log;
import android.util.Pair;

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




    public static final String toStringMap(Map map, boolean ln){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Object eo : map.entrySet()){
            Map.Entry e = (Map.Entry)eo;
            String key = "" + e.getKey();
            String value = "" + e.getValue();
            if(!first){
                sb.append(", ");
                if(ln) sb.append("\n");
            }else{
                first = false;
            }
            sb.append(key+"="+value);
        }
       return  sb.toString();
    }

    public static final String toStringPairList(List<Pair> list, boolean ln){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Pair e : list){
            String key = "" + e.first;
            String value = "" + e.second;
            if(!first){
                sb.append(", ");
                if(ln) sb.append("\n");
            }else{
                first = false;
            }
            sb.append(key+"="+value);
        }
        return  sb.toString();
    }

}
