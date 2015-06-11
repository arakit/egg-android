package jp.egg.android.request;

import jp.egg.android.request.util.UrlFactor;

/**
 * Created by chikara on 2014/07/10.
 */
public abstract class BaseUrl implements UrlFactor {


    protected BaseUrl() {

    }


    public static final String combine(String... str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length;
        if (length == 0) return "";
        boolean sep = true;
        for (int i = 0; i < length; i++) {
            String s = str[i];
            if (s.length() == 0) continue;
            if (s.charAt(0) == '/') s = s.substring(1, s.length());
            if (sep) {
                sb.append(s);
            } else {
                sb.append("/");
                sb.append(s);
            }
            sep = s.charAt(s.length() - 1) == '/';
        }
        return sb.toString();
    }
}
