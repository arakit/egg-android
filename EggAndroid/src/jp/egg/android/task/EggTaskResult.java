package jp.egg.android.task;

public class EggTaskResult <S, E extends EggTaskError>{


	private final boolean is_error;

	public final S result;
	public final E error;

	public EggTaskResult(S result) {
		this.is_error = false;
		this.result = result;
		this.error = null;
	}
	public EggTaskResult(E error) {
		this.is_error = true;
		this.result= null;
		this.error = error;
	}

	public boolean isError(){
		return is_error;
	}
	public boolean isSuccess(){
		return !is_error;
	}

	public S getResult(){
		return result;
	}

	public E getError(){
		return error;
	}

}
