package jp.egg.android.request.in;

import java.util.Map;
import java.util.Map.Entry;

public class EggDefaultParamsRequestBody extends EggParamsRequestBody{


	private Map<String, Object> mDefaultParams;


	public EggDefaultParamsRequestBody(int method, String url, Map<String, Object> params) {
		super(method, url);
		mDefaultParams = params;
	}


	//イベント系

	@Override
	protected Map<String, String> onPrepareParams(Map<String, String> params){
		if(mDefaultParams!=null){
			for(Entry<String, Object> e : mDefaultParams.entrySet()){
				params.put(e.getKey(), e.getValue()!=null?e.getValue().toString() : null);
			}
		}
		return params;
	}



}
