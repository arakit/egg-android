package jp.egg.android.ui.activity;

import android.support.v4.app.FragmentActivity;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelVolleyRequestInActivity();
    }
}
