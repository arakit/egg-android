package jp.egg.android.request;

import java.util.concurrent.ExecutionException;

import jp.egg.android.request.in.EggRequestBody;
import jp.egg.android.request.out.EggResponseBody;
import jp.egg.android.request.volley.VolleyBaseRequest;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;

import com.android.volley.Request;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.toolbox.RequestFuture;

public abstract class EggRequest<I, O> extends EggTask<O, EggRequestError> {

	// private boolean mIsSetUped = false;

	private Request<?> mVollayRequest;

	private EggRequestBody mRequestBody;
	private EggResponseBody<O> mResponseBody;

	protected EggRequest() {

	}

	protected EggRequest(EggRequestBody reqBody, EggResponseBody<O> resBody) {
		mRequestBody = reqBody;
		mResponseBody = resBody;
	}

	// ベース系
	public final void setRequestBody(EggRequestBody reqBody) {
		mRequestBody = reqBody;
	}

	public final void setResponseBody(EggResponseBody<O> resBody) {
		mResponseBody = resBody;
	}

	// その他スーパイベント

	@Override
	protected void onCancel() {
		if (mVollayRequest == null)
			return;
		final EggTaskCentral c = EggTaskCentral.getInstance();
		c.cancelVolleyRquest(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return request == mVollayRequest;
			}
		});
	}

//	@Override
//	protected void onStart() {
//		// if(!mIsSetUped){
//		// setError(null);
//		// return ;
//		// }
//
//	}

	@Override
	protected void onDoInBackground() {
		final EggTaskCentral c = EggTaskCentral.getInstance();

		RequestFuture<O> future = RequestFuture.newFuture();
		VolleyBaseRequest<O> request = new VolleyBaseRequest<O>(mRequestBody,
				mResponseBody, future, future);
		mVollayRequest = request;

		c.addVolleyRequestByObject(request, null);

		try {
			O response = future.get();

			setSucces(response);

			return;

		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}
		setError(null);
	}

	@Override
	protected void onSuccess(O result) {

	}

	@Override
	protected void onError(EggRequestError result) {

	}

	@Override
	protected void onStopTask() {

	}

}
