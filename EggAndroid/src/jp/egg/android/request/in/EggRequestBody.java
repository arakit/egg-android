package jp.egg.android.request.in;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class EggRequestBody {





	private int mMethod;
	private String mUrl;

	public EggRequestBody(int method, String url) {
		mMethod = method;
		mUrl = url;
	}

//	private boolean mIsSetUped;

//	protected final void setUp(){
//
//		onSetup();
//
//		mIsSetUped = true;
//	}

//	//sうてーと
//	public final boolean isSetUped(){
//		return mIsSetUped;
//	}


	//セット
	public void setUrl(String url){
		mUrl = url;
	}
	public void setMethod(int method){
		mMethod = method;
	}

	//ベース処理系
	protected final int prepareMethod(){
		int method = mMethod;
		return onPrepareMethod(method);
	}
	protected final String prepareUrl(){
		String url = mUrl;
		return onPrepareUrl(url);
	}





    public final String getBodyContentType() {
        //return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    	return onBodyContentType();
    }


    public int getMethod(){
    	return prepareMethod();
    }

	public final String getUrl(){
		///if(!isSetUped()) throw new IllegalStateException();
		return prepareUrl();
	}
	public final InputStream getBody(){
		//if(!isSetUped()) throw new IllegalStateException();
		return onBody();
	}
//	public final Map<String, String> getParams(){
//		if(!isSetUped()) throw new IllegalStateException();
//		return m
//	}



	//イベント系

//	protected void onSetup(){
//		mMethod = prepareMethod();
//		mUrl = prepareUrl();
//	}

	//URL
	protected abstract String onPrepareUrl(String url);
	//メソッド
	protected abstract int onPrepareMethod(int method);


	//コンテンツ系
	protected abstract InputStream onBody();
	protected abstract String onBodyContentType();





















//    /**
//     * Returns the raw POST or PUT body to be sent.
//     *
//     * @throws AuthFailureError in the event of auth failure
//     */
//    public byte[] getBody() throws AuthFailureError {
//        Map<String, String> params = getParams();
//        if (params != null && params.size() > 0) {
//            return encodeParameters(params, getParamsEncoding());
//        }
//        return null;
//    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    protected byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

}
