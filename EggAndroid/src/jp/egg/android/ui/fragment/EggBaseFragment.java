/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.egg.android.ui.activity.EggBaseActivity;

/**
 * フラグメント共通基底クラス。
 */
public abstract class EggBaseFragment extends Fragment {

    private boolean mIsStarted = false;
    private boolean mIsStopped = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelVolleyRequestInFragment();
    }

	@Override
	public void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
	}

    @Override
    public void onStart() {
        super.onStart();
        mIsStarted = true;
        mIsStopped = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStopped = true;
        mIsStarted = false;
    }



	public void addTask(EggTask<?,?> task){
		EggTaskCentral.getInstance().addTask(task);
	}

	public void addTaskInFragment(EggTask<?,?> task){
		EggTaskCentral.getInstance().addTask(task);
	}


	public void cancelTaskInFragment(){
		//TODO
	}

    public void addVolleyRequest(Request request){
        EggTaskCentral.getInstance().addVolleyRequestByObject(request, null);
    }

    public void addVolleyRequestInFragment(Request request){
        EggTaskCentral.getInstance().addVolleyRequestByObject(request, EggBaseFragment.this);
    }

    public void cancelVolleyRequestInFragment(){
        EggTaskCentral.getInstance().cancelVolleyRquestByObject(EggBaseFragment.this);
    }

    public String getDefaultTitle(){
        return null;
    }


    protected boolean isStarted(){
        return mIsStarted;
    }
    protected boolean isStopped(){
        return mIsStopped;
    }


    public void refreshActionBarBackground(){
        EggBaseActivity.refreshActionBarBackground(getActivity());
    }

    public void finishActivity(){
        Activity activity = getActivity();
        if(activity == null) return;
        activity.finish();
    }

    public void finishActivityResultOK(Intent data){
        Activity activity = getActivity();
        if(activity == null) return;
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }
    public void finishActivityResultCanceled(Intent data){
        Activity activity = getActivity();
        if(activity == null) return;
        activity.setResult(Activity.RESULT_CANCELED, data);
        activity.finish();
    }

    public void startActivityFromFragment(Intent intent, int requestCode){
        FragmentActivity activity = getActivity();
        if(activity == null) return;
        activity.startActivityFromFragment(EggBaseFragment.this, intent, requestCode);
    }

    public void startActivityFromFragment(Class clazz, Bundle data, int requestCode){
        FragmentActivity activity = getActivity();
        if(activity == null) return;
        Intent intent = new Intent(activity, clazz);
        intent.putExtras(data);
        activity.startActivityFromFragment(EggBaseFragment.this, intent, requestCode);
    }

    public void startActivityFromFragment(Class clazz){
        FragmentActivity activity = getActivity();
        if(activity == null) return;
        Intent intent = new Intent(activity, clazz);
        activity.startActivityFromFragment(EggBaseFragment.this, intent, 0);
    }


}
