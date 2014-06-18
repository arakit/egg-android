package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestListFragment extends EggBaseFragment{

	public static final TestListFragment newInstance(){
		TestListFragment f = new TestListFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}


	public TestListFragment() {
		// TODO 自動生成されたコンストラクター・スタブ
	}


    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_listview, null, false);

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
