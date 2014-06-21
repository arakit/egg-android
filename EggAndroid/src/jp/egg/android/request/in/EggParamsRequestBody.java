package jp.egg.android.request.in;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request.Method;

public abstract class EggParamsRequestBody extends EggRequestBody{

    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    protected static final String DEFAULT_PARAMS_ENCODING = "UTF-8";






	//private Map<String, String> mDefaultParams;


	public EggParamsRequestBody(int method, String url) {
		super(method, url);
		//mParams = params;
	}
//	public EggParamsRequestBody(int method, String url, Map<String, String>) {
//		super(method, url);
//		//mParams = params;
//	}



	//ベース処理系
	protected final Map<String, String> prepareParams(){
		Map<String, String> params = new HashMap<String, String>();
		return onPrepareParams(params);
	}

    protected final String getParamsEncoding() {
        return onParamsEncoding();
    }


	//イベント系

	protected abstract Map<String, String> onPrepareParams(Map<String, String> params);

	protected String onParamsEncoding(){
		return DEFAULT_PARAMS_ENCODING;
	}

	@Override
	protected String onBodyContentType() {
		int method = getMethod();
		if(method == Method.GET){
			return null;
		}else{
			return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
		}
	}

//	@Override
//	protected void onSetup() {
//		super.onSetup();
//
//		mParams = prepareParams();
//
//	}


	@Override
	protected String onPrepareUrl(String url) {

		return url;
	}


	@Override
	protected InputStream onBody() {
		int method = getMethod();
		if( method == Method.GET){
			return null;
		}else{
			Map<String, String> params = prepareParams();
			byte[] buf = encodeParameters(params, getParamsEncoding());
			return new ByteArrayInputStream(buf);
		}
	}

	@Override
	protected int onPrepareMethod(int method) {
		return method;
	}







}
