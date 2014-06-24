/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


	private View mView;

	private class ViewHolder{
		TextView textview;
	}


    public DatabaseStructureFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_strucrures, null, false);
        ViewHolder holder = new ViewHolder();

        //holder.textview = (TextView) v.findViewById(R.id.fragment_databaseviewr_text);


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


	}






}
