package jp.egg.android.view.widget.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by chikara on 2014/07/24.
 *
 *   ListViewで上へのスワイプができないため、その変更をした、スワイプリフレッシュ。
 *   Progressの描画は、SearchSwipeRefreshDrawProgressViewに、横取りさせることができます。
 *
 *   How to use.
 *     子の中にいる対象のListViewをsetListViewでsetしてください。
 *
 */
public class NonDirectListViewSwipeRefreshLayout extends CustomSwipeRefreshLayout {

    public AbsListView mListView;


    public NonDirectListViewSwipeRefreshLayout(Context context) {
        super(context);
    }

    public NonDirectListViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    public void setListView(AbsListView listView){
        mListView = listView;
    }


    private boolean canPullOnListView(){
        return mListView.getFirstVisiblePosition() == 0 && mListView.getChildAt(0).getTop() >= 0;
    }

    @Override
    public boolean canChildScrollUp(){
        if(mListView!=null) {
            return !canPullOnListView();
        }else{
            return super.canChildScrollUp();
        }
    }



}
