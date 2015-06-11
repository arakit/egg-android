package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.co.chrisjenx.paralloid.OnScrollChangedListener;
import uk.co.chrisjenx.paralloid.ParallaxViewController;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.Transformer;

/**
 * Created by chikara on 2014/09/13.
 */
public class ParallaxListViewEx extends ListView implements Parallaxor {


//    public interface OnScrollChangeListener {
//        public void onScrollChanged(int position, int t);
//    }


    ParallaxViewController mParallaxViewController;
    private OnScrollListener mOnScrollListener;
    private Set<OnScrollListener> mOnScrollListeners = new LinkedHashSet<OnScrollListener>();
    private final OnScrollListener mOnWrappedScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
            for (OnScrollListener listener : mOnScrollListeners) {
                listener.onScrollStateChanged(view, scrollState);
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            for (OnScrollListener listener : mOnScrollListeners) {
                listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };

    public ParallaxListViewEx(Context context) {
        super(context);
        init();
    }

    public ParallaxListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallaxListViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mParallaxViewController = ParallaxViewController.wrap(this);
        super.setOnScrollListener(mOnWrappedScrollListener);
    }


    @Override
    public void parallaxViewBy(View view, float multiplier) {
        mParallaxViewController.parallaxViewBy(view, multiplier);
    }

    @Override
    public void parallaxViewBy(View view, Transformer transformer, float multiplier) {
        mParallaxViewController.parallaxViewBy(view, transformer, multiplier);
    }

    @Override
    public void parallaxViewBackgroundBy(View view, Drawable drawable, float multiplier) {
        mParallaxViewController.parallaxViewBackgroundBy(view, drawable, multiplier);
    }


    //    @Override
//    public void setOnScrollListener(OnScrollChangedListener listener){
//        mParallaxViewController.setOnScrollListener(listener);
//    }
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    public void addOnScrollLister(OnScrollListener listener) {
        mOnScrollListeners.add(listener);
    }

    public void removeOnScrollLister(OnScrollListener listener) {
        mOnScrollListeners.remove(listener);
    }

    public void setOnScrollChangeListener(OnScrollChangedListener listener) {
        mParallaxViewController.setOnScrollListener(listener);
    }


//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//
//        if(mOnScrollChangeListener!=null){
//            View c = getChildAt(0);
//            if (c == null) {
//                return;
//            }
//            //assuming all list items have same height
//            //int scrolly = -c.getTop() + getPaddingTop() + getFirstVisiblePosition() * c.getHeight();
//            int scrolly = -c.getTop() + getPaddingTop();
//            mOnScrollChangeListener.onScrollChanged(getFirstVisiblePosition(), scrolly);
//        }
//    }


}
