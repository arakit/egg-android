package jp.egg.android.request;

import java.util.Map;

import jp.egg.android.db.EggDB;
import jp.egg.android.request.in.EggDefaultParamsRequestBody;
import jp.egg.android.request.in.EggRequestBody;
import jp.egg.android.request.out.EggDefaultJsonNodeResponseBody;
import jp.egg.android.request.out.EggResponseBody;
import jp.egg.android.task.EggTask;

public abstract class EggApiRequest<I1, I2, O1, O2> extends EggTask<O2, EggRequestError> {

	// private boolean mIsSetUped = false;

	//private Request<?> mVollayRequest;
	private EggRequestFuture<O1> mRequestFuture;

	private EggRequestBody<I2> mRequestBody;
	private EggResponseBody<O1> mResponseBody;

	private I1 mDefaultInputData;

	protected EggApiRequest() {

	}

	protected void setup(EggRequestBody<I2> reqBody, EggResponseBody<O1> resBody) {
		mRequestBody = reqBody;
		mResponseBody = resBody;
	}

	protected void setupInput(I1 input){
		mDefaultInputData = input;
	}



//	protected EggApiRequest(EggRequestBody reqBody, EggResponseBody<O1> resBody) {
//		mRequestBody = reqBody;
//		mResponseBody = resBody;
//	}

//	// ベース系
//	public final void setRequestBody(EggRequestBody reqBody) {
//		mRequestBody = reqBody;
//	}
//
//	public final void setResponseBody(EggResponseBody<O> resBody) {
//		mResponseBody = resBody;
//	}






	@Override
	protected void onDoInBackground() {
		//final EggTaskCentral c = EggTaskCentral.getInstance();

		////////

		try{
			I2 requestData = onRequestData(mDefaultInputData);
			O1 response = onRequest(requestData);
			if( isCanceled() ) return ;
			if(response == null) throw new FaildRequestExeprion("");

			O2 parseed = onParseResponse(response);
			if( isCanceled() ) return ;
			if(parseed == null) throw new FaildRequestExeprion("");

			boolean success_save = save(parseed);
			if( isCanceled() ) return ;
			if(!success_save) throw new FaildSaveExeprion("");

			setSucces(parseed);

		}catch(Exception ex){
			ex.printStackTrace();
			setError(new EggRequestError(ex.getMessage()));
		}

	}


	protected I2 onRequestData(I1 data){
		return null;
	}

	protected O1 onRequest(I2 input){
		if(mRequestBody!=null){
			mRequestBody.setData(input);
		}
		EggRequestFuture<O1> future = EggRequestUtil.getFuture(
				mRequestBody,
				mResponseBody
				);
		mRequestFuture = future;

		return future.get();
	}

//	protected O2 onParseResponse(O1 response){
////		ObjectMapper om = new ObjectMapper();
////		JsonNode results = jn.get("responseData"). get("results");
//		return null;
//	}
	protected abstract O2 onParseResponse(O1 response);

	protected final boolean save(O2 data) {
		EggDB.beginTransaction();
		try{
			boolean success = onSave(data);
			if(!success) return false;

			EggDB.setTransactionSuccessful();
			return true;
		}
		catch(Exception ex){
			return false;
		}
		finally{
			EggDB.endTransaction();
		}
	}
	protected boolean onSave(O2 data){
		return true;
	}



	@Override
	protected void onRequestCancel() {
		super.onRequestCancel();

		if (mRequestFuture == null)
			return;

		mRequestFuture.cancel();

//		final EggTaskCentral c = EggTaskCentral.getInstance();
//		c.cancelVolleyRquest(new RequestFilter() {
//			@Override
//			public boolean apply(Request<?> request) {
//				return request == mVollayRequest;
//			}
//		});

	}



	private static class FaildRequestExeprion extends RuntimeException{
		public FaildRequestExeprion(String message) {
			super(message);
		}
	}
	private static class FaildSaveExeprion extends RuntimeException{
		public FaildSaveExeprion(String message) {
			super(message);
		}
	}








//	public static class Config{
//		public String url;
//		public int method;
//		private EggRequestBody<?> input;
//		private EggResponseBody<?> output;
//	}
//
//
//	public static class Builder{
//
//		private String mUrl;
//		private int mMethod;
//
//		private Map<String, Object> mParams;
//
//		private EggRequestBody<?> mRequestInput;
//		private EggResponseBody<?> mRequestOutput;
//
//
//		public Builder url(String url){
//			mUrl = url;
//			return this;
//		}
//		public Builder method(int method){
//			mMethod = method;
//			return this;
//		}
//
//		public Builder requestInput(EggRequestBody in){
//			mRequestInput = in;
//			return this;
//		}
//		public Builder requestOutput(EggResponseBody<?> out){
//			mRequestOutput = out;
//			return this;
//		}
//
//
//		public Builder putParams(Map<String, Object> params){
//			getParams().putAll(params);
//			return this;
//		}
//		public Builder putParam(String key, Object value){
//			getParams().put(key, value);
//			return this;
//		}
//
//
//
//
//
//		public EggApiRequest<Object, Model> build(){
//			//EggApiRequest<O1, Model>
//			return null;
//		}
//
//
//
//
//		protected String getUrl(){
//			return mUrl;
//		}
//		protected int getMethod(){
//			return mMethod;
//		}
//
//		protected Map<String, Object> getParams(){
//			if(mParams==null) mParams = new HashMap<String, Object>();
//			return mParams;
//		}
//
//	}




	public static EggDefaultJsonNodeResponseBody outputJsonNode(){
		return new EggDefaultJsonNodeResponseBody();
	}
	public static EggDefaultParamsRequestBody inputParams(int method, String url){
		return new EggDefaultParamsRequestBody(method, url, null);
	}
	public static EggDefaultParamsRequestBody inputParams(int method, String url, Map<String, Object> params){
		return new EggDefaultParamsRequestBody(method, url, params);
	}



}
