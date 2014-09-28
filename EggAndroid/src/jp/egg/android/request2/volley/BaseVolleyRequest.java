package jp.egg.android.request2.volley;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.databind.JsonNode;
import jp.egg.android.util.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by chikara on 2014/07/10.
 */
public abstract class BaseVolleyRequest<I, O> extends Request<O> {


    public static final String SET_COOKIE = "Set-Cookie";

    private Context mContext;
    private Response.Listener<O> mResponseListener;
    private Response.ErrorListener mErrorListener;

    private String mFinalUrl = null;

    //private boolean mIsRetryAuthToken = true;
    private Priority mPriority = Priority.NORMAL;
    private Class<O> mBackedOutputType;
    private String mDeNormalizedUrl;

    protected BaseVolleyRequest(Context context, int method, String url) {
        this(context, method, url, null, null);
    }
    protected BaseVolleyRequest(Context context, int method, String url, Response.Listener<O> successListener, Response.ErrorListener errorListener) {
        super(method,
                url,
                null);
        mContext = context.getApplicationContext();
//        mResponseListener = success_listener;
//        mErrorListener = listener;
        //setRetryPolicy(new HairRetryPolicy(this, mAuthTokenErrorListener));
        mDeNormalizedUrl = url;
        //Log.d("request",""+HairUrl.methodUrl(hairMethod));
        Type[] types = ((ParameterizedType)JUtil.getClass(BaseVolleyRequest.this).getGenericSuperclass()).getActualTypeArguments();
        if( types[1] instanceof Class ){
            mBackedOutputType = (Class) types[1];
        } else if( types[1] instanceof GenericArrayType ) {
            GenericArrayType genericArrayType = (GenericArrayType) types[1];
            Class c1 = (Class) genericArrayType.getGenericComponentType();
            Class c2;
            try {
                String cn = "[L"+c1.getName()+";";
                //Log.d("test23", "b="+cn);
                c2 = Class.forName(cn);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("can not use type "+types[1]);
            }
            //Log.d("test23", "a="+c2);
            mBackedOutputType = c2;
        } else {
            throw new IllegalArgumentException("can not use type "+types[1]);
        }


        setListeners(successListener, errorListener);
    }

    protected Context getContext(){
        return mContext;
    }

    protected Class<O> getBackedOutputType(){
        return mBackedOutputType;
    }

//    private final HairRetryPolicy.OnAuthTokenErrorListener mAuthTokenErrorListener = new HairRetryPolicy.OnAuthTokenErrorListener() {
//        @Override
//        public void onAuthTokenError() {
//            if( !mIsRetryAuthToken ) return;
//
//            requestAuthToken();
//        }
//    };

    protected void setPriority(Priority priority){
        mPriority = priority;
    }

//    private void requestAuthToken(){
//
//        RequestToken in = new RequestToken();
//        in.uuid = AccountPreference.getUuid(mContext);
//        in.secret = AccountPreference.getUuidSecret(mContext);
//
//        RequestFuture<TokenRoot> future = RequestFuture.newFuture();
//
//        AppTokenApi api = new AppTokenApi(
//                mContext,
//                in,
//                future,
//                future
//        );
//
//        EggTaskCentral.getInstance()
//                .addVolleyRequestByObject(api, null);
//
//        try {
//            future.get();
//            Log.d("request", "retryAuthToken");
//        } catch (InterruptedException e) {
//            Log.d("request", "retryAuthToken InterruptedException", e);
//        } catch (ExecutionException e) {
//            Log.d("request", "retryAuthToken ExecutionException", e);
//        } catch (Exception e){
//            Log.d("request", "retryAuthToken ExecutionException", e);
//        }
//
//
//    }


//    public void setRetryAuthTokenEnabled(boolean enabled){
//        mIsRetryAuthToken = enabled;
//    }


    public void setListeners(Response.Listener<O> successListener, Response.ErrorListener errorListener){
        setSuccessListener(successListener);
        setErrorListener(errorListener);
    }
    public void setSuccessListener(Response.Listener<O> successListener){
        mResponseListener = successListener;
    }
    public void setErrorListener(Response.ErrorListener errorListener){
        mErrorListener = errorListener;
    }

    protected abstract I getInput();

    @Override
    public String getUrl()  {
        if(mFinalUrl!=null) return mFinalUrl;

        int method = getMethod();
        if(method == Method.GET){
            String baseUrl = super.getUrl();
            try {
                Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                List<Pair<String, String>> params2 = getParams2();
                for( Pair<String, String> e : params2 ){
                    if(e.second!=null) {
                        builder.appendQueryParameter(e.first, e.second);
                    }
                }
                return mFinalUrl = builder.toString();
            } catch (Exception authFailureError) {
                Log.e( "request", "authFailureError url="+baseUrl , authFailureError);
                return mFinalUrl = null;
            }
        }else {
            return mFinalUrl = super.getUrl();
        }
    }


    protected void convertInputToParams(List<Pair<String, String>> params, Field field, Object value){
        Class type = field.getType();
        String key = field.getName();
        if( value == null ){
            // なし
        }
        else if( type.isArray() ){
            Object[] arr = (Object[]) value;
            for(int i=0;i<arr.length;i++){
                params.add(Pair.create(key, arr[i].toString()));
            }
        }
        else{
            params.add(Pair.create(key, value.toString()));
        }
    }

    protected List<Pair<String, String>> getParams2(){
        I in = getInput();

        List<Pair<String, String>> params2 = new LinkedList<Pair<String, String>>();
        Map<Field, Object> values = ReflectionUtils.getDeclaredFieldValues(
                in,
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean accept(Field field) {
                        return true;
                    }
                },
                null
        );

        for(Map.Entry<Field, Object> e : values.entrySet()){
            Field field = e.getKey();
            Object value = e.getValue();
            convertInputToParams(params2, field, value);
        }

//        HashMap params = Json.fromJson(Json.toJson(in), HashMap.class);
//        HashMap<String, String> params2 = new HashMap<String, String>();
//        for(Object o : params.entrySet()){
//            Map.Entry<String, Object> e = (Map.Entry<String, Object>) o;
//            if(e.getValue()!=null){
//                params2.put(e.getKey(), e.getValue().toString());
//            }
//        }
        Log.d("request", "------------------------------------");
        Log.d("request-input", ""+ mDeNormalizedUrl + " params => " + DUtil.toStringPairList((List)params2, false));
        Log.d("input", ""+ mDeNormalizedUrl + " params => " + DUtil.toStringPairList((List)params2, false));
        return params2;
    }

    @Override
    @Deprecated
    protected final Map<String, String> getParams() throws AuthFailureError {
        throw new RuntimeException("not use!!!!!");
    }


    @Override
    @Deprecated
    public final byte[] getPostBody() throws AuthFailureError {
        throw new RuntimeException("not use!!!!!");
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        List<Pair<String, String>> params = getParams2();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
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


    protected final void parseCookie(NetworkResponse response){
        Map<String, String> headers = response.headers;
        if (headers.containsKey(SET_COOKIE)) {
            String cookie = headers.get(SET_COOKIE);
            onReceivedCookie(cookie);

//            AccountPreference.putCookies(mContext, cookie);
//            mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//                    .edit()
//                    .putString(PREFERENCE_KEY_COOKIE, cookie)
//                    .commit();
//            if(Config.isDebug()) Log.d("cookie", "received cookie = "+ cookie);
//            if(Config.isDebug()) Log.d("request-response-cookie", "received cookie = "+ cookie +"; "+mDeNormalizedUrl);
        }
    }

    protected void onReceivedCookie(String strCookie){

    }

    @Override
    protected Response<O> parseNetworkResponse(NetworkResponse response) {

        parseCookie(response);

        String data = null;
        try {
            data = new String( response.data,
                    HttpHeaderParser.parseCharset(response.headers) );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("output-raw", "" + mDeNormalizedUrl +" > " + data);

        JsonNode node = Json.parse(data);
        Log.d("output", "" + node);
        Log.d("request-response", "" + node + "; "+mDeNormalizedUrl);

        O bean = getOutput(node);

        return Response.success( bean,  HttpHeaderParser.parseCacheHeaders(response));
    }



    @Override
    protected void deliverResponse(O response) {
        if(mResponseListener!=null) {
            mResponseListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if(mErrorListener!=null){
            mErrorListener.onErrorResponse(error);
        }
    }
    protected final String getCookie(){
//        String cookieStr = mContext.getSharedPreferences(PREFERENCE_NAME,
//                Context.MODE_PRIVATE).getString(PREFERENCE_KEY_COOKIE, "");
//        String cookieStr = AccountPreference.getCookies(mContext);
//        return cookieStr;
        String strCookie = onSendCookie();
        return strCookie;
    }

    protected String onSendCookie(){
        return null;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new LinkedHashMap<String, String>( super.getHeaders() );

        String cookieStr = getCookie();
        if (cookieStr!=null && cookieStr.length() > 0) {
            headers.put("cookie", cookieStr);
            //if(Config.isDebug()) Log.d("cookie", "send cookie = " + cookieStr);
        }

        //if(Config.isDebug()) Log.d("cookie", DUtil.toStringMap(headers, false));
        //if(Config.isDebug()) Log.d("request-header", DUtil.toStringMap(headers, false) + "; "+mDeNormalizedUrl);

        return headers;
    }


    @Override
    public Priority getPriority() {
        return mPriority;
    }
}
