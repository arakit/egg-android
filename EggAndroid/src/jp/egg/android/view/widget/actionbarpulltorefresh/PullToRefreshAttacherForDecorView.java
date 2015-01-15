package jp.egg.android.view.widget.actionbarpulltorefresh;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by chikara on 2015/01/15.
 */
public class PullToRefreshAttacherForDecorView extends PullToRefreshAttacher{


    private Activity mActivity;


    protected PullToRefreshAttacherForDecorView(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    protected void initialize(Options options) {
        super.initialize(options);
    }

    @Override
    protected ViewGroup getActionBarContainer () {
        return (ViewGroup) mActivity.getWindow().getDecorView();
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

        Rect rect = getContainerVisibleRect();

        // Create LayoutParams for adding the View as a panel
        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(width, height,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        wlp.x = 0;
        wlp.y = rect.top;
        wlp.gravity = Gravity.TOP;

        // Workaround for Issue #182
        headerView.setTag(wlp);
        mActivity.getWindowManager().addView(headerView, wlp);
    }

    @Override
    protected void handleUpdateHeaderViewPosition(View headerView) {

        WindowManager.LayoutParams wlp = null;
        if (headerView.getLayoutParams() instanceof WindowManager.LayoutParams) {
            wlp = (WindowManager.LayoutParams) headerView.getLayoutParams();
        } else if (headerView.getTag() instanceof  WindowManager.LayoutParams) {
            wlp = (WindowManager.LayoutParams) headerView.getTag();
        }

        Rect rect = getContainerVisibleRect();
        if (wlp != null && wlp.y != rect.top) {
            wlp.y = rect.top;
            mActivity.getWindowManager().updateViewLayout(headerView, wlp);
        }
    }

    @Override
    protected void handleRemoveHeaderViewFromActivity(View headerView) {
        if (headerView.getWindowToken() != null) {
            mActivity.getWindowManager().removeViewImmediate(headerView);
        }
    }




}
