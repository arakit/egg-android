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
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import jp.egg.android.R;
import jp.egg.android.manager.SystemBarTintManager;
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
public class EggBaseActivity extends ActionBarActivity {


//    public interface OnAutoHideActionBarListener{
//        public void onShowAutoHideActionBar();
//        public void onHideAutoHideActionBar();
//    }

    public interface OnCustomActionListener{
        public void onCustomAction(EggBaseActivity activity, Message message);
    }

    private class CustomHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            deliveryCustomAction(msg);
        }
    }

    protected Set<Object> mRefreshRequest = new HashSet<Object>();

    private PullToRefreshLayout mPullToRefreshLayout;

    private CustomHandler mCustomHandler = new CustomHandler();

    //private final Set<OnAutoHideActionBarListener> mAutoHideActionBarListeners = new HashSet<OnAutoHideActionBarListener>();
    private final Set<OnCustomActionListener> mCustomActionListeners = new HashSet<OnCustomActionListener>();


    private SystemBarTintManager mSystemBarTintManager;



    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupSystemBarTint();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupSystemBarTint();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupSystemBarTint();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
        setupSystemBarTint();
    }


    protected SystemBarTintManager setupSystemBarTint(){

        if (mSystemBarTintManager != null) {
            return mSystemBarTintManager;
        }

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        // tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        // tintManager.setNavigationBarTintEnabled(true);

        mSystemBarTintManager = tintManager;

        return mSystemBarTintManager;
    }

    protected SystemBarTintManager getSystemBarTintManager () {
        return mSystemBarTintManager;
    }

    public int getInsetTop (boolean withActionBar) {
        return mSystemBarTintManager.getConfig().getPixelInsetTop(withActionBar);
    }
    public int getInsetBottom () {
        return mSystemBarTintManager.getConfig().getPixelInsetBottom();
    }
    public int getInsetLeft () {
        return 0;
    }
    public int getInsetRight () {
        return mSystemBarTintManager.getConfig().getPixelInsetRight();
    }

    public void setInsetPadding (View view) {
        view.setPadding (
                getInsetLeft(),
                getInsetTop(true),
                getInsetRight(),
                getInsetBottom()
        );
    }


    public int getInsetTopWithTopMaterialActionBar (boolean withActionBar) {
        return mSystemBarTintManager.getConfig().getPixelInsetTop(false) +
                (withActionBar ? (int)getResources().getDimension(R.dimen.abc_action_bar_default_height_material) : 0);
    }

    public void setInsetPaddingWithTopMaterialActionBar (View view, boolean withActionBar) {
        view.setPadding (
                getInsetLeft(),
                getInsetTopWithTopMaterialActionBar(withActionBar),
                getInsetRight(),
                getInsetBottom()
        );
    }

//    public void addAutoHideActionBarListener(OnAutoHideActionBarListener listener){
//        mAutoHideActionBarListeners.add(listener);
//    }
//    public void removeAutoHideActionBarListener(OnAutoHideActionBarListener listener){
//        mAutoHideActionBarListeners.remove(listener);
//    }

    public void addCustomActionListener(OnCustomActionListener listener){
        mCustomActionListeners.add(listener);
    }
    public void removeCustomActionListener(OnCustomActionListener listener){
        mCustomActionListeners.remove(listener);
    }

//    protected void notifyShowAutoHideActionBr(boolean visible){
//        if(visible){
//            for( OnAutoHideActionBarListener listener : mAutoHideActionBarListeners ){
//                listener.onShowAutoHideActionBar();
//            }
//        }else{
//            for( OnAutoHideActionBarListener listener : mAutoHideActionBarListeners ){
//                listener.onHideAutoHideActionBar();
//            }
//        }
//    }

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

//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void showAutoHideActionBar(){
//        if(Build.VERSION.SDK_INT < 11) return;
//        if( isShowingAutoHideActionBar() ) return;
//
//        getSupportActionBar().show();
//        notifyShowAutoHideActionBr(true);
//    }
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void hideAutoHideActionBar(){
//        if(Build.VERSION.SDK_INT < 11) return;
//        if( !isShowingAutoHideActionBar() ) return;
//
//        getSupportActionBar().hide();
//        notifyShowAutoHideActionBr(false);
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public boolean isShowingAutoHideActionBar(){
//        if(Build.VERSION.SDK_INT < 11) return false;
//        return getSupportActionBar().isShowing();
//    }


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
    public static void refreshActionBarBackground(EggBaseActivity activity){
        if(activity == null) return;
        Resources.Theme theme = activity.getTheme();
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        TypedValue actionBarStyle = new TypedValue();
        theme.resolveAttribute(R.attr.actionBarStyle, actionBarStyle, true);
        TypedArray actionBarStyleAttributes = theme.obtainStyledAttributes(actionBarStyle.resourceId, new int[]{R.attr.background});
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

    protected boolean isPullToRefreshing () {
        if (mPullToRefreshLayout!=null) {
            return mPullToRefreshLayout.isRefreshing();
        }
        return false;
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
        setUpRefreshBar(layout, refreshListener,
                new Options.Builder()
                        .scrollDistance(0.3f)
                        .build()
                );
    }
    public void setUpRefreshBar(PullToRefreshLayout layout, final Runnable refreshListener, Options options){

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
                        options
                )
                .setup(layout);

    }




}
