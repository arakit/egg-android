package jp.egg.android.request.out;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public abstract class EggResponseBody<O> {



	//ベース系
    public final Response<O> parseNetworkResponse(NetworkResponse response) {
        try {

        	return onParseNetworkResponse(response);

        } catch (Exception e) {
            return Response.error(new ParseError(e));
		}
    }






    //イベント系


    protected abstract Response<O> onParseNetworkResponse(NetworkResponse response) ;




    //ツール系

    public static String parseToString(NetworkResponse response) throws UnsupportedEncodingException{

        String str =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));

        return str;

    }


}
