package jp.egg.android.util;

import android.util.Log;
import android.util.Pair;

import java.util.List;
import java.util.Map;

import jp.egg.android.request2.okhttp.Request;

public class DUtil {

    public static final void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static final void d(String tag, String msg, Throwable tr) {
        Log.d(tag, msg, tr);
    }


    public static final void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static final void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }


    public static final void d_request(String url) {
        Log.d("request", "url = " + url);
    }

    public static final void d_request(String url, Map<String, String> params) {
        Log.d("request", "url = " + url + " " + params);
    }


    public static final String toStringMap(Map map, boolean ln) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object eo : map.entrySet()) {
            Map.Entry e = (Map.Entry) eo;
            String key = "" + e.getKey();
            String value = "" + e.getValue();
            if (!first) {
                sb.append(", ");
                if (ln) sb.append("\n");
            } else {
                first = false;
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }

    public static final String toStringPairList(List<Pair<String, String>> list, boolean ln) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Pair e : list) {
            String key = "" + e.first;
            String value = "" + e.second;
            if (!first) {
                sb.append(", ");
                if (ln) sb.append("\n");
            } else {
                first = false;
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }

    public static final String toStringFormDataList(List<Request.FormData> list, boolean ln) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Request.FormData e : list) {

            String key = "" + e.name;
            String value = null;

            if (e instanceof Request.TextFormData) {
                value = ((Request.TextFormData) e).value;
            }
            else if (e instanceof Request.FileFormData) {
                Request.FileFormData f = ((Request.FileFormData) e);
                value = f.file + "("+ f.filename +")";
            }

            if (!first) {
                sb.append(", ");
                if (ln) sb.append("\n");
            } else {
                first = false;
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }

}
