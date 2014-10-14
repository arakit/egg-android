package jp.egg.android.manager;

import android.content.Context;
import android.graphics.Typeface;
import jp.egg.android.util.Log;

import java.util.Hashtable;

/**
 * Created by chikara on 2014/10/10.
 */
public class IconFontManager {

    private static final String TAG = IconFontManager.class.getSimpleName();

    // 例
    // private static final String ICON = "fonts/icon.ttf";

    private static final Hashtable<String, Typeface> sCache = new Hashtable<String, Typeface>();

    public static Typeface getIconTypeface(Context context, String assetPath) {
        synchronized (sCache) {
            if (!sCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(context.getAssets(), assetPath);
                    sCache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }
            return sCache.get(assetPath);
        }
    }


}
