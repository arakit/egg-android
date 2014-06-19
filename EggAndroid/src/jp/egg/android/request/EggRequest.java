package jp.egg.android.request;

import java.util.HashMap;
import java.util.Map;

import jp.egg.android.request.volley.JacksonRequest;
import jp.egg.android.request.volley.RequestVolleyHelper;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.central.EggTaskCentral;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class EggRequest<S> extends EggTask<S, EggRequestError>{


	private boolean mIsSetUped = false;

	private int mMethod;
	private String mUrl;
	private Map<String, String> mParams;


	private Request<?> mVollayRequest;






	protected EggRequest() {

	}


	protected void setUp(){

		mMethod = prepareMethod();
		mUrl = prepareUrl();
		mParams = prepareParams();

		mIsSetUped = true;
	}


	//ベース処理系
	protected final int prepareMethod(){
		int method = Method.GET;
		return onPrepareMethod(method);
	}
	protected final String prepareUrl(){
		String url = null;
		return onPrepareUrl(url);
	}
	protected final Map<String, String> prepareParams(){
		Map<String, String> params = new HashMap<String, String>();
		return onPrepareParams(params);
	}



	//イベント系

	protected abstract Map<String, String> onPrepareParams(Map<String, String> params);
	protected abstract String onPrepareUrl(String url);
	protected abstract int onPrepareMethod(int method);


	//その他スーパイベント

	@Override
	protected void onCancel() {
		if( mVollayRequest == null ) return ;
		final EggTaskCentral c = EggTaskCentral.getInstance();
		c.cancelVolleyRquest(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return request == mVollayRequest;
			}
		});
	}

	@Override
	protected void onStart() {
		if(!mIsSetUped) error(null);

		final EggTaskCentral c = EggTaskCentral.getInstance();

		JacksonRequest request = new JacksonRequest(mMethod, mUrl,
				RequestVolleyHelper.requestBodyWithParamas(mParams),
				new Response.Listener<JsonNode>() {
					@Override
					public void onResponse(JsonNode response) {

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}
		);
		mVollayRequest = request;

		c.addVolleyRequestByObject(request, null);

	}

	@Override
	protected void onSuccess(S result) {

	}

	@Override
	protected void onError(EggRequestError result) {

	}

	@Override
	protected void onStop() {

	}






}
