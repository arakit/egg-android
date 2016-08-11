package jp.egg.android.request2.okhttp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.egg.android.util.DUtil;
import jp.egg.android.util.JUtil;
import jp.egg.android.util.Json;
import jp.egg.android.util.Log;
import jp.egg.android.util.ReflectionUtils;

/**
 * Created by chikara on 2016/07/26.
 */
public abstract class BaseRequest<I, O> implements Request<O> {

    public static final int REQUEST_TYPE_DEFAULT = 0;
    public static final int REQUEST_TYPE_JSON = 1;
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String COOKIE = "Cookie";
    /**
     * Charset for request.
     */
    private static final String JSON_PROTOCOL_CHARSET = "utf-8";
    /**
     * Content type for request.
     */
    private static final String JSON_PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", JSON_PROTOCOL_CHARSET);
    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private Context mContext;
    private Response.Listener<O> mResponseListener;
    private Response.ErrorListener mErrorListener;
    private String mFinalUrl = null;
    private Priority mPriority = Priority.NORMAL;
    private Class<O> mBackedOutputType;
    private String mBaseUrl;
    private int mMethod;
    private int mRequestType = REQUEST_TYPE_DEFAULT;
    /**
     * The retry policy for this request.
     */
    private RetryPolicy mRetryPolicy;
    /**
     * Sequence number of this request, used to enforce FIFO ordering.
     */
    private Integer mSequence;
    /**
     * Whether or not this request has been canceled.
     */
    private boolean mCanceled = false;
    /**
     * Whether or not a response has been delivered for this request yet.
     */
    private boolean mResponseDelivered = false;
    private QueueHandler mQueueHandler;
    /**
     * An opaque token tagging this request; used for bulk cancellation.
     */
    private Object mTag;
    private boolean mShouldCache;

    protected BaseRequest(Context context, int method, String url) {
        this(context, method, url, null, null);
    }

    protected BaseRequest(Context context, int method, String url, Response.Listener<O> successListener, Response.ErrorListener errorListener) {
        mMethod = method;
        mContext = context.getApplicationContext();
        mBaseUrl = url;
        mBackedOutputType = findGenericOutputTypes(
                findTargetClass(JUtil.getClass(BaseRequest.this)));

        setListeners(successListener, errorListener);
    }

    /**
     * Set-Cookieの値をクッキーのキーと値に
     *
     * @param cookie
     * @return
     */
    protected static Pair<String, String> parseSetCookieToNameValuePair(String cookie) {
        if (TextUtils.isEmpty(cookie)) {
            return null;
        }
        String[] arr = cookie.split(";", 0);
        if (arr.length == 0) {
            return null;
        }
        String[] pair = arr[0].trim().split("=", 2);
        if (pair.length != 2) {
            return null;
        }
        return new Pair<String, String>(pair[0], pair[1]);
    }

    @NonNull
    private Class findGenericOutputTypes(Class clazz) {
        Type genericSuperClass = clazz.getGenericSuperclass();
        if (genericSuperClass instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) genericSuperClass).getActualTypeArguments();
            if (types[1] instanceof Class) {
                return (Class) types[1];
            } else if (types[1] instanceof GenericArrayType) {
                GenericArrayType genericArrayType = (GenericArrayType) types[1];
                Class c1 = (Class) genericArrayType.getGenericComponentType();
                Class c2;
                try {
                    String cn = "[L" + c1.getName() + ";";
                    c2 = Class.forName(cn);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("can not use type " + types[1]);
                }
                return c2;
            } else {
                throw new IllegalArgumentException("can not use type " + types[1]);
            }
        } else {
            throw new IllegalArgumentException("can not use type " + genericSuperClass);
        }
    }

    @NonNull
    private Class findTargetClass(Class clazz) {
        Class c = clazz;
        Class parameterizedTypeClass = null;
        while (c != null && c != BaseRequest.class) {
            Type genericSuperclass = c.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type[] types = parameterizedType.getActualTypeArguments();
                if (types != null && types.length == 2) {
                    if (types[1] instanceof Class || types[1] instanceof GenericArrayType) {
                        parameterizedTypeClass = c;
                    }
                }
            }
            Class superClass = c.getSuperclass();
            c = superClass;
        }
        return parameterizedTypeClass;
    }

    protected Context getContext() {
        return mContext;
    }

    protected Class<O> getBackedOutputType() {
        return mBackedOutputType;
    }

    public void setListeners(Response.Listener<O> successListener, Response.ErrorListener errorListener) {
        setSuccessListener(successListener);
        setErrorListener(errorListener);
    }

    public void setSuccessListener(Response.Listener<O> successListener) {
        mResponseListener = successListener;
    }

    public void setErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    protected void setRequestType(int requestType) {
        mRequestType = requestType;
    }

    protected abstract I getInput();

    public final int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        if (mFinalUrl != null) return mFinalUrl;

        int method = getMethod();
        if (method == Method.GET) {
            String baseUrl = mBaseUrl;
            try {
                Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                List<Pair<String, String>> params2 = getParams();
                for (Pair<String, String> e : params2) {
                    if (e.second != null) {
                        builder.appendQueryParameter(e.first, e.second);
                    }
                }
                return mFinalUrl = builder.toString();
            } catch (Exception authFailureError) {
                Log.e("request", "authFailureError url=" + baseUrl, authFailureError);
                return mFinalUrl = null;
            }
        } else {
            return mFinalUrl = mBaseUrl;
        }
    }

    protected void convertInputToParams(List<Pair<String, String>> params, Field field, Object value) {
        Class type = field.getType();
        String key = field.getName();
        if (value == null) {
            // なし
        } else if (type.isArray()) {
            Object[] arr = (Object[]) value;
            for (int i = 0; i < arr.length; i++) {
                params.add(Pair.create(key, arr[i].toString()));
            }
        } else {
            params.add(Pair.create(key, value.toString()));
        }
    }

    protected List<Pair<String, String>> getParams() {
        I in = getInput();

        List<Pair<String, String>> params2 = new LinkedList<Pair<String, String>>();
        Map<Field, Object> values = ReflectionUtils.getDeclaredFieldValues(
                in,
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean accept(Field field) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isStatic(modifiers)) return false;
                        if (Modifier.isPrivate(modifiers)) return false;
                        if (Modifier.isProtected(modifiers)) return false;
                        if (field.getName().contains("$")) return false;
                        return true;
                    }
                },
                null
        );

        for (Map.Entry<Field, Object> e : values.entrySet()) {
            Field field = e.getKey();
            Object value = e.getValue();
            convertInputToParams(params2, field, value);
        }

        if (Log.isDebug()) {
            Log.d("request-input", "" + mBaseUrl + " params => " + DUtil.toStringPairList((List) params2, false));
        }
        return params2;
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() {
        switch (mRequestType) {
            case REQUEST_TYPE_DEFAULT: {
                List<Pair<String, String>> params = getParams();
                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }
            case REQUEST_TYPE_JSON: {
                return getBodyForJson();
            }
            default: {
                return null;
            }
        }
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    protected String getBodyContentTypeForDefault() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public String getBodyContentType() {
        switch (mRequestType) {
            case REQUEST_TYPE_DEFAULT:
                return getBodyContentTypeForDefault();
            case REQUEST_TYPE_JSON:
                return getBodyContentTypeForJson();
            default:
                return null;
        }
    }

    /**
     * JSONでのリクエスト用のリクエストボディ
     * REQUEST_TYPE_JSONのときのみ
     *
     * @return json string
     */
    private byte[] getBodyForJson() {
        String requestBody = getParamsJson();
        try {
            return requestBody == null ? null : requestBody.getBytes(JSON_PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, JSON_PROTOCOL_CHARSET);
            return null;
        }
    }

    /**
     * JSONでのリクエスよ用のコンテントタイプ
     *
     * @return
     */
    private String getBodyContentTypeForJson() {
        return JSON_PROTOCOL_CONTENT_TYPE;
    }

    /**
     * 入力されたInputをもとに、リクエスト用のJSONを返す
     *
     * @return
     */
    protected String getParamsJson() {
        I in = getInput();

        JsonNode jsonNode = Json.toJson(in);
        String string = Json.stringify(jsonNode);

        if (Log.isDebug()) {
            Log.d("request-input", "" + mBaseUrl + " params => " + string);
        }
        return string;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(List<Pair<String, String>> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Pair<String, String> entry : params) {
                encodedParams.append(URLEncoder.encode(entry.first, paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.second, paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    protected abstract O getOutput(JsonNode jn);

    protected final void parseCookie(NetworkResponse response) {

        List<Pair<String, String>> headers = response.headers;
        List<String> result = new ArrayList<String>();
        for (Pair<String, String> header : headers) {
            if (SET_COOKIE.equalsIgnoreCase(header.first)) {
                result.add(header.second);
            }
        }
        if (result.size() > 0) {
            onReceivedCookie(result);
        }
    }

    protected void onReceivedCookie(List<String> cookies) {

    }

    @Override
    public Response<O> parseNetworkResponse(NetworkResponse response) {

        parseCookie(response);

        String data = parseToString(response);

        JsonNode node;
        try {
            node = Json.parse(data);
        } catch (Exception ex) {
            node = null;
        }

        if (Log.isDebug()) {
            if (node != null) {
                Log.d("output", "" + mBaseUrl + " > " + Json.stringify(node));
            } else {
                Log.d("output", "" + mBaseUrl + " > " + data);
            }
        }

        if (node != null) {
            O bean = getOutput(node);
            return Response.success(bean, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            return null;
        }
    }

    protected String parseToString(NetworkResponse response) {
        String data = null;
        try {
            data = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }


    @Override
    public void deliverResponse(O response) {
        if (mResponseListener != null) {
            mResponseListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    protected List<Pair<String, String>> getCookies() {
        List<Pair<String, String>> strCookies = new ArrayList<Pair<String, String>>();
        return strCookies;
    }

    private String makeCookieHeader(@NonNull List<Pair<String, String>> cookies) {
        StringBuilder builder = new StringBuilder();
        for (Pair<String, String> e : cookies) {
            if (builder.length() != 0) {
                builder.append("; ");
            }
            builder.append(e.first).append("=").append(e.second);
        }
        return builder.toString();
    }

    public List<Pair<String, String>> getHeaders() {
        List<Pair<String, String>> headers = new ArrayList<>();

        List<Pair<String, String>> cookies = getCookies();
        if (cookies != null && cookies.size() > 0) {
            String cookiesHeaderValue = makeCookieHeader(cookies);
            headers.add(new Pair<String, String>(COOKIE, cookiesHeaderValue));
            if (Log.isDebug()) {
                Log.d("header", "send cookie header, " + cookiesHeaderValue);
            }
        }

        return headers;
    }

    @Override
    public final Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    /**
     * Returns the sequence number of this request.
     */
    @Override
    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    /**
     * Returns the retry policy that should be used  for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    /**
     * Sets the retry policy for this request.
     *
     * @return This Request object to allow for chaining.
     */
    @Override
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
    }

    /**
     * Mark this request as having a response delivered on it.  This can be used
     * later in the request's lifetime for suppressing identical responses.
     */
    @Override
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     * Returns true if this request has had a response delivered for it.
     */
    @Override
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    @Override
    public VolleyError parseNetworkError(VolleyError volleyError) {

        if (volleyError!=null && volleyError.networkResponse!=null && volleyError.networkResponse!=null) {
            String body = parseToString(volleyError.networkResponse);
            Log.e("error", "parseNetworkError " + body);
        }

        return volleyError;
    }

    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    @Override
    public void cancel() {
        mCanceled = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    @Override
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * Returns this request's tag.
     *
     * @see Request#setTag(Object)
     */
    @Override
    public Object getTag() {
        return mTag;
    }

    /**
     * Set a tag on this request. Can be used to cancel all requests with this
     * tag by {@link com.android.volley.RequestQueue#cancelAll(Object)}.
     *
     * @return This Request object to allow for chaining.
     */
    @Override
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     * <p/>
     * <p>Also dumps all events from this request's event log; for debugging.</p>
     */
    @Override
    public void finish(final String tag) {
        if (mQueueHandler != null) {
            mQueueHandler.finish(this);
        }
//        if (VolleyLog.MarkerLog.ENABLED) {
//            final long threadId = Thread.currentThread().getId();
//            if (Looper.myLooper() != Looper.getMainLooper()) {
//                // If we finish marking off of the main thread, we need to
//                // actually do it on the main thread to ensure correct ordering.
//                Handler mainThread = new Handler(Looper.getMainLooper());
//                mainThread.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mEventLog.add(tag, threadId);
//                        mEventLog.finish(this.toString());
//                    }
//                });
//                return;
//            }
//
//            mEventLog.add(tag, threadId);
//            mEventLog.finish(this.toString());
//        } else {
//            long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
//            if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
//                VolleyLog.d("%d ms: %s", requestTime, this.toString());
//            }
//        }
    }

    @Override
    public void enqueued(QueueHandler queueHandler, int sequence) {
        mQueueHandler = queueHandler;
        mSequence = sequence;
    }

    /**
     * Set whether or not responses to this request should be cached.
     *
     * @return This Request object to allow for chaining.
     */
    @Override
    public final void setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
    }

    /**
     * Returns true if responses to this request should be cached.
     */
    @Override
    public final boolean shouldCache() {
        return mShouldCache;
    }


}
