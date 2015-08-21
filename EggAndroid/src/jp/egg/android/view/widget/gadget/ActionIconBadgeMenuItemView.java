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

import jp.egg.android.R;
import jp.egg.android.util.ToastUtil;

/**
 * Created by chikara on 2015/08/21.
 */
public class ActionIconBadgeMenuItemView extends FrameLayout {


    private CharSequence mTitle;

    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    public ActionIconBadgeMenuItemView(Context context) {
        super(context);
        init(context);
    }

    public ActionIconBadgeMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionIconBadgeMenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        View child = LayoutInflater.from(context).inflate(R.layout.ab_action_menu_icon_badge_layout, this, true);

        getIconView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(ActionIconBadgeMenuItemView.this);
                }
            }
        });
        getIconView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean ret = false;
                if (mOnLongClickListener != null) {
                    ret = mOnLongClickListener.onLongClick(ActionIconBadgeMenuItemView.this);
                }
                if (ret) {
                    return true;
                }
                CharSequence title = mTitle;
                if (!TextUtils.isEmpty(title)) {
                    ToastUtil.popupToast(v, title);
                    return true;
                }
                return false;
            }
        });

    }

    public void setBadgeIcon(int badgeIconResource) {
        ImageView badgeView = getBadgeImageView();
        badgeView.setImageResource(badgeIconResource);
        if (badgeIconResource != 0) {
            badgeView.setVisibility(View.VISIBLE);
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    public void setImageResource(int resId) {
        ImageView iconView = getIconView();
        iconView.setImageResource(resId);
    }

    public void setImageDrawable(Drawable drawable) {
        ImageView iconView = getIconView();
        iconView.setImageDrawable(drawable);
    }

    protected ImageView getBadgeImageView() {
        ImageView badgeView = (ImageView) findViewById(R.id.badge);
        return badgeView;
    }

    protected ImageView getIconView() {
        ImageView iconView = (ImageView) findViewById(android.R.id.icon);
        return iconView;
    }


    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public void setUpActionView(MenuItem item) {
        setImageDrawable(item.getIcon());
        setTitle(item.getTitle());
    }

    public void setUpActionView(MenuItem item, final OnClickListener listener) {
        setUpActionView(item);
        setOnMenuClickListener(listener);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (listener != null) {
                    listener.onClick(ActionIconBadgeMenuItemView.this);
                }
                return true;
            }
        });
    }

    public void setOnMenuClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnMenuLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }


    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
