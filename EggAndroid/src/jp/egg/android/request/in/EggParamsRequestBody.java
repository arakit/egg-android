package jp.egg.android.request.in;

import android.net.Uri;

import com.android.volley.Request.Method;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class EggParamsRequestBody extends EggRequestBody<Map<String, Object>> {

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
    protected final Map<String, String> prepareParams() {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, Object> data = getData();
        if (data != null) {
            for (Entry<String, Object> e : data.entrySet()) {
                params.put(e.getKey(), e.getValue() != null ? e.getValue().toString() : null);
            }
        }
        return onPrepareParams(params);
    }

    protected final String getParamsEncoding() {
        return onParamsEncoding();
    }


    //イベント系

    protected abstract Map<String, String> onPrepareParams(Map<String, String> params);

    protected String onParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    @Override
    protected String onBodyContentType() {
        int method = getMethod();
        if (method == Method.GET) {
            return null;
        } else {
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
        Uri uri = Uri.parse(url);
        if (prepareMethod() == Method.GET) {
            Uri.Builder builder = uri.buildUpon();
            Map<String, String> params = prepareParams();
            for (Entry<String, String> e : params.entrySet()) {
                builder.appendQueryParameter(e.getKey(), e.getValue());
            }
            return builder.build().toString();
        } else {
            return uri.toString();
        }
    }


    @Override
    protected InputStream onBody() {
        int method = getMethod();
        if (method == Method.GET) {
            return null;
        } else {
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
