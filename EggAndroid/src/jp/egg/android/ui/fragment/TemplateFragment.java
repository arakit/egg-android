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
 * テンプレート フラグメント。
 */
public class TemplateFragment extends EggBaseFragment {

    public TemplateFragment() {
    }

    public static final TemplateFragment newInstance() {
        TemplateFragment f = new TemplateFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container, false);

//        getFragmentManager().beginTransaction()
//                .replace(R.id.tab_content, f)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .addToBackStack(null)
//                .commit();

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
