package jp.egg.android.request2.task;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.util.HandlerUtil;
import jp.egg.android.util.JUtil;
import jp.egg.android.util.Json;
import jp.egg.android.util.Log;

/**
 * Created by chikara on 2014/09/06.
 */
public abstract class BaseImageUploadTask2<I, O> extends EggTask<O, BaseImageUploadTask2.UploadTaskError> {

    protected static final int DEFAULT_RETRY_LIMIT = 1;
    private static final int REQUEST_RESULT_FINISH = 0;
    private static final int REQUEST_RESULT_RETRY = 1;
    private static String TAG = "BaseImageUploadTask2";
    private Context mContext;
    private int mMethod;
    private String mUrl;
    private Class<O> mBackedOutputType;
    private Call mCurrentRequest;
    private int mRetryLimit = DEFAULT_RETRY_LIMIT;

    protected BaseImageUploadTask2(Context context, int method, String url) {
        mContext = context.getApplicationContext();
        mMethod = method;
        mUrl = url;
        Type[] types = ((ParameterizedType) JUtil.getClass(BaseImageUploadTask2.this).getGenericSuperclass()).getActualTypeArguments();
        mBackedOutputType = (Class) types[1];
    }

    protected static MultipartBuilder addPart(MultipartBuilder builder, String name, String value) {
        return builder.addPart(
                Headers.of("Content-Disposition", String.format("form-data; name=\"%s\"", name)),
                RequestBody.create(null, value));
    }

    protected static MultipartBuilder addPart(MultipartBuilder builder, String name, File file, MediaType contentType) {
        return builder.addPart(
                Headers.of("Content-Disposition", String.format("form-data; name=\"%s\""), name),
                RequestBody.create(contentType, file));
    }

    protected Context getContext() {
        return mContext;
    }

    protected Class<O> getBackedOutputType() {
        return mBackedOutputType;
    }

    protected abstract I getInput();

    protected abstract MultipartBuilder getRequestParams(I in);

    protected abstract O getOutput(JsonNode node);

    protected final String getCookie() {
        String strCookie = onSendCookie();
        return strCookie;
    }

    protected String onSendCookie() {
        return null;
    }

    /**
     * リクエスト失敗時のリトライ回数
     *
     * @param limit 1の場合、失敗すると1回だけリトライする
     */
    protected void setRetryLimit(int limit) {
        mRetryLimit = limit;
    }

    @Override
    protected final void onDoInBackground() {
        super.onDoInBackground();

        OkHttpClient client = new OkHttpClient();

        int retryCount = 0;
        retry_loop:
        while (retryCount <= mRetryLimit) {
            int result = handleRequest(client, retryCount);
            switch (result) {
                case REQUEST_RESULT_FINISH:
                    // 終了
                    break retry_loop;
                case REQUEST_RESULT_RETRY:
                    // リトライする
                    continue retry_loop;
                default:
                    throw new IllegalStateException("unknown result code.");
            }
        }


    }

    private int handleRequest(OkHttpClient client, int currentRetryCount) {

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
        Log.d(TAG, "requestBody = " + requestBody);

        Request request = new Request.Builder()
                //.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(url)
                .post(requestBody)
                .build();

        Log.d(TAG, "request = " + request);

        Call call = client.newCall(request);
        if (isCanceled()) {
            Log.d(TAG, "isCanceled.");
            setCancel();
            return REQUEST_RESULT_FINISH;
        }

        mCurrentRequest = call;

        Response response = null;
        try {
            response = call.execute();
        } catch (Exception ex) {
            Log.e(TAG, "failed execute.", ex);
        }

        Log.d(TAG, "response=" + response);

        if (call.isCanceled()) {
            Log.d(TAG, "call.isCanceled.");
            setCancel();
            return REQUEST_RESULT_FINISH;
        } else if (response != null && response.isSuccessful()) {
            // success!!
            try {
                String body = response.body().string();
                Log.d(TAG, "response body = " + body);
                JsonNode jn = Json.parse(body);
                setSucces(getOutput(jn));
            } catch (Exception ex) {
                Log.e(TAG, "failed parse.", ex);
                setError(null);
            }
            return REQUEST_RESULT_FINISH;
        } else if (response != null) {
            // success以外
            int code = response.code();
            String message = response.message();
            Log.d(TAG, "response is error. response code is " + code + ". " + message);

            if (code == 401 || code == 403) {
                boolean retry = onRetryAuthorizedInBackground();
                if (retry) {
                    Log.d(TAG, "retry");
                    return REQUEST_RESULT_RETRY;
                } else {
                    Log.d(TAG, "unauthorized error.");
                    setError(null);
                    return REQUEST_RESULT_FINISH;
                }
            }

            Log.d(TAG, "response is null error.");
            setError(null);
            return REQUEST_RESULT_FINISH;
        } else {
            Log.d(TAG, "result is error.");
            setError(null);
            return REQUEST_RESULT_FINISH;
        }
    }


    protected boolean onRetryAuthorizedInBackground() {
        return false;
    }

    @Override
    protected void onRequestCancel() {


        HandlerUtil.postBackground(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRequest != null && !mCurrentRequest.isCanceled()) {
                    mCurrentRequest.cancel();
                }
            }
        });


        super.onRequestCancel();
    }


    @Override
    protected void onError(UploadTaskError result) {
        super.onError(result);
    }


    public abstract static class UploadTaskError extends EggTaskError {

        public abstract int code();

        public abstract String message();
    }

    public static class DefaultUploadTaskError extends UploadTaskError {
        private int code;
        private String message;

        public DefaultUploadTaskError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public int code() {
            return code;
        }

        @Override
        public String message() {
            return message;
        }
    }

}
