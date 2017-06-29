package jp.egg.android.request2.okhttp;

import android.os.SystemClock;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jp.egg.android.util.Log;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by chikara on 16/07/26.
 */
public class OkHttpNetwork implements Network {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;
    private OkHttpClient mClient;


    public OkHttpNetwork(OkHttpClient client) {
        mClient = client;
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
     * request's retry policy, a timeout exception is thrown.
     *
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
                                                VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = retryPolicy.getCurrentTimeout();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
//            request.addMarker(
//                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            Log.d("network", String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        Log.d("network", String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
        //request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    protected okhttp3.RequestBody handleBuildRequestBody(Request request) {

        RequestBody requestBody;
        if (request.isFormData()) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (Request.FormData e : request.getFormDataBody()) {
                if (e instanceof Request.TextFormData) {
                    Request.TextFormData content = (Request.TextFormData) e;
                    builder.addFormDataPart(content.name, content.value);
                }
                else if (e instanceof Request.FileFormData) {
                    Request.FileFormData content = (Request.FileFormData) e;
                    MediaType mediaType = content.contentType!=null ? MediaType.parse(content.contentType) : null;
                    builder.addFormDataPart(content.name, content.filename, RequestBody.create(mediaType, content.file));
                }
            }
            requestBody = builder.build();
        } else {
            MediaType mediaType = MediaType.parse(request.getBodyContentType());
            requestBody = okhttp3.RequestBody.create(mediaType, request.getBody());
        }
        if (requestBody != null) {
            requestBody = wrap(requestBody, request);
        }
        return requestBody;
    }

    protected okhttp3.Request handleBuildRequest(final Request request) {

        int method = request.getMethod();
        String url = request.getUrl();
        List<Pair<String, String>> headers = request.getHeaders();

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(url);
        okhttp3.RequestBody okHttpRequestBody;
        switch (method) {
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.POST:
                okHttpRequestBody = handleBuildRequestBody(request);
                builder.post(okHttpRequestBody);
                break;
            case Request.Method.DELETE:
                okHttpRequestBody = handleBuildRequestBody(request);
                if (okHttpRequestBody!=null) {
                    builder.delete(okHttpRequestBody);
                } else {
                    builder.delete();
                }
                break;
            case Request.Method.PUT:
                okHttpRequestBody = handleBuildRequestBody(request);
                builder.put(okHttpRequestBody);
                break;
        }

        for (Pair<String, String> header : headers) {
            builder.addHeader(header.first, header.second);
        }
        return builder.build();
    }

    private RequestBody wrap (final RequestBody requestBody, final Request request) {
        return new CountingRequestBody(requestBody, new CountingRequestBody.OnRequestProgressListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {
                request.onRequestProgress(bytesWritten, contentLength);
            }
        });
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            Response response = null;
            byte[] responseContents = null;
            List<Pair<String, String>> responseHeaders = new ArrayList<>();
            try {

                int timeout = request.getRetryPolicy().getCurrentTimeout();

                OkHttpClient client = mClient.newBuilder()
                        .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                        .readTimeout(timeout, TimeUnit.MILLISECONDS)
                        .build();

                Call call = client.newCall(handleBuildRequest(request));
                request.setCancelExecutor(new CancelExecutorImpl(call));
                response = call.execute();
                request.setCancelExecutor(null);
                int statusCode = response.code();

                for (Map.Entry<String, List<String>> valuesByName : response.headers().toMultimap().entrySet()) {
                    String headerName = valuesByName.getKey();
                    for (String headerValue : valuesByName.getValue()) {
                        responseHeaders.add(Pair.create(headerName, headerValue));
                    }
                }

//                // Handle cache validation.
//                if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
//
//                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED,
//                            request.getCacheEntry().data, responseHeaders, true);
//
////                    Entry entry = request.getCacheEntry();
////                    if (entry == null) {
////                        return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null,
////                                responseHeaders, true);
////                    }
////
////                    // A HTTP 304 response does not have all header fields. We
////                    // have to use the header fields from the cache entry plus
////                    // the new ones from the response.
////                    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
////                    entry.responseHeaders.putAll(responseHeaders);
////                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data,
////                            entry.responseHeaders, true);
//                }

                responseContents = response.body().bytes();

//                // Some responses such as 204s do not have content.  We must check.
//                if (httpResponse.getEntity() != null) {
//                    responseContents = entityToBytes(httpResponse.getEntity());
//                } else {
//                    // Add 0 byte response as a way of honestly representing a
//                    // no-content request.
//                    responseContents = new byte[0];
//                }

                if (responseContents == null) {
                    responseContents = new byte[0];
                }

                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, request, responseContents, statusCode);

                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false);

            }
            catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new TimeoutError());
            }
//            catch (ConnectTimeoutException e) {
//                attemptRetryOnException("connection", request, new TimeoutError());
//            }
            catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            }
            catch (IOException e) {
                int statusCode;
                NetworkResponse networkResponse = null;
                if (response != null) {
                    statusCode = response.code();
                } else {
                    throw new NoConnectionError(e);
                }
                VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED ||
                            statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
                    } else {
                        // TODO: Only throw ServerError for 5xx status codes.
                        throw new ServerError(networkResponse);
                    }
                } else {
                    throw new NetworkError(networkResponse);
                }
            }
            finally {
                if (response != null) {
                    response.close();
                }
            }
        }
    }

    private static class CancelExecutorImpl implements Request.CancelExecutor {

        Call call;

        CancelExecutorImpl (Call call) {
            this.call = call;
        }

        @Override
        public void cancel(Request request) {
            this.call.cancel();
        }
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request,
                                 byte[] responseContents, int statusCode) {
        if (requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            Log.d("network", String.format("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
                            "[rc=%d], [retryCount=%s]", request, requestLifetime,
                    responseContents != null ? responseContents.length : "null",
                    statusCode, request.getRetryPolicy().getCurrentRetryCount()));
        }
    }

    public static class CountingRequestBody extends RequestBody {

        protected RequestBody delegate;
        protected OnRequestProgressListener listener;

        protected CountingSink countingSink;

        public CountingRequestBody(RequestBody delegate, OnRequestProgressListener listener) {
            this.delegate = delegate;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return delegate.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            BufferedSink bufferedSink;

            countingSink = new CountingSink(sink);
            bufferedSink = Okio.buffer(countingSink);

            delegate.writeTo(bufferedSink);

            bufferedSink.flush();
        }

        public interface OnRequestProgressListener {

            public void onRequestProgress(long bytesWritten, long contentLength);

        }

        protected final class CountingSink extends ForwardingSink {

            private long bytesWritten = 0;

            public CountingSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                bytesWritten += byteCount;
                listener.onRequestProgress(bytesWritten, contentLength());
            }

        }

    }


}
