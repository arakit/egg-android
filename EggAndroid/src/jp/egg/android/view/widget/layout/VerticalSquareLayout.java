package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import jp.egg.android.R;

/**
 * Created by chikara on 2015/08/24.
 */
public class VerticalSquareLayout extends FrameLayout {

    private float mHorizontalWeight;

    public VerticalSquareLayout(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
        mHorizontalWeight = a.getFloat(R.styleable.SquareLayout_slWeight, 1.0f);
        a.recycle();
    }

    public void setHorizontalWeight (float weight) {
        if (mHorizontalWeight == weight) {
            return;
        }
        mHorizontalWeight = weight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec((int) (measureHeightSize * mHorizontalWeight), measureHeightMode),
                heightMeasureSpec
        );
    }

}
