package jp.egg.android.request.util;

import com.android.volley.Request;
import jp.egg.android.request.BaseUrl;

/**
 * Created by chikara on 2014/09/19.
 */
public enum SampleMethod implements MethodFactor {


    app_hello(Request.Method.GET, "/app/hello")

    ;

    public final int method;
    public final String path;

    private SampleMethod(int method, String path)
    {
        this.method = method;
        this.path = path;
    }

    @Override
    public int method() {
        return method;
    }
    @Override
    public String path() {
        return path;
    }

}

