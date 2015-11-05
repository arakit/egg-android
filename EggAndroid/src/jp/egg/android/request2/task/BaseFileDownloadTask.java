package jp.egg.android.request2.task;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.IOException;

import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskError;

/*


 */


/**
 * Created by chikara on 2014/09/06.
 */
public abstract class BaseFileDownloadTask<I> extends EggTask<File, EggTaskError> {


    private Context mContext;
    private String mUrl;
    private File mOutputFile;


    protected BaseFileDownloadTask(Context context, String url, File output) {
        mContext = context.getApplicationContext();
        mUrl = url;
        mOutputFile = output;
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract I getInput();

    protected abstract RequestParams getRequestParams(I in);

    protected final String getCookie() {
        String strCookie = onSendCookie();
        return strCookie;
    }

    protected String onSendCookie() {
        return null;
    }

    protected void onDownloadProgress(long bytesWritten, long totalSize) {

    }

    @Override
    protected final void onDoInBackground() {
        super.onDoInBackground();

        String url = mUrl;

        I input = getInput();
        RequestParams params = getRequestParams(input);

        String strCookie = getCookie();
        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(1000 * 60);
        if (!TextUtils.isEmpty(strCookie)) {
            client.addHeader("cookie", strCookie);
        }

        ResponseHandler rs = new ResponseHandler(mOutputFile);
        RequestHandle request = client.get(url, params, rs);

        if (rs.isSuccess && !rs.isFailure) {
            setSucces(rs.response);
        } else {
            setError(null);
        }
    }

    private class ResponseHandler extends FileAsyncHttpResponseHandler {

        public File response;
        boolean isFailure;
        boolean isSuccess;

        public ResponseHandler(Context context) {
            super(context);
        }

        public ResponseHandler(File file) {
            super(file);
        }

        @Override
        protected File getTemporaryFile(Context context) {
            return super.getTemporaryFile(context);
        }

        @Override
        public File getTargetFile() {
            return super.getTargetFile();
        }

        @Override
        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, File file) {
            isFailure = true;
            response = file;
        }

        @Override
        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, File file) {
            isSuccess = true;
            response = file;
        }


        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            BaseFileDownloadTask.this.onDownloadProgress(bytesWritten, totalSize);
        }
    }


}
