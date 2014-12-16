/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;
import jp.egg.android.ui.activity.EggBaseActivity;
import jp.egg.android.view.widget.actionbarpulltorefresh.PullToRefreshLayout;
import jp.egg.android.view.widget.layout.ParallaxListViewEx;
import uk.co.chrisjenx.paralloid.OnScrollChangedListener;

import java.util.HashSet;
import java.util.Set;

/**
 * フラグメント共通基底クラス。
 */
public abstract class EggBaseFragment extends Fragment {

    private Set<Object> mRefreshRequest = new HashSet<Object>();

    private PullToRefreshLayout mPullToRefreshLayout;


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
        EggBaseActivity.refreshActionBarBackground(getEggActivity());
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

    public EggBaseActivity getEggActivity(){
        FragmentActivity fragmentActivity = getActivity();
        if(fragmentActivity instanceof EggBaseActivity){
            return (EggBaseActivity) fragmentActivity;
        }else{
            return null;
        }
    }




    protected int getCurrentRefreshRequestCount () {
        return mRefreshRequest.size();
    }
    protected boolean hasRefreshRequest () {
        return getCurrentRefreshRequestCount() > 0;
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

    protected void onRefreshStateUpdate(boolean refreshing) {
        if(mPullToRefreshLayout!=null) {
            mPullToRefreshLayout.setRefreshing(refreshing);
        }
    }


    protected void setPullToRefreshLayout(PullToRefreshLayout layout){
        mPullToRefreshLayout = layout;
        getEggActivity().setUpRefreshBar(mPullToRefreshLayout, new Runnable() {
            @Override
            public void run() {
                onPullToRefresh();
            }
        });
    }
    protected void onPullToRefresh(){

    }









    ////






    private int mActionBarHeight;
    private int mParallaxHeaderHeight;
    private Drawable mActionBarBackgroundDrawable;
    private View mParallaxHeaderView;

    private void updateParallax(int scrollPosition){

        int headerHeight = mParallaxHeaderHeight - mActionBarHeight;
        double ratio = Math.min(Math.max(scrollPosition, 0), headerHeight) / (double)headerHeight;
        int newAlpha = (int) (ratio * 255);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        if(mParallaxHeaderView!=null) {
            if (ratio < 1.0) {
                mParallaxHeaderView.setVisibility(View.VISIBLE);
            } else {
                mParallaxHeaderView.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void setUpParallax(int abHeight, int parallaxHeaderHeight, ParallaxListViewEx listView, View headerView, final AbsListView.OnScrollListener listener){

        mActionBarHeight = abHeight;
        mParallaxHeaderHeight = parallaxHeaderHeight;
        mParallaxHeaderView = headerView;

        listView.parallaxViewBy(headerView, 0.5f);

        listView.setOnScrollChangeListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(Object who, int l, int t, int oldl, int oldt) {
                updateParallax(t);
            }
        });

        listView.addOnScrollLister(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listener != null) {
                    listener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //updateParallax(view.getSc);
                if (listener != null) {
                    listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }

        });


    }

    protected void initFadingActionBar(EggBaseActivity activity, int abDrawable) {

        if (mActionBarBackgroundDrawable == null) {
            mActionBarBackgroundDrawable = activity.getResources().getDrawable(abDrawable);
        }
        setActionBarBackgroundDrawable(activity, mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        mActionBarBackgroundDrawable.setAlpha(0);
    }

    protected void releaseFadingActionBar(){

    }

    private void setActionBarBackgroundDrawable(EggBaseActivity activity, Drawable drawable){
        activity.getSupportActionBar().setBackgroundDrawable(drawable);
    }


    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            setActionBarBackgroundDrawable(getEggActivity(), who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };


}
