package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import jp.egg.android.R;
import jp.egg.android.util.ToastUtil;

/**
 * Created by chikara on 2014/09/13.
 */
public class ActionBadgeMenuItemView extends FrameLayout{

    //private View mContentView;

    private CharSequence mTitle;

    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    public ActionBadgeMenuItemView(Context context) {
        super(context);
        init(context);
    }

    public ActionBadgeMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionBadgeMenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }



    private void init(Context context){
        View child = LayoutInflater.from(context).inflate(R.layout.ab_action_menu_layout, this, true);

        getIconView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener!=null){
                    mOnClickListener.onClick(ActionBadgeMenuItemView.this);
                }
            }
        });
        getIconView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean ret = false;
                if(mOnLongClickListener!=null){
                    ret = mOnLongClickListener.onLongClick(ActionBadgeMenuItemView.this);
                }
                if( ret ){
                    return true;
                }
                CharSequence title = mTitle;
                if(!TextUtils.isEmpty(title)) {
                    CharSequence badgeText = getBadgeText();
                    if( TextUtils.isEmpty(badgeText) ) {
                        ToastUtil.popupToast(v, title);
                    }else{
                        ToastUtil.popupToast(v, title + " " + badgeText + "");
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public void setColor(int badgeBackgroundResource, int textColor){
        TextView view = getBadgeTextView();
        view.setBackgroundResource(badgeBackgroundResource);
        view.setTextColor(textColor);
    }

    public CharSequence getBadgeText(){
        return getBadgeTextView().getText();
    }

    public void setBadgeText(CharSequence text){
        TextView badgeView = getBadgeTextView();
        if( text != null ) {
            badgeView.setText(text);
            badgeView.setVisibility(View.VISIBLE);
        }else{
            badgeView.setText("");
            badgeView.setVisibility(View.INVISIBLE);
        }
    }

    public void setImageResource(int resId){
        ImageView iconView = getIconView();
        iconView.setImageResource(resId);
    }
    public void setImageDrawable(Drawable drawable){
        ImageView iconView = getIconView();
        iconView.setImageDrawable(drawable);
    }

    protected TextView getBadgeTextView(){
        TextView badgeView = (TextView) findViewById(R.id.badge);
        return badgeView;
    }
    protected ImageView getIconView(){
        ImageView iconView = (ImageView) findViewById(android.R.id.icon);
        return iconView;
    }

    public void setBadgeForNumber(int num){
        if( num >= 1000 ) {
            setBadgeText("999+");
        }else if( num > 0 ) {
            setBadgeText(Integer.toString(num));
        }else{
            setBadgeText(null);
        }
    }

    public void setTitle(CharSequence title){
        mTitle = title;
    }

    public void setUpActionView(MenuItem item){
        setImageDrawable(item.getIcon());
        setBadgeText(null);
        setTitle(item.getTitle());
        //item.setCheckable(true);
    }

    public void setUpActionView(MenuItem item, final OnClickListener listener){
        setUpActionView(item);
        setOnMenuClickListener(listener);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(listener!=null) listener.onClick(ActionBadgeMenuItemView.this);
                return true;
            }
        });
    }

    public void setOnMenuClickListener(OnClickListener listener){
        mOnClickListener = listener;
    }
    public void setOnMenuLongClickListener(OnLongClickListener listener){
        mOnLongClickListener = listener;
    }



    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
