package jp.egg.android.request.out;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public abstract class EggResponseBody<O> {


    public static String parseToString(NetworkResponse response) throws UnsupportedEncodingException {

        String str =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));

        return str;

    }


    //イベント系

    //ベース系
    public final Response<O> parseNetworkResponse(NetworkResponse response) {
        try {

            return onParseNetworkResponse(response);

        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }


    //ツール系

    protected abstract Response<O> onParseNetworkResponse(NetworkResponse response);


}
