package jp.egg.android.request;

import java.util.concurrent.ExecutionException;

import jp.egg.android.request.in.EggRequestBody;
import jp.egg.android.request.out.EggResponseBody;
import jp.egg.android.request.volley.VolleyBaseRequest;
import jp.egg.android.task.EggTaskCentral;
import jp.egg.android.task.EggTaskCentral.LoadImageContainer;
import jp.egg.android.task.EggTaskCentral.LoadImageListener;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.RequestFuture;

public class EggRequestUtil {

	public static final void d(String tag, String msg){
		Log.d(tag, msg);
	}



	public static final void d_request(String url){
		Log.d("request", ""+url);
	}


	public static <O> EggRequestFuture<O> getFuture(EggRequestBody in, EggResponseBody<O> out){

		final EggTaskCentral c = EggTaskCentral.getInstance();

		RequestFuture<O> future = RequestFuture.newFuture();
		VolleyBaseRequest<O> request = new VolleyBaseRequest<O>(
				in,
				out,
				future,
				future
		);
		//Request<O>  = request;

		c.addVolleyRequestByObject(request, null);

		return EggRequestFuture.make( future );

	}

	public static <O> O get(EggRequestBody in, EggResponseBody<O> out){

		final EggTaskCentral c = EggTaskCentral.getInstance();

		RequestFuture<O> future = RequestFuture.newFuture();
		VolleyBaseRequest<O> request = new VolleyBaseRequest<O>(
				in,
				out,
				future,
				future
		);
		//Request<O>  = request;

		c.addVolleyRequestByObject(request, null);

		try {
			O response = future.get();

			return response;

		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}
		return null;

	}


	public static void cancelImage(final ImageView view){

		Object tag = view.getTag();

		if(tag!=null && tag instanceof LoadImageContainer){
			LoadImageContainer ic = (LoadImageContainer) tag;
			ic.cancelRequest();

			view.setTag(null);
		}

	}
	public static void displayImage(
			final ImageView view, String url,
			final int defaultImageResId
			){

		displayImage(view, url, defaultImageResId,
				view.getWidth(), view.getHeight());
	}

	public static void displayImage(
			final ImageView view, String url,
			final int defaultImageResId,
			int maxWidth, int maxHeight
			){

		cancelImage(view);

		final EggTaskCentral c = EggTaskCentral.getInstance();

		//final int errorImageResId = 0;
		//final int defaultImageResId = 0;


		LoadImageListener lis  = new LoadImageListener() {

			@Override
			public void onLoaded(Bitmap bmp) {
//                if (response.getBitmap() != null) {
//                    view.setImageBitmap(response.getBitmap());
//                } else {
//                    view.setImageResource(defaultImageResId);
//                }
			}

			@Override
			public void onError() {
				//view.setImageResource(errorImageResId);
			}
        };

        view.setImageResource(defaultImageResId);

        LoadImageContainer ic = c.displayImage(view, url, lis, maxWidth, maxHeight);

        //LoadImageContainer ic = c.loadImage(url, lis, maxWidth, maxHeight);
        view.setTag(ic);

		//c.addVolleyRequestByObject(new Imag, null);

	}





}
