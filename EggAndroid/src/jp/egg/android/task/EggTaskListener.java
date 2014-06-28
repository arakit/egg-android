package jp.egg.android.task;

public interface EggTaskListener<S, E extends EggTaskError> {


	public void onSucess(S response);
	public void onError(EggTaskError error);
	public void onCancel();

}
