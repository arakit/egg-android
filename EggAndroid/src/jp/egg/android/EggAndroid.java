package jp.egg.android;

import jp.egg.android.db.EggDB;
import jp.egg.android.task.EggTaskCentral;
import android.content.Context;

public class EggAndroid {



	public static final void initialize(Context context){
		Context appc = context.getApplicationContext();
		EggTaskCentral.initialize(appc);
		EggDB.initialize(appc);
	}

	public static final void terminate(){
		EggDB.dispose();
		EggTaskCentral.destroy();
	}



}
