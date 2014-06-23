package jp.egg.android.db;

import jp.egg.android.db.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class EggDB {

	public static final void initialize(Context context){
		Configuration.Builder builder = new Configuration.Builder(context.getApplicationContext());
		builder.setCacheSize(1024*1024*4);
//		builder.setDatabaseName(getString(R.string.AA_DB_NAME));
//		builder.setDatabaseVersion( getResources().getInteger(R.integer.AA_DB_VERSION) );
//		builder.setDatabaseName(databaseName);
		builder.setDatabaseName("app.db");
		builder.setDatabaseVersion(1);
		initialize(builder.create(), true);
	}


	public static void initialize(Configuration configuration, boolean loggingEnabled) {
		// Set logging enabled first
		setLoggingEnabled(loggingEnabled);
		Cache.initialize(configuration);
	}

	public static void setLoggingEnabled(boolean enabled) {
		Log.setEnabled(enabled);
	}



	public static void dispose() {
		Cache.dispose();
	}



	public static SQLiteDatabase getDatabase() {
		return Cache.openDatabase();
	}

	public static void beginTransaction() {
		Cache.openDatabase().beginTransaction();
	}

	public static void endTransaction() {
		Cache.openDatabase().endTransaction();
	}

	public static void setTransactionSuccessful() {
		Cache.openDatabase().setTransactionSuccessful();
	}

	public static boolean inTransaction() {
		return Cache.openDatabase().inTransaction();
	}

	public static void execSQL(String sql) {
		Cache.openDatabase().execSQL(sql);
	}

	public static void execSQL(String sql, Object[] bindArgs) {
		Cache.openDatabase().execSQL(sql, bindArgs);
	}


}
