package jp.egg.android.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;
import jp.egg.android.ui.fragment.EggBaseFragment;
import jp.egg.android.view.widget.actionbarpulltorefresh.ActionBarPullToRefresh;
import jp.egg.android.view.widget.actionbarpulltorefresh.Options;
import jp.egg.android.view.widget.actionbarpulltorefresh.PullToRefreshLayout;
import jp.egg.android.view.widget.actionbarpulltorefresh.listeners.OnRefreshListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chikara on 2014/07/10.
 */
public class EggBaseActivity extends FragmentActivity{

    private class CustomHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            deliveryCustomAction(msg);
        }
    }

    private Set<Object> mRefreshRequest = new HashSet<Object>();

    private PullToRefreshLayout mPullToRefreshLayout;

    private CustomHandler mCustomHandler = new CustomHandler();

    private final Set<OnAutoHideActionBarListener> mAutoHideActionBarListeners = new HashSet<OnAutoHideActionBarListener>();
    private final Set<OnCustomActionListener> mCustomActionListeners = new HashSet<OnCustomActionListener>();


    public interface OnAutoHideActionBarListener{
        public void onShowAutoHideActionBar();
        public void onHideAutoHideActionBar();
    }

    public interface OnCustomActionListener{
        public void onCustomAction(EggBaseActivity activity, Message message);
    }

    public void addAutoHideActionBarListener(OnAutoHideActionBarListener listener){
        mAutoHideActionBarListeners.add(listener);
    }
    public void removeAutoHideActionBarListener(OnAutoHideActionBarListener listener){
        mAutoHideActionBarListeners.remove(listener);
    }

    public void addCustomActionListener(OnCustomActionListener listener){
        mCustomActionListeners.add(listener);
    }
    public void removeCustomActionListener(OnCustomActionListener listener){
        mCustomActionListeners.remove(listener);
    }

    protected void notifyShowAutoHideActionBr(boolean visible){
        if(visible){
            for( OnAutoHideActionBarListener listener : mAutoHideActionBarListeners ){
                listener.onShowAutoHideActionBar();
            }
        }else{
            for( OnAutoHideActionBarListener listener : mAutoHideActionBarListeners ){
                listener.onHideAutoHideActionBar();
            }
        }
    }

    protected void deliveryCustomAction(Message msg){
        for( OnCustomActionListener listener : mCustomActionListeners ){
            listener.onCustomAction(this, msg);
        }
    }

    public final void sendCustomAction(Message msg){
        mCustomHandler.sendMessage(msg);
    }
    public final void sendCustomAction(int what, int arg1, int arg2, Object obj){
        mCustomHandler.sendMessage(
                mCustomHandler.obtainMessage(what, arg1, arg2, obj) );
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showAutoHideActionBar(){
        if(Build.VERSION.SDK_INT < 11) return;
        if( isShowingAutoHideActionBar() ) return;

        getActionBar().show();
        notifyShowAutoHideActionBr(true);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void hideAutoHideActionBar(){
        if(Build.VERSION.SDK_INT < 11) return;
        if( !isShowingAutoHideActionBar() ) return;

        getActionBar().hide();
        notifyShowAutoHideActionBr(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isShowingAutoHideActionBar(){
        if(Build.VERSION.SDK_INT < 11) return false;
        return getActionBar().isShowing();
    }


    public void addTask(EggTask<?,?> task){
        EggTaskCentral.getInstance().addTask(task);
    }

    public void addTaskInActivity(EggTask<?,?> task){
        EggTaskCentral.getInstance().addTask(task);
    }


    public void addVolleyRequest(Request request){
        EggTaskCentral.getInstance().addVolleyRequestByObject(request, null);
    }

    public void addVolleyRequestInActivity(Request request){
        EggTaskCentral.getInstance().addVolleyRequestByObject(request, EggBaseActivity.this);
    }

    public void cancelVolleyRequestInActivity(){
        EggTaskCentral.getInstance().cancelVolleyRquestByObject(EggBaseActivity.this);
    }




    public void startActivity(Class clazz){
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    public void startActivity(Class clazz, int requestCode){
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }
    public void startActivity(Class clazz, Bundle data, int requestCode){
        Intent intent = new Intent(this, clazz);
        if(data!=null) intent.putExtras(data);
        startActivityForResult(intent, requestCode);
    }




    public void refreshActionBarBackground(){
        refreshActionBarBackground(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void refreshActionBarBackground(FragmentActivity activity){
        if(activity == null) return;
        Resources.Theme theme = activity.getTheme();
        ActionBar actionBar = activity.getActionBar();
        TypedValue actionBarStyle = new TypedValue();
        theme.resolveAttribute(android.R.attr.actionBarStyle, actionBarStyle, true);
        TypedArray actionBarStyleAttributes = theme.obtainStyledAttributes(actionBarStyle.resourceId, new int[]{android.R.attr.background});
        Drawable actionBarBackground = actionBarStyleAttributes.getDrawable(0);
        actionBar.setBackgroundDrawable(actionBarBackground);
        actionBarStyleAttributes.recycle();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelVolleyRequestInActivity();
    }


    protected void updateRefreshRequest() {
        if( mRefreshRequest.size() > 0 ){
            startRefresh();
        }else{
            finishedRefresh();
        }
    }

    protected Object newStartRefreshRequest() {
        Object tag = new Object();
        mRefreshRequest.add(tag);
        updateRefreshRequest();
        return tag;
    }
    protected void startRefreshRequest(Object request) {
        mRefreshRequest.add(request);
        updateRefreshRequest();
    }
    protected void finishRefreshRequest(Object request) {
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
        setUpRefreshBar(mPullToRefreshLayout, new Runnable() {
            @Override
            public void run() {
                onPullToRefresh();
            }
        });
    }
    protected void onPullToRefresh(){

    }

    public void setUpRefreshBar(PullToRefreshLayout layout, final Runnable refreshListener){

        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        if (refreshListener != null) {
                            refreshListener.run();
                        }
                    }
                })
                .options(
                        new Options.Builder()
                                .scrollDistance(0.3f)
                                .build()
                )
                .setup(layout);

    }




}
