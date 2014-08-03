package jp.egg.android.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import com.android.volley.Request;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;

/**
 * Created by chikara on 2014/07/10.
 */
public class EggBaseActivity extends FragmentActivity{



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




    public void refreshActionBarBackground(){
        refreshActionBarBackground(this);
    }

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
