package jp.egg.android.request2.task;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.Header;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.util.HandlerUtil;
import jp.egg.android.util.JUtil;
import jp.egg.android.util.Json;
import jp.egg.android.util.Log;
import jp.egg.android.util.StringUtil;

/*


 */


/**
 * Created by chikara on 2014/09/06.
 */
public abstract class BaseImageUploadTask2<I, O> extends EggTask<O, EggTaskError> {

    private static String TAG = "BaseImageUploadTask2";

    private Context mContext;
    private int mMethod;
    private String mUrl;
    private Class<O> mBackedOutputType;

    private Call mCurrentRequest;

    protected BaseImageUploadTask2(Context context, int method, String url) {
        mContext = context.getApplicationContext();
        mMethod = method;
        mUrl = url;
        Type[] types = ((ParameterizedType) JUtil.getClass(BaseImageUploadTask2.this).getGenericSuperclass()).getActualTypeArguments();
        mBackedOutputType = (Class) types[1];
    }

    protected Context getContext(){
        return mContext;
    }

    protected Class<O> getBackedOutputType(){
        return mBackedOutputType;
    }

    protected abstract I getInput();

    protected abstract MultipartBuilder getRequestParams(I in);

    protected abstract O getOutput(JsonNode node);

    protected final String getCookie(){
        String strCookie = onSendCookie();
        return strCookie;
    }

    protected String onSendCookie(){
        return null;
    }



    protected static MultipartBuilder addPart (MultipartBuilder builder, String name, String value) {
        return builder.addPart(
                Headers.of("Content-Disposition", String.format("form-data; name=\"%s\"", name) ),
                RequestBody.create(null, value));
    }
    protected static MultipartBuilder addPart (MultipartBuilder builder, String name, File file, MediaType contentType) {
        return builder.addPart(
                Headers.of("Content-Disposition", String.format("form-data; name=\"%s\""), name),
                RequestBody.create(contentType, file));
    }


    @Override
    protected final void onDoInBackground() {
        super.onDoInBackground();

        OkHttpClient client = new OkHttpClient();

        String url = mUrl;
        I input = getInput();

        try {
            String strCookie = getCookie();
            if (!TextUtils.isEmpty(strCookie)) {
                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                client.setCookieHandler(cookieManager);

                List<String> values = new ArrayList<String>(Arrays.asList(strCookie));
                Map<String, List<String>> cookies = new HashMap<String, List<String>>();
                cookies.put("Set-Cookie", values);

                client.getCookieHandler().put(new URI(url), cookies);
            }
        } catch (Exception ex) {
            Log.e(TAG, "cookie setup error.", ex);
        }


        MultipartBuilder builder = getRequestParams(input);
        if (builder == null) {
            builder = new MultipartBuilder();
        }

        RequestBody requestBody = builder.build();
//        RequestBody requestBody = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
//                        RequestBody.create(null, "Square Logo"))
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
//                        RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
//                .build();

        Log.d(TAG, "requestBody = " +requestBody);

        Request request = new Request.Builder()
                //.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(url)
                .post(requestBody)
                .build();

        Log.d(TAG, "request = " +request);

        Call call = client.newCall(request);
        mCurrentRequest = call;

        Response response = null;
        try {
            response = call.execute();
        } catch (Exception ex) {
            Log.e(TAG, "failed execute.", ex);
        }

        Log.d(TAG, "response="+response);

        if (call.isCanceled()) {
            Log.d(TAG, "call.isCanceled.");
            setCancel();
        }
        else if (response != null) {

            try {
                String body = response.body().string();
                Log.d(TAG, "response body = "+body);
                JsonNode jn = Json.parse(body);
                setSucces(getOutput(jn));
            } catch (Exception ex) {
                Log.e(TAG, "failed parse.", ex);
                setError(null);
            }
        }
        else{
            Log.d(TAG, "result is error.");
            setError(null);
        }


    }


    @Override
    protected void onRequestCancel() {


        HandlerUtil.postBackground(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequest!=null && !mCurrentRequest.isCanceled()) {
                    mCurrentRequest.cancel();
                }
            }
        });


        super.onRequestCancel();
    }
}
