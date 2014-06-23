package jp.egg.android.app;

import jp.egg.android.db.EggDB;
import jp.egg.android.task.central.EggTaskCentral;
import android.app.Application;

public class EggApplication extends Application{



	@Override
	public void onCreate() {
		super.onCreate();

		EggTaskCentral.initialize(getApplicationContext());
		EggDB.initialize(getApplicationContext());


	}

	@Override
	public void onTerminate() {
		super.onTerminate();


		EggDB.dispose();
		EggTaskCentral.destroy();


	}




}
