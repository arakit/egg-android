package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import jp.egg.android.R;

/**
 * Created by chikara on 2014/09/11.
 */
public class HorizontalSquareLayout extends FrameLayout {

    private float mVerticalWeight;

    public HorizontalSquareLayout(Context context) {
        super(context);
        init(context, null);
    }

    public HorizontalSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HorizontalSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
        mVerticalWeight = a.getFloat(R.styleable.SquareLayout_slWeight, 1.0f);
    }


    public void setVerticalWeight (float weight) {
        if (mVerticalWeight==weight) {
            return;
        }
        mVerticalWeight = weight;
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidthSize = MeasureSpec.getSize(widthMeasureSpec);


        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec((int) (measureWidthSize * mVerticalWeight), measureWidthMode)
        );
    }

}
