package jp.egg.android.request;

import java.util.Map;

import com.android.volley.Request.Method;
import com.fasterxml.jackson.databind.JsonNode;

public class EggSimpleJacksonRequest extends EggRequest<JsonNode>{


	private String mUrl;



	public static EggSimpleJacksonRequest newGetRequest(String url){
		EggSimpleJacksonRequest r = new EggSimpleJacksonRequest();

		r.mUrl = url;

		r.setUp();
		return r;
	}



	@Override
	protected Map<String, String> onPrepareParams(Map<String, String> params) {
		return null;
	}

	@Override
	protected String onPrepareUrl(String url) {
		return mUrl;
	}

	@Override
	protected int onPrepareMethod(int method) {
		return Method.GET;
	}

}
