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

import java.util.HashSet;
import java.util.Set;

/**
 * フラグメント共通基底クラス。
 */
public abstract class EggBaseFragment extends Fragment {

    private Set<Object> mRefreshRequest = new HashSet<Object>();



    private boolean mIsStarted = false;
    private boolean mIsStopped = false;

    private boolean mIsDestroyed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean isDestroyed(){
        return mIsDestroyed;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
        cancelVolleyRequestInFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
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
        if(data!=null) intent.putExtras(data);
        activity.startActivityFromFragment(EggBaseFragment.this, intent, requestCode);
    }

    public void startActivityFromFragment(Class clazz){
        FragmentActivity activity = getActivity();
        if(activity == null) return;
        Intent intent = new Intent(activity, clazz);
        activity.startActivityFromFragment(EggBaseFragment.this, intent, 0);
    }





    protected void updateRefreshRequest() {
        if( mRefreshRequest.size() > 0 ){
            startRefresh();
        }else{
            finishedRefresh();
        }
    }

    public Object newStartRefreshRequest() {
        Object tag = new Object();
        startRefreshRequest(tag);
        return tag;
    }
    public void startRefreshRequest(Object request) {
        mRefreshRequest.add(request);
        updateRefreshRequest();
    }
    public void finishRefreshRequest(Object request) {
        mRefreshRequest.remove(request);
        updateRefreshRequest();
    }

    protected void startRefresh(){
        onRefreshStateUpdate(true);
    }

    protected void finishedRefresh(){
        onRefreshStateUpdate(false);
    }

    protected void onRefreshStateUpdate(boolean refreshing){

    }

}
