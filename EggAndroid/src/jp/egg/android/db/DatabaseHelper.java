package jp.egg.android.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{


	public DatabaseHelper(Configuration conf) {
		super(conf.getContext(), conf.getDatabaseName(), null, conf.getDatabaseVersion());
	}




	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}



}
