package jp.egg.android.app.fragment;

import jp.egg.android.R;
import jp.egg.android.ui.activity.DatabaseViewerActivity;
import jp.egg.android.ui.fragment.EggBaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TestMainFragment extends EggBaseFragment{

	public static final TestMainFragment newInstance(){
		TestMainFragment f = new TestMainFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}



	public TestMainFragment() {
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
        View v = inflater.inflate(R.layout.fragment_test_main, null, false);

        Button btn1 = (Button) v.findViewById(R.id.btn1);

        btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), DatabaseViewerActivity.class);
				getActivity().startActivityFromFragment(TestMainFragment.this, intent, 0);
			}
		});


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
