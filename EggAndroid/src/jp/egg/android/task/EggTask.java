package jp.egg.android.task;

public abstract class EggTask <S, E extends EggTaskError> {

	private boolean mIsCanceled = false;
	private boolean mIsStarted = false;
	private boolean mIsRunning = false;


	public final void cancel(){
		if(mIsCanceled) return ;
		mIsCanceled = true;
		onCancel();
	}
	public final void start(){
		if(mIsStarted) return ;
		mIsStarted = true;
		mIsRunning = true;
		onStart();
	}
	protected final void finish(){
		if(!mIsStarted) return ;
		mIsRunning = false;
	}







	//ステート系

	public final boolean isCanceled(){
		return mIsCanceled;
	}
	public final boolean isStarted(){
		return mIsStarted;
	}
	public final boolean isRunning(){
		return mIsRunning;
	}




	//イベント系

	/**
	 * キャンセルされたら呼ばれます。
	 * この中でキャンセル処理してください
	 *
	 */
	protected void onCancel(){

	}

	/**
	 * 処理を開始してください。
	 *
	 */
	protected void onStart(){

	}


	/**
	 *
	 *
	 */
	protected void onSuccess(S result){

	}

	/**
	 *
	 *
	 */
	protected void onError(E result){

	}

}
