package jp.egg.android.request2.task;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.util.JUtil;
import jp.egg.android.util.Json;
import jp.egg.android.util.Log;

/*


 */


/**
 * Created by chikara on 2014/09/06.
 */
public abstract class BaseImageUploadTask<I, O> extends EggTask<O, EggTaskError> {

    private static String TAG = "BaseImageUploadTask";

    private Context mContext;
    private int mMethod;
    private String mUrl;
    private Class<O> mBackedOutputType;

    private RequestHandle mCurrentRequest;

//    private Response<O> mResponseListener;
//    private Response.ErrorListener mErrorListener;

    protected BaseImageUploadTask(Context context, int method, String url) {
        mContext = context.getApplicationContext();
        mMethod = method;
        mUrl = url;
        Type[] types = ((ParameterizedType) JUtil.getClass(BaseImageUploadTask.this).getGenericSuperclass()).getActualTypeArguments();
        mBackedOutputType = (Class) types[1];
    }

    protected Context getContext() {
        return mContext;
    }

    protected Class<O> getBackedOutputType() {
        return mBackedOutputType;
    }

    protected abstract I getInput();

    protected abstract RequestParams getRequestParams(I in);

    protected abstract O getOutput(JsonNode node);

    protected final String getCookie() {
        String strCookie = onSendCookie();
        return strCookie;
    }

    protected String onSendCookie() {
        return null;
    }

    @Override
    protected final void onDoInBackground() {
        super.onDoInBackground();

        String url = mUrl;

        I input = getInput();
        RequestParams params = getRequestParams(input);
//        mIn.setupBase(mContext);
//
//        if(mIn.device_type!=null)       params.add("device_type", mIn.device_type);
//        if(mIn.app_version!=null)       params.add("app_version", mIn.app_version);
//        if(mIn.app_version_code!=null)  params.add("app_version_code", mIn.app_version_code.toString());

        String strCookie = getCookie();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(1000 * 60);
        if (!TextUtils.isEmpty(strCookie)) {
            client.addHeader("cookie", strCookie);
        }

        Log.d(TAG, "request " + url + " " + params);

        ResponseHandler rs = new ResponseHandler();
        RequestHandle request;

        try {
            request = client.post(url, params, rs);
            mCurrentRequest = request;
        } catch (Exception ex) {
            Log.e(TAG, "failed client.post()", ex);
            throw ex;
        }
//        request = client.post(url, params, rs)

        if (isCanceled()) {
            Log.d(TAG, "request is pre canceled.");
            request.cancel(true);
        }
        while (!request.isFinished() && !request.isCancelled()) {
            Log.d(TAG, "request do background. running now. finished or canceled wait.");
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {

            }
        }

        if (request.isFinished() && rs.isSuccess && !rs.isFailure) {
            JsonNode jn = Json.parse(rs.response);
            setSucces(getOutput(jn));
        } else if (request.isCancelled()) {
            setCancel();
        } else {
            setError(null);
        }
    }

    @Override
    protected void onRequestCancel() {
        if (mCurrentRequest != null && !mCurrentRequest.isCancelled()) {
            mCurrentRequest.cancel(true);
        }
        super.onRequestCancel();
    }

    private static class ResponseHandler extends TextHttpResponseHandler {

        public String response;
        boolean isFailure;
        boolean isSuccess;

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            isFailure = true;
            response = s;
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            isSuccess = true;
            response = s;
        }

    }
}
