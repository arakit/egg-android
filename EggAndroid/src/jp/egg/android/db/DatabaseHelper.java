package jp.egg.android.db;

import jp.egg.android.db.util.SQLiteUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper{


	public DatabaseHelper(Configuration conf) {
		super(conf.getContext(), conf.getDatabaseName(), null, conf.getDatabaseVersion());
	}




	@Override
	public void onCreate(SQLiteDatabase db) {
		executeCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}


	private void executeCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			for (TableInfo tableInfo : Cache.getTableInfos()) {
				db.execSQL(SQLiteUtils.createTableDefinition(tableInfo));
			}
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}


}
