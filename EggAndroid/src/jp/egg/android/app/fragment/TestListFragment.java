package jp.egg.android.app.fragment;

import java.util.ArrayList;
import java.util.List;

import jp.egg.android.R;
import jp.egg.android.app.EggApplication;
import jp.egg.android.app.model.entities.TestResult;
import jp.egg.android.db.EggDB;
import jp.egg.android.request.EggRequestUtil;
import jp.egg.android.request.in.EggDefaultParamsRequestBody;
import jp.egg.android.request.out.EggDefaultJsonNodeResponseBody;
import jp.egg.android.task.EggTask;
import jp.egg.android.task.EggTaskCentral;
import jp.egg.android.task.EggTaskError;
import jp.egg.android.ui.adapter.EggDefaultListAdapter;
import jp.egg.android.ui.fragment.EggBaseFragment;
import jp.egg.android.util.DUtil;
import jp.egg.android.util.HandlerUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private LayoutInflater mLayoutInflater;

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
//				EggSimpleJsonNodeRequest r = EggSimpleJsonNodeRequest.newInstance(
//						Method.GET, mRequestUrl, null);
//				r.setOnListener(new EggTaskListener<JsonNode, EggRequestError>() {
//					@Override
//					public void onSucess(JsonNode response) {
//						DUtil.d("test", "TestListFragment success = "+response);
//					}
//					@Override
//					public void onError(EggTaskError error) {
//						DUtil.d("test", "TestListFragment error = "+error);
//					}
//				});
				TestTask tt = new TestTask();
				EggTaskCentral.getInstance().addTask(tt);

			}
		});

		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

        return v;
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (EggApplication) getActivity().getApplication();

		mLayoutInflater = getLayoutInflater(savedInstanceState);

		mRequestUrl = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&safe=off&q=hatsune";

	}


	@Override
	public void onStart() {
		super.onStart();
	}


	private class TestData{
		List<TestResult> results;
	}

//	@JsonIgnoreProperties(ignoreUnknown=true)
//	public static class Result{
//		public String GsearchResultClass;
//		public String content;
//		public String contentNoFormatting;
//		public Integer height;
//		public String imageId;
//		public String originalContextUrl;
//		public Integer tbHeight;
//		public String tbUrl;
//		public Integer tbWidth;
//		public String title;
//		public String titleNoFormatting;
//		public String unescapedUrl;
//		public String url;
//		public String visibleUrl;
//		public Integer width;
//	}



	private static class Holder{

		ImageView icon1;
		TextView text1;
		TextView text2;

	}

	private class ListViewAdapter extends EggDefaultListAdapter<TestResult>{


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder;
			if(convertView == null){
				holder = new Holder();
				convertView = mLayoutInflater.inflate(R.layout.item_test_list, null);
				convertView.setTag(holder);

				holder.text1 = (TextView) convertView.findViewById(R.id.text_1);
				holder.text2 = (TextView)  convertView.findViewById(R.id.text_2);
				holder.icon1 = (ImageView)  convertView.findViewById(R.id.icon_1);

			}else{
				holder = (Holder) convertView.getTag();
			}

			TestResult item = getItem(position);

			holder.text1.setText(item.url);
			holder.text2.setText(""+item.height);

			EggRequestUtil.displayImage( holder.icon1, item.url, R.drawable.ic_launcher);

			return convertView;
		}




	}

	private final ListViewAdapter mAdapter = new ListViewAdapter();


	private class TestTask extends EggTask<TestData, EggTaskError>{


		@Override
		protected void onDoInBackground() {
			super.onDoInBackground();

			try{


				TestData ret = new TestData();
				JsonNode jn = EggRequestUtil.get(
						new EggDefaultParamsRequestBody(Method.GET, mRequestUrl, null) ,
						new EggDefaultJsonNodeResponseBody()
						);

				DUtil.d("test", "onDoinBackgroung : "+jn);

				ObjectMapper om = new ObjectMapper();
				JsonNode results = jn.get("responseData"). get("results");
				ret.results = new ArrayList<TestResult>();
				for(int i=0;i<results.size();i++){
					DUtil.d("test", "i = "+results.get(i));
					TestResult r = om.treeToValue(results.get(i) , TestResult.class);
					ret.results.add(r);
				}

				EggDB.beginTransaction();
				try{
					for(TestResult e : ret.results){
						e.save();
					}

					EggDB.setTransactionSuccessful();
				}finally{
					EggDB.endTransaction();
				}


				setSucces(ret);

			}catch(Exception ex){
				ex.printStackTrace();
				setError(null);
			}

		}

		@Override
		protected void onSuccess(final TestData result) {
			//super.onSuccess(result);

			HandlerUtil.post(new Runnable() {
				@Override
				public void run() {
					DUtil.d("test", "onSuccess run "+result.results);

					//mAdapter.clearItem();
					mAdapter.addItems(mAdapter.getCount(), result.results);
				}
			});


		}


	}



}
