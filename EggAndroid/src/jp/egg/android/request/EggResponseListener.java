package jp.egg.android.request;

import jp.egg.android.task.EggTaskError;
import jp.egg.android.task.EggTaskListener;

public interface EggResponseListener<S, E extends EggTaskError> extends EggTaskListener<S, E> {

    //public void onSuccess(S response);
    //public void onError(E error);


}
