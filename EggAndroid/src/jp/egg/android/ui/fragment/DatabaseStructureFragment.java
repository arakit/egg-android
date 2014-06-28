/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import jp.egg.android.R;
import jp.egg.android.db.EggDB;
import jp.egg.android.db.util.Log;
import jp.egg.android.ui.adapter.DatabaseStructureAdapter;
import jp.egg.android.util.StringUtil;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 *
 */
public class DatabaseStructureFragment extends EggBaseFragment {

	public static final DatabaseStructureFragment newInstance(){
		DatabaseStructureFragment f = new DatabaseStructureFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}


	private DatabaseStructureAdapter mAdapter;

	private View mView;

	private class ViewHolder{
		//TextView textview;
		ListView listview;
	}


    public DatabaseStructureFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_strucrures, null, false);
        ViewHolder holder = new ViewHolder();

        //holder.textview = (TextView) v.findViewById(R.id.fragment_databaseviewr_text);
        holder.listview = (ListView) v.findViewById(R.id.fragment_databaselist_structure);

        holder.listview.setAdapter(mAdapter);

        v.setTag(holder);
        mView = v;
        return v;
    }




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mAdapter = new DatabaseStructureAdapter(getActivity(), getLayoutInflater(savedInstanceState));

	}


	@Override
	public void onStart() {
		super.onStart();


		//executeList();
	}



	private void executeList(){

		Log.d("test", "executeList");

		ViewHolder holder = (ViewHolder) mView.getTag();

		List<DatabaseStructureAdapter.Item> items = new ArrayList<DatabaseStructureAdapter.Item>();

		String sql = "PRAGMA table_info('テーブル名');";
		Cursor c = EggDB.getDatabase().rawQuery(sql, null);
        Log.d("test", "table num = " + String.valueOf(c.getCount()));
		StringBuilder sb = new StringBuilder();
		if(c.moveToFirst()){
			String[] colmns = c.getColumnNames();
			int colmn_name = c.getColumnIndex("name");
			int colmn_tbl_name = c.getColumnIndex("tbl_name");
			 //sb.append("colmns = "+	StringUtil.makeDivideString(colmns, ", "));
			 //sb.append('\n');

			while(c.moveToNext()){
				String[] values = new String[colmns.length];
				for(int i=0;i<colmns.length;i++){
					values[i] = c.getString(i);
				}
				 sb.append("values = "+	StringUtil.makeDivideString(values, ", "));
				 sb.append('\n');

				DatabaseStructureAdapter.Item item = new DatabaseStructureAdapter.Item();
				item.name = c.getString(colmn_name) ;
				items.add(item);
			}

		}
		c.close();

		Log.d("data = "+sb.toString());

		mAdapter.addAll(items);
		mAdapter.notifyDataSetChanged();


	}





}
