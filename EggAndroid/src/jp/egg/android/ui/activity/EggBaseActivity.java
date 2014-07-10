package jp.egg.android.ui.activity;

import android.support.v4.app.FragmentActivity;
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
}
