package jp.egg.android.request2.okhttp;

import android.util.Pair;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import java.util.List;

/**
 * Created by chikara on 16/07/26.
 */
public interface Request<O> {

    boolean isCanceled();

    void enqueued(QueueHandler queueHandler, int sequence);

    void finish(String tag);

    void cancel();

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

}
