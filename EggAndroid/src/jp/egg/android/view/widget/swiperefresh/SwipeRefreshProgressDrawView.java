package jp.egg.android.view.widget.swiperefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by chikara on 2014/07/25.
 * <p/>
 * SwipeRefreshLayoutのProgressの描画を外側でおこないためのView. つまり描画横取りView.
 * <p/>
 * How to use.
 * setSwipeRefreshLayout()で、対象のSwipeRefreshLayoutをsetしてください。
 * さすれば、Progressの描画を横取りできるであろう。
 */
public class SwipeRefreshProgressDrawView extends View {

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private CustomSwipeRefreshProgress mProgressBar;
    private int mProgressBarHeight;


    public SwipeRefreshProgressDrawView(Context context) {
        super(context);
        init();
    }

    public SwipeRefreshProgressDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeRefreshProgressDrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mProgressBarHeight = (int) (metrics.density * CustomSwipeRefreshLayout.PROGRESS_BAR_HEIGHT);
    }


    /**
     * SwipeRefreshLayoutのProgressの描画を横取りします。
     *
     * @param swipeRefreshLayout
     */
    public void setSwipeRefreshLayout(final CustomSwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout == null) {
            mSwipeRefreshLayout = null;
            mProgressBar = null;
            return;
        }
        mSwipeRefreshLayout = swipeRefreshLayout;
        mProgressBar = mSwipeRefreshLayout.getProgressBar();

        mSwipeRefreshLayout.setOnDrawCallListener(new CustomSwipeRefreshLayout.OnDrawCallListener() {
            @Override
            public void onDrawCall() {
                SwipeRefreshProgressDrawView.this.invalidate();
            }
        });
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mProgressBar != null) {
            //Logger.LOGD("swipe", "draw()");
            mProgressBar.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (mProgressBar != null) {
            //Logger.LOGD("swipe", ""+width+", "+mProgressBarHeight);
            mProgressBar.setBounds(0, 0, width, mProgressBarHeight); //CHANGED PAIRS
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(widthSize, mProgressBarHeight);
    }
}
