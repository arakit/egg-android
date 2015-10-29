package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import jp.egg.android.R;

/**
 * Created by chikara on 2014/09/11.
 */
public class FitSquareLayout extends FrameLayout {

    public FitSquareLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FitSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FitSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        // nothing to do
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int min = Math.min(measureWidth, measureHeight);
        measureWidth = min;
        measureHeight = min;

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(measureWidth, measureWidthMode),
                MeasureSpec.makeMeasureSpec(measureHeight, measureHeightMode)
        );

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
//        setMeasuredDimension(size, size);

    }

}
