package jp.egg.android.view.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by chikara on 2014/09/11.
 */
public class HorizontalSquareLayout extends FrameLayout{

    public HorizontalSquareLayout(Context context) {
        super(context);
    }

    public HorizontalSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
