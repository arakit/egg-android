package jp.egg.android.ui.fragment;

import jp.egg.android.R;
import jp.egg.android.app.EggApplication;
import jp.egg.android.request.EggRequestError;
import jp.egg.android.request.EggSimpleJsonNodeRequest;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.task.EggTaskListener;
import jp.egg.android.task.central.EggTaskCentral;
import jp.egg.android.util.DUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.fasterxml.jackson.databind.JsonNode;

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
        Button btn1 = (Button) v.findViewById(R.id.button1);

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

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

			}
		});

		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EggSimpleJsonNodeRequest r = EggSimpleJsonNodeRequest.newInstance(
						Method.GET, mRequestUrl, null);
				r.setOnListener(new EggTaskListener<JsonNode, EggRequestError>() {
					@Override
					public void onSucess(JsonNode response) {
						DUtil.d("test", "success = "+response);
					}
					@Override
					public void onError(EggTaskError error) {
						DUtil.d("test", "error = "+error);
					}
				});
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
