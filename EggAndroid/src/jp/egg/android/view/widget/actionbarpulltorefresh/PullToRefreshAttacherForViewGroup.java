package jp.egg.android.view.widget.actionbarpulltorefresh;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import jp.egg.android.util.Log;

/**
 * Created by chikara on 2015/01/15.
 */
public class PullToRefreshAttacherForViewGroup extends PullToRefreshAttacher{

    public final String TAG = "PullToRefreshAttacherForToolBar";



    private Activity mActivity;
    private ViewGroup mBarContainerView;

    protected PullToRefreshAttacherForViewGroup(Activity activity, ViewGroup barContainer) {
        super(activity);
        if (barContainer == null) {
            throw new IllegalArgumentException("toolBar cannot be null.");
        }
        mActivity = activity;
        mBarContainerView = barContainer;
    }

    @Override
    protected void initialize(Options options) {
        super.initialize(options);
    }

    @Override
    public ViewGroup getActionBarContainer () {
        return mBarContainerView;
    }

    @Override
    protected void handleAddHeaderViewToActivity(View headerView) {

        // Honour the requested layout params
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams requestedLp = headerView.getLayoutParams();
        if (requestedLp != null) {
            width = requestedLp.width;
            height = requestedLp.height;
        }

//        // Create LayoutParams for adding the View as toolBar
//        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(width, height,
//                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
//                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                PixelFormat.TRANSLUCENT);

        ViewGroup.LayoutParams lp = requestedLp;

        if (lp == null) {
            // Create LayoutParams for adding the View as toolBar
            lp = new ViewGroup.LayoutParams(
                    width, height
            );
        }

//        tlp.leftMargin = 0;
//        tlp.topMargin = getRect().top;
//        tlp.gravity = Gravity.TOP;


        // Workaround for Issue #182
        headerView.setTag(lp);
        mBarContainerView.addView(headerView, lp);


        Log.d(TAG, "mToolBar.addView() "+mBarContainerView.getChildCount());

    }

    @Override
    protected void handleUpdateHeaderViewPosition(View headerView) {

//        Toolbar.LayoutParams tlp = null;
//        if (headerView.getLayoutParams() instanceof WindowManager.LayoutParams) {
//            tlp = (Toolbar.LayoutParams) headerView.getLayoutParams();
//        } else if (headerView.getTag() instanceof  WindowManager.LayoutParams) {
//            tlp = (Toolbar.LayoutParams) headerView.getTag();
//        }

//        Rect rect = getRect();
//        if (tlp != null && tlp.topMargin != rect.top) {
//            tlp.topMargin = rect.top;
//            mToolBar.updateViewLayout(headerView, tlp);
//        }

    }

    @Override
    protected void handleRemoveHeaderViewFromActivity(View headerView) {
        mBarContainerView.removeView(headerView);
        Log.d(TAG, "mToolBar.removeView() "+mBarContainerView.getChildCount());
    }




}
