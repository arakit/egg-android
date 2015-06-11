/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import jp.egg.android.R;

/**
 *
 */
public class DatabaseViewerFragment extends EggBaseFragment {

    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private View mView;
    private Fragment[] mFragments;

    public DatabaseViewerFragment() {
    }

    public static final DatabaseViewerFragment newInstance() {
        DatabaseViewerFragment f = new DatabaseViewerFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_viewer, null, false);
        ViewHolder holder = new ViewHolder();

        //holder.textview = (TextView) v.findViewById(R.id.fragment_databaseviewr_text);

        // Set up the ViewPager with the sections adapter.
        holder.pager = (ViewPager) v.findViewById(R.id.pager);
        holder.pager.setAdapter(mSectionsPagerAdapter);

        v.setTag(holder);
        mView = v;
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFragments = new Fragment[]{
                DatabaseDummyFragment.newInstance(),
                DatabaseListFragment.newInstance(),
                DatabaseStructureFragment.newInstance(),
        };

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private class ViewHolder {
        //TextView textview;
        ViewPager pager;

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            return mFragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                default:
                    return "aaa";
            }
        }
    }


}
