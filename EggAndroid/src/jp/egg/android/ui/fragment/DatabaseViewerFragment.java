/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class DatabaseViewerFragment extends EggBaseFragment {

	public static final DatabaseViewerFragment newInstance(){
		DatabaseViewerFragment f = new DatabaseViewerFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}



    public DatabaseViewerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_viewer, null, false);



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
