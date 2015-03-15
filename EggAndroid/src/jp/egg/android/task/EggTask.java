package jp.egg.android.task;

import jp.egg.android.util.DUtil;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;


public abstract class EggTask <S, E extends EggTaskError> implements Comparable<EggTask<S, E>> {


    private enum ResultState{
        none,
        success,
        error,
        cancel,
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Thread mExecutingThread = null;


    private boolean mIsCanceled = false;
    private boolean mIsStarted = false;
    private boolean mIsRunning = false;
    private boolean mIsStopped = false;

    private ResultState mResultState = ResultState.none;
    private S mSuccess;
    private E mError;


    /** Sequence number of this request, used to enforce FIFO ordering. */
    private Long mSequence;


    private EggTaskListener<S, E> mListener;

//  /** An event log tracing the lifetime of this request; for debugging. */
//  private final MarkerLog mEventLog = MarkerLog.ENABLED ? new MarkerLog() : null;


    public final void setOnListener(EggTaskListener<S, E> lis){
        mListener = lis;
    }


    public final void requestCancel(){
        if(mIsCanceled) return ;
        mIsCanceled = true;
        onRequestCancel();
    }

//	public final void cancel(){
//		if(mIsCanceled) return ;
//		try{
//			mIsCanceled = true;
//			onCancel();
//		}catch(Exception ex){
//			mIsCanceled = false;
//		}
//	}


    final void setSequence(){
        mSequence = SystemClock.uptimeMillis();
    }

    final void start(){
        if(mIsStarted || mIsStopped) return ;
        mIsStarted = true;
        onStartTask();
    }
    final void stop(){
        if(!mIsStarted || mIsStopped) return ;
        mIsStopped = true;
        onStopTask();
    }

    final void postStart(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onStopTask();
            }
        });
    }

    final void postStop(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onStopTask();
            }
        });
    }


    public final void execute(){
        if(!mIsStarted || mIsStopped) return ;
        DUtil.d("test", "execute");
        mExecutingThread = Thread.currentThread();
        try{
            mIsStarted = true;
            mIsRunning = true;
            onDoInBackground();
            finish();
            return ;
        }catch(Exception ex){
            errorFinish(null);
            return ;
        }
    }



    //
    private final void finish(){
        if(!mIsStarted) return ;
        mIsRunning = false;

        switch(mResultState){
            case success : {
                final S success = mSuccess;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(success);
                        if(mListener!=null){
                            mListener.onSuccess(success);
                        }
                    }
                });
            } break;
            case error : {
                final E error = mError;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(error);
                        if(mListener!=null){
                            mListener.onError(error);
                        }
                    }
                });
            } break;
            case cancel : {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onCancel();
                        if(mListener!=null){
                            mListener.onCancel();
                        }
                    }
                });
            } break;
            default: {
                //
            } break;
        }


    }
    //	private final boolean finishIfError(){
//		if( isError() ){
//			finish();
//			return true;
//		}
//		return false;
//	}
    private final void errorFinish(E error){
        setError(error);
        finish();
    }
//	private final void successFinish(S success){
//		setSucces(success);
//		finish();
//	}
//	private final void successFinish(S success){
//		if(!mIsStarted) return ;
//		if(isNotResultYet()) setSucces(success);
//		onSuccess(success);
//		finish();
//	}

    protected final void setSucces(S success){
        mResultState = ResultState.success;
        mSuccess = success;
    }
    protected final void setError(E error){
        mResultState = ResultState.error;
        mError = error;
    }
    protected final void setCancel(){
        mResultState = ResultState.cancel;
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
        return mResultState == ResultState.error;
    }
    public final boolean isSuccess(){
        return mResultState == ResultState.success;
    }
    public final boolean isNotResultYet(){
        return mResultState == ResultState.none;
    }

    public final E getError(){
        if(!isError()) return null;
        return mError;
    }
    public final S getSuccess(){
        if(!isSuccess()) return  null;
        return mSuccess;
    }

    public final EggTaskResult<S, E> getResult(){
        if(isSuccess()){
            return new EggTaskResult<S, E>(getSuccess());
        }else if(isError()){
            return new EggTaskResult<S, E>(getError());
        }
        return null;
    }


    //その他
    /**
     * Adds an event to this request's event log; for debugging.
     */
    public final void addMarker(String tag) {
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
    protected void onCancel(){

    }

    /**
     * 処理を開始してください。
     *
     */
    protected void onStartTask() {

    }

    /**
     * 処理は終了します。
     * onSucsess か onErroeの後
     *
     */
    protected void onStopTask(){

    }

    /**
     *
     */
    protected void onDoInBackground() {

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



//	public void onQueue(){
//		mSequence = SystemClock.uptimeMillis();
//	}


    protected void onRequestCancel(){
        if( mExecutingThread!=null ){
            mExecutingThread.interrupt();
        }
    }


    /**
     * Priority values.  Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * Returns the {@link Priority} of this request; {@link Priority#NORMAL} by default.
     */
    public final Priority getPriority() {
        return Priority.NORMAL;
    }

    //

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @Override
    public int compareTo(EggTask<S, E> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        if(left == right){
            if( this.mSequence == other.mSequence ){
                return 0;
            }else if( this.mSequence > other.mSequence ){
                return +1;
            }else{
                return -1;
            }
        }else if(right.ordinal() > left.ordinal()){
            return +1;
        }else{
            return -1;
        }

//        // High-priority requests are "lesser" so they are sorted to the front.
//        // Equal priorities are sorted by sequence number to provide FIFO ordering.
//        return left == right ?
//                this.mSequence > other.mSequence :
//                right.ordinal() - left.ordinal();
    }

}
