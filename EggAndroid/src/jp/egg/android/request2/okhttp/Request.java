package jp.egg.android.request2.okhttp;

import android.util.Pair;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by chikara on 16/07/26.
 */
public interface Request<O> {

    boolean isCanceled();

    void enqueued(QueueHandler queueHandler, int sequence);

    void finish(String tag);

    void cancel();

    void setCancelExecutor (CancelExecutor cancelExecutor) ;

    Object getTag();

    void setTag(Object tag);

    int getSequence();

    void markDelivered();

    Response<O> parseNetworkResponse(NetworkResponse response);

    VolleyError parseNetworkError(VolleyError error);

    boolean hasHadResponseDelivered();

    void deliverResponse(O response);

    void deliverError(VolleyError error);

    int getMethod();

    byte[] getBody();

    void onRequestProgress(long bytesWritten, long contentLength) ;

    boolean isFormData ();

    FormData[] getFormDataBody ();

    String getBodyContentType();

    String getUrl();

    List<Pair<String, String>> getHeaders();

    RetryPolicy getRetryPolicy();

    void setRetryPolicy(RetryPolicy retryPolicy);

    void setShouldCache(boolean shouldCache);

    boolean shouldCache();

    Priority getPriority();

    /**
     * Priority values.  Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * Supported request methods.
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
    }

    public static final class FileFormData extends FormData {
        public String contentType;
        public String filename;
        public File file;
    }
    public static final class TextFormData extends FormData {
        public String value;
    }
    public abstract static class FormData {
        public String name;

        public static final TextFormData text (String name, String value) {
            TextFormData data = new TextFormData();
            data.name = name;
            data.value = value;
            return data;
        }
        public static final FileFormData file (String name, String filename, File file) {
            FileFormData data = new FileFormData();
            data.name = name;
            data.filename = filename;
            data.file = file;
            return data;
        }
    }

    public interface CancelExecutor {
        public void cancel (Request request);
    }
}
