package jp.egg.android.ui.menu.action;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import jp.egg.android.R;

/**
 * Created by chikara on 2014/09/13.
 */
@Deprecated
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class BadgeActionProvider extends ActionProvider {

    private Context mContext;

    /**
     * Creates a new instance. ActionProvider classes should always implement a
     * constructor that takes a single Context parameter for inflating from menu XML.
     *
     * @param context Context for accessing resources.
     */
    public BadgeActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        //View view = super.onCreateActionView(forItem);
        View view = LayoutInflater.from(mContext).inflate(R.layout.ab_action_menu_layout, null);
        ImageView iconView = (ImageView) view.findViewById(android.R.id.icon);
        TextView badgeView = (TextView) view.findViewById(R.id.badge);
        iconView.setImageDrawable(forItem.getIcon());
        badgeView.setText("");
        badgeView.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    private void updateInfoBadge(View view, int num){
        TextView badgeView = (TextView) view.findViewById(R.id.badge);
        if( num >= 100 ) {
            badgeView.setText("99+");
            badgeView.setVisibility(View.VISIBLE);
        }else if( num > 0 ) {
            badgeView.setText("" + num);
            badgeView.setVisibility(View.VISIBLE);
        }else{
            badgeView.setText("");
            badgeView.setVisibility(View.INVISIBLE);
        }
    }

}
