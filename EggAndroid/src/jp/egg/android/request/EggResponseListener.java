package jp.egg.android.request;

public interface EggResponseListener<T> {


	public void onSucessResponse(T response);
	public void onErrorResponse();

}
