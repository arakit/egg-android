package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import jp.egg.android.R;

/**
 * Created by chikara on 2014/09/14.
 */
public class ActionBadgeMenuItemWhiteView extends ActionBadgeMenuItemView {


    public ActionBadgeMenuItemWhiteView(Context context) {
        super(context);
        init();
    }

    public ActionBadgeMenuItemWhiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionBadgeMenuItemWhiteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setColor(R.drawable.bg_ab_menu_action_badge_white, Color.DKGRAY);
    }


}
