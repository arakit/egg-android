/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import jp.egg.android.db.EggDB;
import jp.egg.android.util.StringUtil;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * database の一覧
 */
public class DatabaseListFragment extends EggBaseFragment {

	public static final DatabaseListFragment newInstance(){
		DatabaseListFragment f = new DatabaseListFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}


	private View mView;

	private class ViewHolder{
		ListView listView;
	}


    public DatabaseListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_list, null, false);
        ViewHolder holder = new ViewHolder();

        holder.listView= (ListView) v.findViewById(R.id.fragment_databaselist_list);


        v.setTag(holder);
        mView = v;
        return v;
    }




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}


	@Override
	public void onStart() {
		super.onStart();

		executeList();
	}




	private void executeList(){

		ViewHolder holder = (ViewHolder) mView.getTag();

		Cursor c = EggDB.getDatabase().rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
        Log.d("test", "table num = " + String.valueOf(c.getCount()));
		StringBuilder sb = new StringBuilder();
		if(c.moveToFirst()){
			String[] colmns = c.getColumnNames();
			 sb.append("colmns = "+	StringUtil.makeDivideString(colmns, ", "));
			 sb.append('\n');

			while(c.moveToNext()){
				String[] values = new String[colmns.length];
				for(int i=0;i<colmns.length;i++){
					values[i] = c.getString(i);
				}
				 sb.append("values = "+	StringUtil.makeDivideString(values, ", "));
				 sb.append('\n');
			}
		}
		c.close();

		//TODO
		//holder.listView.setText(sb.toString());

	}






}
