package jp.egg.android.ui.adapter;

import jp.egg.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class DatabaseListAdapter extends ArrayAdapter<DatabaseListAdapter.Item>{

	public static class Item{
		public String name;
		public String type;
		public String tbl_name;
		public String rootpage;
		public String sql;
	}
	private static class Holder{
		TextView name;
		TextView summary;
	}


	public static final int LAYOUT_ID = R.layout.item_test_list;

	private LayoutInflater mLayoutInflater;

	public DatabaseListAdapter(Context context, LayoutInflater inflater) {
		super(context, 0);
		mLayoutInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = mLayoutInflater.inflate(LAYOUT_ID, null);

			holder.name = (TextView) convertView.findViewById(R.id.text_1);
			holder.summary = (TextView) convertView.findViewById(R.id.text_2);

			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}

		Item item = getItem(position);

		holder.name.setText( ""+item.name + " (" + item.tbl_name +")");
		holder.summary.setText( ""+item.sql);

		return convertView;
	}



}
