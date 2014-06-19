package jp.egg.android.task;


public abstract class EggTask <S, E extends EggTaskError> {

	private boolean mIsCanceled = false;
	private boolean mIsStarted = false;
	private boolean mIsRunning = false;
	private boolean mIsError = false;



//  /** An event log tracing the lifetime of this request; for debugging. */
//  private final MarkerLog mEventLog = MarkerLog.ENABLED ? new MarkerLog() : null;


	public final void cancel(){
		if(mIsCanceled) return ;
		try{
			mIsCanceled = true;
			onCancel();
		}catch(Exception ex){
			mIsCanceled = false;
		}
	}
	public final void start(){
		if(mIsStarted) return ;
		try{
			mIsStarted = true;
			mIsRunning = true;
			onStart();
		}catch(Exception ex){
			error(null);
		}
	}

	//
	private final void finish(){
		if(!mIsStarted) return ;
		mIsRunning = false;
		onStop();
	}
	protected final void error(E error){
		if(!mIsStarted) return ;
		mIsError = true;
		onError(error);
		finish();
	}
	protected final void success(S success){
		if(!mIsStarted) return ;
		onSuccess(success);
		finish();
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
	public final boolean isError(){
		return mIsError;
	}


	//その他
    /**
     * Adds an event to this request's event log; for debugging.
     */
    public void addMarker(String tag) {
//        if (MarkerLog.ENABLED) {
//            mEventLog.add(tag, Thread.currentThread().getId());
//        } else if (mRequestBirthTime == 0) {
//            mRequestBirthTime = SystemClock.elapsedRealtime();
//        }
    }



	//イベント系

	/**
	 * キャンセルされたら呼ばれます。
	 * この中でキャンセル処理してください
	 *
	 */
	protected abstract void onCancel() throws FailedCancelExeption;

	/**
	 * 処理を開始してください。
	 *
	 */
	protected abstract void onStart() throws FailedStartExeption;

	/**
	 * 処理は終了します。
	 * onSucsess か onErroeの後
	 *
	 */
	protected abstract void onStop();


	/**
	 *
	 *
	 */
	protected abstract void onSuccess(S result);

	/**
	 *
	 *
	 */
	protected abstract void onError(E result);




}
