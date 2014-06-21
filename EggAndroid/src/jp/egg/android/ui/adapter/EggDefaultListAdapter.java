package jp.egg.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;


public class EggDefaultListAdapter<I> extends EggBaseListAdapter<I> {

//	private Class mHolderClazz;
//	public EggListAdapter(Class clazz) {
//		mHolderClazz = clazz;
//	}

	private final ArrayList<I> mList = new ArrayList<I>();



	public void addItem(int index, I item){
		mList.add(index, item);
		notifyDataSetChanged();
	}
	public void addItem(I item){
		mList.add(item);
		notifyDataSetChanged();
	}
	public void addItems(int index, List<I> items){
		mList.addAll(index, items);
		notifyDataSetChanged();
	}


	public void removeItem(I item){
		mList.remove(item);
		notifyDataSetChanged();
	}
	public void removeItemAt(int index){
		mList.remove(index);
		notifyDataSetChanged();
	}


	public void clearItem(){
		mList.clear();
		notifyDataSetChanged();
	}



	@Override
	public int getCount(){
		return mList.size();
	}

	@Override
	public I getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


}
