package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * Created by chikara on 2014/07/21.
 */
public class CheckableImageView extends ImageView implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private boolean mChecked = false;
    private boolean mIsAutoToggle = true;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckableImageView(Context context) {
        super(context);
    }


    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public void setAutoToggle(boolean enable) {
        mIsAutoToggle = enable;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == mChecked) return;
        mChecked = checked;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }
        refreshDrawableState();
        invalidate();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {
        /*
         * XXX: These are tiny, need some surrounding 'expanded touch area',
         * which will need to be implemented in Button if we only override
         * performClick()
         */

        /* When clicked, toggle the state */
        if (mIsAutoToggle) {
            toggle();
        }
        return super.performClick();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + CHECKED_STATE_SET.length);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param view      The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(CheckableImageView view, boolean isChecked);
    }


}
