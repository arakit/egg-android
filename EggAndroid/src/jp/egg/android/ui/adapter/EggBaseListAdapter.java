package jp.egg.android.ui.adapter;

import android.widget.BaseAdapter;

public abstract class EggBaseListAdapter<I> extends BaseAdapter {

//	private Class mHolderClazz;
//	public EggListAdapter(Class clazz) {
//		mHolderClazz = clazz;
//	}

    @Override
    public abstract int getCount();

    @Override
    public abstract I getItem(int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

//	@SuppressWarnings("unchecked")
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		I item;
//		H holder;
//		if( convertView == null ){
//
//			try {
//				holder = (H) mHolderClazz.newInstance();
//			}catch (Exception e) {
//				holder = null;
//			}
//			item = getItem(position);
//
//			convertView = initView(position, convertView, parent, holder, item);
//			if(convertView != null) convertView.setTag(holder);
//
//		}else{
//			holder = (H) convertView.getTag();
//			item = getItem(position);
//		}
//
//		convertView = updateView(position, convertView, holder, item);
//		 if(convertView != null) convertView.setTag(holder);
//
//		return convertView;
//	}
//
////	protected boolean isInitView(int position, View convertView, ViewGroup parent){
////		return convertView == null ;
////	}
//
//	protected abstract View initView(int position, View convertView, ViewGroup parent);
//
//	protected abstract View updateView(int position, View view, H holder);


}
