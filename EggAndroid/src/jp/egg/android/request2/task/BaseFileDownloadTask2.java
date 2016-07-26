package jp.egg.android.request2.task;

import android.content.Context;
import android.net.Uri;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.util.HandlerUtil;
import jp.egg.android.util.JUtil;
import jp.egg.android.util.Log;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by chikara on 2014/09/06.
 */
public abstract class BaseFileDownloadTask2<I, O> extends EggTask<O, BaseFileDownloadTask2.UploadTaskError> {

    protected static final int DEFAULT_RETRY_LIMIT = 1;
    private static final int REQUEST_RESULT_FINISH = 0;
    private static final int REQUEST_RESULT_RETRY = 1;
    private static String TAG = "BaseFileDownloadTask2";
    private Context mContext;
    private int mMethod;
    private String mUrl;
    private Class<O> mBackedOutputType;
    private Call mCurrentRequest;
    private int mRetryLimit = DEFAULT_RETRY_LIMIT;

    protected BaseFileDownloadTask2(Context context, int method, String url) {
        mContext = context.getApplicationContext();
        mMethod = method;
        mUrl = url;
        Type[] types = ((ParameterizedType) JUtil.getClass(BaseFileDownloadTask2.this).getGenericSuperclass()).getActualTypeArguments();
        mBackedOutputType = (Class) types[1];
    }

    protected Context getContext() {
        return mContext;
    }

    protected Class<O> getBackedOutputType() {
        return mBackedOutputType;
    }

    protected final int getMethod() {
        return mMethod;
    }

    protected abstract I getInput();

    protected abstract RequestBody getRequestBody(I in);

    protected abstract O getOutput(ResponseBody body);

    protected final List<String> getCookies() {
        List<String> strCookies = onSendCookies();
        if (strCookies == null) {
            strCookies = new ArrayList<String>();
        }
        return strCookies;
    }

    protected List<String> onSendCookies() {
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

        OkHttpClient client = new OkHttpClient
                .Builder()
                .build();

        int retryCount = 0;
        retry_loop:
        while (retryCount <= mRetryLimit) {
            int result = handleRequest(client);
            switch (result) {
                case REQUEST_RESULT_FINISH:
                    // 終了
                    break retry_loop;
                case REQUEST_RESULT_RETRY:
                    // リトライする
                    retryCount++;
                    continue retry_loop;
                default:
                    throw new IllegalStateException("unknown result code.");
            }
        }


    }


    protected Uri.Builder buildUrl() {
        return Uri.parse(mUrl).buildUpon();
    }

    private int handleRequest(OkHttpClient client) {

        String url = buildUrl().build().toString();
        I input = getInput();

        try {
            List<String> strCookies = getCookies();

//            CookieManager cookieManager = new CookieManager();
//            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//            client.setCookieHandler(cookieManager);
//
//            List<String> values = new ArrayList<String>(strCookies);
//            Map<String, List<String>> cookies = new HashMap<String, List<String>>();
//            cookies.put("Set-Cookie", values);
//
//            client.getCookieHandler().put(new URI(url), cookies);

            HttpUrl httpUrl = HttpUrl.parse(url);

            List<Cookie> cookies = new ArrayList<>();
            for (String str : strCookies) {
                cookies.add(Cookie.parse(httpUrl, str));
            }
            client.cookieJar().saveFromResponse(HttpUrl.parse(url), cookies);

        } catch (Exception ex) {
            if (Log.isDebug()) {
                Log.e(TAG, "cookie setup error.", ex);
            }
        }

        RequestBody requestBody = getRequestBody(input);
        if (Log.isDebug()) {
            Log.d(TAG, "requestBody = " + requestBody);
        }

        Request request = new Request.Builder()
                //.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(url)
                .post(requestBody)
                .build();

        if (Log.isDebug()) {
            Log.d(TAG, "request = " + request);
        }

        Call call = client.newCall(request);
        if (isCanceled()) {
            if (Log.isDebug()) {
                Log.d(TAG, "isCanceled.");
            }
            setCancel();
            return REQUEST_RESULT_FINISH;
        }

        mCurrentRequest = call;

        Response response = null;
        try {
            response = call.execute();
        } catch (Exception ex) {
            if (Log.isDebug()) {
                Log.e(TAG, "failed execute.", ex);
            }
        }

        if (Log.isDebug()) {
            Log.d(TAG, "response=" + response);
        }

        if (call.isCanceled()) {
            if (Log.isDebug()) {
                Log.d(TAG, "call.isCanceled.");
            }
            setCancel();
            return REQUEST_RESULT_FINISH;
        } else if (response != null && response.isSuccessful()) {
            // success!!
            try {
                ResponseBody body = response.body();
                setSucces(getOutput(body));
            } catch (Exception ex) {
                if (Log.isDebug()) {
                    Log.e(TAG, "failed parse.", ex);
                }
                setError(null);
            }
            return REQUEST_RESULT_FINISH;
        } else if (response != null) {
            // success以外
            int code = response.code();
            String message = response.message();
            if (Log.isDebug()) {
                Log.d(TAG, "response is error. response code is " + code + ". " + message);
            }

            if (code == 401) {
                boolean retry = onRetryAuthorizedInBackground();
                if (retry) {
                    if (Log.isDebug()) {
                        Log.d(TAG, "retry");
                    }
                    return REQUEST_RESULT_RETRY;
                } else {
                    if (Log.isDebug()) {
                        Log.d(TAG, "unauthorized error.");
                    }
                    DefaultUploadTaskError error = new DefaultUploadTaskError(code, message);
                    setError(error);
                    return REQUEST_RESULT_FINISH;
                }
            }

            if (Log.isDebug()) {
                Log.d(TAG, "response is null error.");
            }
            DefaultUploadTaskError error = new DefaultUploadTaskError(code, message);
            setError(error);
            return REQUEST_RESULT_FINISH;
        } else {
            if (Log.isDebug()) {
                Log.d(TAG, "result is error.");
            }
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
