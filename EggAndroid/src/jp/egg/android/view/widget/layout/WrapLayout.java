package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.egg.android.R;

/**
 * Created by chikara on 2014/08/30.
 */
public class WrapLayout extends ViewGroup {

    private static final String TAG = "WrapLayout";

    private static final int DEFAULT_SPACE_WIDTH_DP = 8;
    private static final int DEFAULT_SPACE_HEIGHT_DP = 4;

    private int mSpaceWidth = -1;
    private int mSpaceHeight = -1;

    private int mGravity = Gravity.LEFT;

    public WrapLayout(Context context) {
        this(context, null);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        Resources res = getResources();
        float density = res.getDisplayMetrics().density;
        if (mSpaceWidth < 0) {
            mSpaceWidth = (int) (DEFAULT_SPACE_WIDTH_DP * density);
        }
        if (mSpaceHeight < 0) {
            mSpaceHeight = (int) (DEFAULT_SPACE_HEIGHT_DP * density);
        }

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.WrapLayout, defStyleAttr, defStyleRes);

        int index;

        index = a.getInt(R.styleable.WrapLayout_android_gravity, -1);
        if (index >= 0) {
            setHorizontalGravity(index);
        }
    }

    public void setHorizontalGravity(int horizontalGravity) {
        final int gravity = horizontalGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) != gravity) {
            mGravity = (mGravity & ~Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) | gravity;
            requestLayout();
        }
    }


    public void setSpaceWidth(int width) {
        if (mSpaceWidth != width) {
            mSpaceWidth = width;
            requestLayout();
        }
    }

    public void setSpaceHeight(int height) {
        if (mSpaceHeight != height) {
            mSpaceHeight = height;
            requestLayout();
        }
    }

    public Pair<View, TextView> addTextChild(int layoutId, CharSequence text) {
        return addTextChild(layoutId, android.R.id.text1, text, getChildCount());
    }

    public Pair<View, TextView> addTextChild(int layoutId, int textViewId, CharSequence text, int index) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(layoutId, this, false);
        TextView textView = null;
        if (view instanceof ViewGroup) {
            textView = (TextView) ((ViewGroup) view).findViewById(textViewId);
        } else if (view instanceof TextView) {
            textView = (TextView) view;
        }
        if (textView != null) {
            textView.setText(text);
        }
        addView(view, index);
        return Pair.create(view, textView);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        boolean isGravityRight = (mGravity & Gravity.RIGHT) == Gravity.RIGHT;
        boolean isGravityLeft = !isGravityRight;

        int childCount = this.getChildCount();
        int layoutWidth = r - l - getPaddingLeft() - getPaddingRight();
        int lineTop = getPaddingTop();
        int currentTotal = 0;
        int lineMaxHeight = 0;
        List<View> lineChild = new ArrayList<>();

        if (layoutWidth < 0) layoutWidth = 0;

        final int spaceWidth = mSpaceWidth;
        final int spaceHeight = mSpaceHeight;

        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = this.getChildAt(i);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (i != 0 && layoutWidth > currentTotal + width + spaceWidth) {
                    if (isGravityLeft) {
                        child.layout(
                                currentTotal + spaceWidth + getPaddingLeft(),
                                lineTop,
                                currentTotal + width + spaceWidth + getPaddingLeft(),
                                lineTop + height);
                    } else {
                        child.layout(
                                layoutWidth - (currentTotal + width + spaceWidth + getPaddingRight()),
                                lineTop,
                                layoutWidth - (currentTotal + 0 + spaceWidth + getPaddingRight()),
                                lineTop + height);
                    }
                    currentTotal += width + spaceWidth;
                    lineMaxHeight = Math.max(lineMaxHeight, height);
                    lineChild.add(child);
                } else {
                    lineTop += lineMaxHeight + (i == 0 ? 0 : spaceHeight);
                    if (isGravityLeft) {
                        child.layout(
                                getPaddingLeft(),
                                lineTop,
                                width + getPaddingLeft(),
                                lineTop + height);
                    } else {
                        child.layout(
                                layoutWidth - (width + getPaddingRight()),
                                lineTop,
                                layoutWidth - (0 + getPaddingRight()),
                                lineTop + height);
                    }
                    currentTotal = width;
                    lineMaxHeight = height;
                    lineChild.clear();
                    lineChild.add(child);
                }
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int childCount = this.getChildCount();
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int lineTop = 0;
        int lineMaxHeight = 0;
        int currentTotal = 0;
        List<View> lineChild = new ArrayList<View>();

        if (layoutWidth < 0) layoutWidth = 0;
        if (layoutHeight < 0) layoutHeight = 0;

        final int spaceWidth = mSpaceWidth;
        final int spaceHeight = mSpaceHeight;

        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = this.getChildAt(i);
                child.measure(
                        View.MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.UNSPECIFIED)
                );
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (i != 0 && layoutWidth > currentTotal + width + spaceWidth) {
                    currentTotal += width + spaceWidth;
                    lineMaxHeight = Math.max(lineMaxHeight, height);
                    lineChild.add(child);
                } else {
                    lineTop += lineMaxHeight + (i == 0 ? 0 : spaceHeight);
                    currentTotal = width;
                    lineMaxHeight = height;
                    lineChild.clear();
                    lineChild.add(child);
                }
            }
        }

        setMeasuredDimension(
                layoutWidth + getPaddingLeft() + getPaddingRight(),
                lineTop + lineMaxHeight + getPaddingTop() + getPaddingBottom()
        );

    }
}