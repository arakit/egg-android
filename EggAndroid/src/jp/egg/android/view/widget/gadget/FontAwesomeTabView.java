package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import jp.egg.android.R;
import jp.egg.android.manager.FontAwesome;

/**
 * Created by chikara on 2014/10/10.
 */
public class FontAwesomeTabView extends FrameLayout{

    private FontAwesomeTextView mIconView;
    private TextView mLabelView;


    public FontAwesomeTabView(Context context) {
        this(context, null);
    }

    public FontAwesomeTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontAwesomeTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp(context, attrs, defStyle);
    }


    private void setUp (Context context, AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FontAwesomeTabView);
        int layoutResId = a.getResourceId(R.styleable.FontAwesomeTabView_faTabLayout, R.layout.layout_font_awesome_tab);
        String iconName = a.getString(R.styleable.FontAwesomeTabView_faTabIconName);
//        float iconTextSize = a.getDimension(R.styleable.FontAwesomeTabView_faTabIconTextSize, 12.0f);
        String label = a.getString(R.styleable.FontAwesomeTabView_faTabLabel);
//        float textSize = a.getDimension(R.styleable.FontAwesomeTabView_faTabLabelTextSize, 12.0f);
//        ColorStateList textColor = a.getColorStateList(R.styleable.FontAwesomeTabView_faTabTextColor);

        a.recycle();


        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutResId, this, true);

        mIconView = (FontAwesomeTextView) findViewById(R.id.icon_font);
        mLabelView = (TextView) findViewById(R.id.label);


        String iconText = null;
        if ( iconName!=null ) {
            iconText = FontAwesome.getFaMap().get(iconName);
        }

        if ( iconText!=null ) {
            mIconView.setText(iconText);
        }
        if ( label!=null ) {
            mLabelView.setText(label);
        }

    }





}
