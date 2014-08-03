/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;


import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * フラグメント共通基底クラス。
 */
public abstract class EggBaseFragment extends Fragment {

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
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
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


}
