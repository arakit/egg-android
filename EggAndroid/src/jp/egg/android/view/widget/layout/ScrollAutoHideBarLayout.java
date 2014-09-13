package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.FrameLayout;

/**
 * Created by chikara on 2014/08/07.
 */
public class ScrollAutoHideBarLayout extends FrameLayout{


    public interface OnScrolledListener {
        public void onSwipeScrollingDown();
        public void onSwipeScrollingUp();


        public void onScrollStart(int px);
        public void onScrolling(int px);
        public void onScrollEnd(int px);
    }


    private VelocityTracker mVelocityTracker;
    private float mDensity;
    private float mDownedX;
    private float mDownedY;
    private boolean mIsFirstMove = false;

    private OnScrolledListener mOnScrolledListener;


    public ScrollAutoHideBarLayout(Context context) {
        super(context);
        init();
    }

    public ScrollAutoHideBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollAutoHideBarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        //setFillViewport(true);
        mDensity = getResources().getDisplayMetrics().density;
    }


    public void setOnScrolledListener(OnScrolledListener listener){
        mOnScrolledListener = listener;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("test33", "onScrollChanged ");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.d("test33", "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Log.d("test33", "onTouchEvent");
        return super.onTouchEvent(ev);
    }

//    @Override
//    public boolean arrowScroll(int direction) {
//        //Log.d("test33", "arrowScroll");
//        return super.arrowScroll(direction);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.d("test33", "dispatchTouchEvent");

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mDownedX = event.getX();
                mDownedY = event.getY();
                mIsFirstMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float vy = mVelocityTracker.getYVelocity();

                //ue

                if(mIsFirstMove){
                    mIsFirstMove = false;
                    onScrollStart((int)(event.getY() - mDownedY));
                }
                onScrolling((int)(event.getY() - mDownedY));

                if(vy > 75 * mDensity){
                    onScrollDown();
                }
                else if( vy < -75 * mDensity ){
                    onScrollUp();
                }
                //Log.d("test33", "X velocity is " + mVelocityTracker.getXVelocity() +" pixels per second");
                //Log.d("test33", "Y velocity is " + mVelocityTracker.getYVelocity() +" pixels per second");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if(!mIsFirstMove) {
                    onScrollEnd((int) (event.getY() - mDownedY));
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    protected void onScrollStart(int px){
        if( mOnScrolledListener!=null )
            mOnScrolledListener.onScrollStart(px);
    }
    protected void onScrollEnd(int px){
        if( mOnScrolledListener!=null )
            mOnScrolledListener.onScrollEnd(px);
    }


    protected void onScrolling(int px){
        if( mOnScrolledListener!=null )
            mOnScrolledListener.onScrolling(px);
    }

    protected void onScrollDown(){
        if( mOnScrolledListener!=null )
            mOnScrolledListener.onSwipeScrollingDown();
    }

    protected void onScrollUp(){
        if( mOnScrolledListener!=null )
            mOnScrolledListener.onSwipeScrollingUp();
    }


}

