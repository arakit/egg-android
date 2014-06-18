package jp.egg.android.task.central;

import jp.egg.android.request.volley.EggVolley;
import jp.egg.android.request.volley.VolleyTag;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;

public class EggTaskCentral {

	public static EggTaskCentral sInstance = null;

	public static EggTaskCentral initialize(Context context){
		if(sInstance != null) return sInstance;
		EggTaskCentral central = new EggTaskCentral();
		sInstance = central;
		central.onInitialize(context);
		return central;
	}
	public static void destroy(){
		EggTaskCentral central = sInstance;
		if(central == null) return ;
		central.onDestroy();
		sInstance = null;
	}
	public static EggTaskCentral getInstance(){
		if(sInstance == null) throw new IllegalStateException("not initialize. must call EggTaskCentral.initialize().");
		return sInstance;
	}



	//インスタンス

	private Context mContext;
	private RequestQueue mQueue;


	private EggTaskCentral() {

	}


	private void onInitialize(Context context){
		mContext = context.getApplicationContext();
		mQueue = EggVolley.newRequestQueue(mContext, 5 * 1024 * 1024);
		startRequest();
	}
	private void onDestroy(){
		mContext = null;
		cancelRquestAll();
		stopRquest();
	}

	public void startRequest(){
		mQueue.start();
	}
	public void stopRquest(){
		mQueue.stop();
	}
	public void cancelRquest(RequestFilter filter){
		mQueue.cancelAll(filter);
	}
	public void cancelRquestByObject(final Object obj){
		mQueue.cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				if(request.getTag()!=null && request.getTag() instanceof VolleyTag ){
					return ((VolleyTag) request.getTag()).object == obj;
				}
				return false;
			}
		});
	}
	public void cancelRquestAll(){
		mQueue.cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
	}


//	public void addVolleyRequestByActivity(Request<?> request, Activity activity){
//		if(request == null) return ;
//
//		VolleyTag tag = new VolleyTag();
//		tag.activity = activity;
//		request.setTag(tag);
//
//		mQueue.add(request);
//
//	}
//
//	public void addVolleyRequestByFragment(Request<?> request, Fragment fragment){
//		if(request == null) return ;
//
//		VolleyTag tag = new VolleyTag();
//		tag.fragment = fragment;
//		request.setTag(tag);
//
//		mQueue.add(request);
//
//	}

	public void addVolleyRequestByObject(Request<?> request, Object obj){
		if(request == null) return ;

		VolleyTag tag = new VolleyTag();
		tag.object = obj;
		request.setTag(tag);

		mQueue.add(request);

	}



}
