package jp.egg.android.app.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.egg.android.app.model.entities.TestGoogleImageSearchResult;
import jp.egg.android.request.EggParamsToJsonNodeApi;
import jp.egg.android.util.Json;

import com.android.volley.Request.Method;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author chikara funabashi
 *
 * <request data, response data, save model>
 */
public class TestGoogleImageSearchApi extends EggParamsToJsonNodeApi<String , List<TestGoogleImageSearchResult>>{


	public static final String URL = "http://ajax.googleapis.com/ajax/services/search/images";

	//v=1.0&rsz=8&safe=off&q=hatsune

	public static final TestGoogleImageSearchApi newInstance(String q){
		TestGoogleImageSearchApi api = new TestGoogleImageSearchApi();
		api.setupInput(q);
		return api;
	}

	private TestGoogleImageSearchApi() {
//		setup(
//				inputParams(Method.GET, URL),
//				outputJsonNode()
//		);
		setup(Method.GET, URL);
	}



	@Override
	protected Map<String, Object> onRequestData(String data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("v", "1.0");
		map.put("rsz", 8);
		map.put("safe", "off");
		map.put("q", data);
		return map;
	}




	@Override
	protected List<TestGoogleImageSearchResult> onParseResponse(JsonNode response) {
		JsonNode results = response.get("responseData"). get("results");
		List<TestGoogleImageSearchResult> ret = new ArrayList<TestGoogleImageSearchResult>();
		for(int i=0;i<results.size();i++){
			ret.add( Json.fromJson(results.get(i), TestGoogleImageSearchResult.class) );
		}
		return ret;
	}

	@Override
	protected boolean onSave(List<TestGoogleImageSearchResult> data) {
		for(int i=0;i<data.size();i++){
			data.get(i).save();
		}
		return true;
	}




}
