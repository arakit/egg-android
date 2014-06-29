package jp.egg.android.request;

import java.util.concurrent.ExecutionException;

import com.android.volley.toolbox.RequestFuture;

public class EggRequestFuture <O> {


	private final RequestFuture<O> mRequestFuture;

	public final static <O> EggRequestFuture<O> make(RequestFuture<O> future){
		return new EggRequestFuture<O>(future);
	}


	private EggRequestFuture(RequestFuture<O> f){
		mRequestFuture = f;
	}

	public void cancel(){
		mRequestFuture.cancel(true);
	}
	public O get(){
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
