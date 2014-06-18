/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.request;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jp.egg.android.util.DUtil;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;

/**
 * APIリクエスト基底クラス。 (POSTしか無いAPIの為、その前提で。)
 */
public abstract class EggBaseRequest<T> extends Request<T> {

	//private Context mContext;
	//private HttpResponseListener<T> mListener;

	private EggResponseListener<T> mResponseListener;
	private RequestFuture<T> mFuture;


	/**
	 * @param url
	 *            {@link String}
	 * @param listener
	 *            {@link HttpResponseListener}
	 */
	public EggBaseRequest(String url, EggResponseListener<T> listener) {
		super(Request.Method.POST, url, makeErrorListener(listener) );
		mResponseListener = listener;
	}

	/**
	 * @param url
	 *            {@link String}
	 * @param future
	 *            {@link RequestFuture}
	 */
	public EggBaseRequest(String url, RequestFuture<T> future) {
		super(Request.Method.POST, url, future);
		mFuture = future;
	}

	/**
	 * POSTパラメータを準備する。
	 *
	 * @return Key=Value
	 */
	protected Map<String, String> prepareParams(){
		HashMap<String, String> map = new HashMap<String, String>();
		return map;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.android.volley.Request#deliverResponse(java.lang.Object)
	 */
	@Override
	protected void deliverResponse(T response) {
		if (mResponseListener != null) {
			mResponseListener.onSucessResponse(response);
		} else if (mFuture != null) {
			mFuture.onResponse(response);
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

	/**
	 * レスポンスを作成する。
	 *
	 * @param response
	 *            {@link NetworkResponse}
	 * @return {@link Response}
	 */
	protected Response<JSONObject> parseNetworkResponseToJSONObject(NetworkResponse response) {
		DUtil.d_request(getUrl(), prepareParams());

		String str = null;

		final int statusCode = response.statusCode;
		final byte[] datas = response.data;

		if (statusCode < 400 && datas != null) {
			str = responseDataToString(datas);
		} else {
			DUtil.e("ApiBaseRequest#parseNetworkResponseToJSONObject",
					"statusCode=" + statusCode, null);
			str = responseDataToString(datas);
		}

		JSONObject json = null;

		if (str != null) {
			try {
				json = new JSONObject(str);
			} catch (JSONException e) {
				DUtil.e("ApiBaseRequest#parseNetworkResponseToJSONObject",
						e.getMessage(), e);
			}
		}

		if (json != null) {
			return Response.success(json,
					HttpHeaderParser.parseCacheHeaders(response));
		} else {
			return Response.error(new VolleyError("statusCode=" + statusCode
					+ " data=" + str));
		}
	}

	/**
	 * レスポンスを文字列にする。
	 *
	 * @param data
	 *            レスポンスバイト配列
	 * @return レスポンス文字列
	 */
	protected static String responseDataToString(byte[] data) {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}


	private final static ErrorListener makeErrorListener(final EggResponseListener<?> listener){
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				listener.onErrorResponse();
			}
		};
	}

}
