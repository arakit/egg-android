package jp.egg.android.request;

import jp.egg.android.task.EggTaskError;

public class EggRequestError extends EggTaskError {

    public String message;

    public EggRequestError(String messsage) {
        this.message = messsage;
    }

}
