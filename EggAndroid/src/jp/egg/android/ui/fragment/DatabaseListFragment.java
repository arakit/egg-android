/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import jp.egg.android.R;
import jp.egg.android.ui.adapter.DatabaseListAdapter;
import jp.egg.android.util.Log;
import jp.egg.android.util.StringUtil;

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
    private DatabaseListAdapter mAdapter;


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
        holder.listView.setAdapter(mAdapter);

        v.setTag(holder);
        mView = v;
        return v;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new DatabaseListAdapter(getActivity(), getLayoutInflater(savedInstanceState));
    }


    @Override
    public void onStart() {
        super.onStart();

        executeList();
    }




    private void executeList(){

        Log.d("test", "executeList");

        ViewHolder holder = (ViewHolder) mView.getTag();

        List<DatabaseListAdapter.Item> items = new ArrayList<DatabaseListAdapter.Item>();

        Cursor c = ActiveAndroid.getDatabase().rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
        Log.d("test", "table num = " + String.valueOf(c.getCount()));
        StringBuilder sb = new StringBuilder();
        if(c.moveToFirst()){
            String[] colmns = c.getColumnNames();
            int colmn_name = c.getColumnIndex("name");
            int colmn_tbl_name = c.getColumnIndex("tbl_name");
            int colmn_type = c.getColumnIndex("type");
            int colmn_rootpage = c.getColumnIndex("rootpage");
            int colmn_sql = c.getColumnIndex("sql");
            //sb.append("colmns = "+	StringUtil.makeDivideString(colmns, ", "));
            //sb.append('\n');

            while(c.moveToNext()){
                String[] values = new String[colmns.length];
                for(int i=0;i<colmns.length;i++){
                    values[i] = c.getString(i);
                }
                sb.append("values = "+	StringUtil.makeDivideString(values, ", "));
                sb.append('\n');

                DatabaseListAdapter.Item item = new DatabaseListAdapter.Item();
                item.name = c.getString(colmn_name) ;
                item.tbl_name  = c.getString(colmn_tbl_name);
                item.type  = c.getString(colmn_type);
                item.rootpage  = c.getString(colmn_rootpage);
                item.sql  = c.getString(colmn_sql);
                items.add(item);
            }

        }
        c.close();

        //Log.d("data = " + sb.toString());

        mAdapter.addAll(items);
        mAdapter.notifyDataSetChanged();

        //TODO
        //holder.listView.setText(sb.toString());

    }






}
