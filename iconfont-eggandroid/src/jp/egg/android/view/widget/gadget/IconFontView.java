package jp.egg.android.view.widget.gadget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import jp.egg.android.manager.IconFontManager;

/**
 * Created by chikara on 2014/10/10.
 */
public class IconFontView extends TextView {

    private static final String TAG = IconFontView.class.getSimpleName();

    public IconFontView(Context context) {
        super(context);
        setUpCustomFont();
    }

    public IconFontView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpCustomFont();
    }

    public IconFontView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpCustomFont();
    }

    public static boolean setCustomFont(Context context, TextView view, String path) {
        Typeface tf = null;
        try {
            // ここでフォントファイル読み込み。
            // 読み込み済みならキャッシュから。
            tf = IconFontManager.getIconTypeface(context, path);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        view.setTypeface(tf);
        return true;
    }

    private final void setUpCustomFont() {
        String path = onCustomFontSetUp();
        if (path != null) {
            setCustomFont(getContext(), this, path);
        }
    }

    protected String onCustomFontSetUp() {
        return null;
    }


}
