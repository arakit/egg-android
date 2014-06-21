package jp.egg.android.request.in;

import java.util.Map;

public class EggDefaultParamsRequestBody extends EggParamsRequestBody{


	private Map<String, String> mDefaultParams;


	public EggDefaultParamsRequestBody(int method, String url, Map<String, String> params) {
		super(method, url);
		mDefaultParams = params;
	}


	//イベント系

	protected Map<String, String> onPrepareParams(Map<String, String> params){
		if(mDefaultParams!=null){
			params.putAll(params);
		}
		return params;
	}


}
