package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import jp.egg.android.app.EggApplication;
import jp.egg.android.request.EggSimpleJacksonRequest;
import jp.egg.android.task.central.EggTaskCentral;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

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


	EggApplication mApp;

	private String mRequestUrl;


    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_listview, null, false);

        ListView listView = (ListView) v.findViewById(R.id.fragment_test_listview_listview);

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {

			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
					// 最後尾までスクロールしたので、何かデータ取得する処理

				}
			}
		});

		listView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EggSimpleJacksonRequest r = EggSimpleJacksonRequest.newGetRequest(mRequestUrl);
				EggTaskCentral.getInstance().addTask(r);
			}
		});

        return v;
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (EggApplication) getActivity().getApplication();

		mRequestUrl = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=sky";

	}


	@Override
	public void onStart() {
		super.onStart();
	}






	private class ListViewAdapter extends BaseAdapter{

		@Override
		public int getCount() {

			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}


	}



}
