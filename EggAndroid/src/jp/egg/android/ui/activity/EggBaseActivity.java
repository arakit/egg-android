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
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chikara on 2014/07/10.
 */
public class EggBaseActivity extends FragmentActivity{

    private final Set<OnAutoHideActionBarListener> mAutoHideActionBarListeners = new HashSet<OnAutoHideActionBarListener>();

    public interface OnAutoHideActionBarListener{
        public void onShowAutoHideActionBar();
        public void onHideAutoHideActionBar();
    }

    public void addAutoHideActionBarListener(OnAutoHideActionBarListener listener){
        mAutoHideActionBarListeners.add(listener);
    }
    public void removeAutoHideActionBarListener(OnAutoHideActionBarListener listener){
        mAutoHideActionBarListeners.remove(listener);
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
}
