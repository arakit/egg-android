package jp.egg.android.request;

import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

public class EggRequestFuture<O> {


    private final RequestFuture<O> mRequestFuture;

    private EggRequestFuture(RequestFuture<O> f) {
        mRequestFuture = f;
    }

    public final static <O> EggRequestFuture<O> make(RequestFuture<O> future) {
        return new EggRequestFuture<O>(future);
    }

    public void cancel() {
        mRequestFuture.cancel(true);
    }

    public O get() {
        try {
            return mRequestFuture.get();
        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return null;
    }


}
