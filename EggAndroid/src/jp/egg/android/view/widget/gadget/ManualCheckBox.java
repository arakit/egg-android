package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by chikara on 2014/07/25.
 */
public class ManualCheckBox extends CheckBox{

    public ManualCheckBox(Context context) {
        super(context);
    }

    public ManualCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void toggle() {
        //自動で切り替えさせない。プログラム側で切り替え。
    }

    @Override
    public boolean performClick() {
        return super.performClick(); //onClickListenerを呼ばせる
    }
}
