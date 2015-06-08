/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.egg.android.R;

/**
 *
 */
public class DatabaseDummyFragment extends EggBaseFragment {

    private View mView;


    public DatabaseDummyFragment() {
    }

    public static final DatabaseDummyFragment newInstance() {
        DatabaseDummyFragment f = new DatabaseDummyFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_dummy, null, false);
        ViewHolder holder = new ViewHolder();


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

    private class ViewHolder {

    }


}
