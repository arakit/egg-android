package jp.egg.android.ui.activity;

import java.util.ArrayList;
import java.util.List;

import jp.egg.android.R;
import jp.egg.android.ui.fragment.NavigationDrawerFragment;
import jp.egg.android.ui.fragment.PlaceholderFragment;
import jp.egg.android.ui.fragment.TestListFragment;
import jp.egg.android.util.DUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks ,
		NavigationDrawerFragment.NavigationDrawerInterface {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;


	private  static class FragmentItem{
		String title;
		Fragment fragment;

		public FragmentItem(String title, Fragment fragment) {
			this.title = title;
			this.fragment = fragment;
		}
	}

	private List<FragmentItem> mFragments;

	private List<FragmentItem> getFragments(){
		if(mFragments==null){
			mFragments = new ArrayList<MainActivity.FragmentItem>();
			mFragments.add(new FragmentItem("item0", PlaceholderFragment.newInstance(0)));
			mFragments.add(new FragmentItem("testList", TestListFragment.newInstance()));
		}
		return mFragments;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DUtil.d("test", "onCreate "+savedInstanceState );

//		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
//				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment = NavigationDrawerFragment.newInstance();

		getFragments();

		mTitle = getTitle();

		if(savedInstanceState==null){

			FragmentManager fm = getSupportFragmentManager();
			fm
			.beginTransaction()
			.add(R.id.navigation_drawer_container, mNavigationDrawerFragment)
			.commit();

		}

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				(ViewGroup) findViewById( R.id.navigation_drawer_container) ,
				(DrawerLayout) findViewById(R.id.drawer_layout),
				mNavigationDrawerFragment.isAdded()
				);


	}




	@Override
	protected void onStart() {
		super.onStart();

	}




	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fm = getSupportFragmentManager();
//		fragmentManager
//				.beginTransaction()
//				.replace(R.id.container,
//						PlaceholderFragment.newInstance(position + 1)).commit();
		fm
		.beginTransaction()
		.replace(R.id.container, getFragments().get(position).fragment )
		.commit();

	}

	public void onSectionAttached(int number) {
//		switch (number) {
//		case 1:
//			mTitle = "";
//			break;
//		case 2:
//			mTitle = "";
//			break;
//		case 3:
//			mTitle = "";
//			break;
//		}
		mTitle = getFragments().get(number).title;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public String getNavigationDrawerItemTitle(int position) {
		return getFragments().get(position).title;
	}

	@Override
	public int getNavigationDrawerItemSize() {
		return getFragments().size();
	}




	@Override
	protected void onStop() {
		super.onStop();
		DUtil.d("test", "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DUtil.d("test", "onDestroy");
		mFragments = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		DUtil.d("test", "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		DUtil.d("test", "onResume");
	}




}
