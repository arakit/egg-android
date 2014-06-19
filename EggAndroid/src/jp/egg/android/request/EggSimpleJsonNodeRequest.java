package jp.egg.android.request;

import java.util.Map;

import jp.egg.android.request.in.EggParamsRequestBody;
import jp.egg.android.request.out.EggJsonNodeResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

public class EggSimpleJsonNodeRequest extends EggRequest<Map<String, String>, JsonNode>{

	public static EggSimpleJsonNodeRequest newInstance(final int method, final String url, final Map<String, String> params){
		EggSimpleJsonNodeRequest r = new EggSimpleJsonNodeRequest();

		EggParamsRequestBody in = new EggParamsRequestBody(method, url){
			@Override
			protected Map<String, String> onPrepareParams(Map<String, String> def_params) {
				if(params!=null) def_params.putAll(params);
				return def_params;
			}

		};
		EggJsonNodeResponseBody out = new EggJsonNodeResponseBody(){

		};

		r.setRequestBody(in);
		r.setResponseBody(out);

		return r;
	}


//	public EggSimpleJsonNodeRequest() {
//
//	}




}
