/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.request.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;


/**
 * APIリクエスト基底クラス。 (POSTしか無いAPIの為、その前提で。)
 */
public abstract class BaseVolleyRequest<T> extends Request<T> {

    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("application/json; charset=%s", PROTOCOL_CHARSET);


	//private Context mContext;
	//private HttpResponseListener<T> mListener;
	private Listener<T> mResponseListener;
	//private ErrorListener mErrorListener;
	private Object mRequestBody;

	//private RequestFuture<T> mFuture;

//	/**
//	 * @param url
//	 *            {@link String}
//	 * @param listener
//	 *            {@link HttpResponseListener}
//	 */
//	public BaseVolleyRequest(int method, String url, String b,HttpResponseListener<T> listener) {
//		this(method, url, listener, listener);
//	}

	/**
	 * @param url
	 *            {@link String}
	 * @param listener
	 *            {@link HttpResponseListener}
	 */
	public BaseVolleyRequest(int method, String url, Object body, Listener<T> response, ErrorListener error) {
		super(method, url, error);
		mResponseListener = response;
		mRequestBody = body;
		//mErrorListener = error;
	}

//	/**
//	 * @param url
//	 *            {@link String}
//	 * @param future
//	 *            {@link RequestFuture}
//	 */
//	public BaseVolleyRequest(int method, String url, RequestFuture<T> future) {
//		super(Request.Method.POST, url, future);
//		mFuture = future;
//	}

	/**
	 * POSTパラメータを準備する。
	 *
	 * @return Key=Value
	 */
	protected Map<String, String> prepareParams(){
		if( mRequestBody == null ) return null;
		if( mRequestBody instanceof Map ){
			try{
				Map<String, String> map = (Map) mRequestBody;
				return map;
			}catch(Exception ex){
				throw new IllegalArgumentException("Can not use this map.", ex);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.android.volley.Request#deliverResponse(java.lang.Object)
	 */
	@Override
	protected void deliverResponse(T response) {
//		if (mListener != null) {
//			mListener.onResponse(response);
//		}
//		if (mFuture != null) {
//			mFuture.onResponse(response);
//		}
		if(mResponseListener!=null){
			mResponseListener.onResponse(response);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.android.volley.Request#getParams()
	 */
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = prepareParams();
		return params;
	}



    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);


    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
        	if(mRequestBody == null) return null;
        	if(mRequestBody instanceof String){
        		String str = (String) mRequestBody;
                return str.getBytes(PROTOCOL_CHARSET);
        	}
        	throw new IllegalArgumentException("謎なrequestBody.");
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }













//	/**
//	 * レスポンスを作成する。
//	 *
//	 * @param response
//	 *            {@link NetworkResponse}
//	 * @return {@link Response}
//	 */
//	protected Response<JSONObject> parseNetworkResponseToJSONObject(NetworkResponse response) {
//		DUtil.d_request(getUrl(), prepareParams());
//
//		String str = null;
//
//		final int statusCode = response.statusCode;
//		final byte[] datas = response.data;
//
//		if (statusCode < 400 && datas != null) {
//			str = responseDataToString(datas);
//		} else {
//			DUtil.e("ApiBaseRequest#parseNetworkResponseToJSONObject",
//					"statusCode=" + statusCode, null);
//			str = responseDataToString(datas);
//		}
//
//		JSONObject json = null;
//
//		if (str != null) {
//			try {
//				json = new JSONObject(str);
//			} catch (JSONException e) {
//				DUtil.e("ApiBaseRequest#parseNetworkResponseToJSONObject",
//						e.getMessage(), e);
//			}
//		}
//
//		if (json != null) {
//			return Response.success(json,
//					HttpHeaderParser.parseCacheHeaders(response));
//		} else {
//			return Response.error(new VolleyError("statusCode=" + statusCode
//					+ " data=" + str));
//		}
//	}
//
//	/**
//	 * レスポンスを文字列にする。
//	 *
//	 * @param data
//	 *            レスポンスバイト配列
//	 * @return レスポンス文字列
//	 */
//	protected static String responseDataToString(byte[] data) {
//		try {
//			return new String(data, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			return null;
//		}
//	}



}
