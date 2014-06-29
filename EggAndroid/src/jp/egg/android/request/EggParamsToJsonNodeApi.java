package jp.egg.android.request;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author chikara funabashi
 *
 * <request data, response data, save model>
 */
public abstract class EggParamsToJsonNodeApi<I, O> extends EggApiRequest<I , Map<String, Object>, JsonNode, O>{




	protected void setup(int method, String url) {
		setup(
				inputParams(method, url),
				outputJsonNode()
		);
	}



}
