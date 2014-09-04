package jp.egg.android.util;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chikara on 2014/08/30.
 */
public class AUtil {

    public static void makeAboutSpannable(SpannableStringBuilder span, String str_link, String replace, final Runnable on_click){
        Pattern pattern = Pattern.compile(str_link);
        Matcher matcher = pattern.matcher(span);
        ForegroundColorSpan color_theme = new ForegroundColorSpan(Color.parseColor("#53b7bb"));
        if(matcher.find()) {
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if(on_click!=null) on_click.run();
                }
            }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(color_theme,
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(replace!=null) span.replace(matcher.start(), matcher.end(), replace);
        }
    }

    public static void makeClickableTagsSpannable(Context context, SpannableStringBuilder span, String word, int textAppearance, final Runnable on_click){
        //ForegroundColorSpan color_theme = new ForegroundColorSpan(color);
        TextAppearanceSpan appearanceSpan = new TextAppearanceSpan(context, textAppearance);
        int start = span.length();
        span.append(word);
        int end = span.length();
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if(on_click!=null) on_click.run();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                //ds.setColor(ds.linkColor);
                //ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(appearanceSpan,
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }


    public static boolean isInternetConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm==null) return false;

        boolean is_connect = false;

        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null){
            if( info.isConnected() ) is_connect = true;
        }

        return is_connect;
    }


    public static void deleteAppDatabases(Context context){
        String[] dbs = context.databaseList();
        if( dbs==null || dbs.length==0 ) return;
        for( String db : dbs ) {
            context.deleteDatabase(db);
        }
    }

    public static void deleteCacheData(Context context){
        File cacheDir = context.getCacheDir();
        if( cacheDir == null ) return;
        try {
            FileUtils.cleanDirectory(cacheDir);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void deleteAppFiles(Context context){
        File fileDir = context.getFilesDir();
        if( fileDir == null ) return;
        try {
            FileUtils.cleanDirectory(fileDir);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void deleteSharedPreferences(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            ApplicationInfo info = appContext.getPackageManager()
                    .getApplicationInfo(appContext.getPackageName(), 0);
            String dirPath = info.dataDir + File.separator + "shared_prefs"
                    + File.separator;
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                String[] list = dir.list();
                int size = list.length;
                for (int i = 0; i < size; i++) {
                    new File(dirPath + list[i]).delete();
                }
            } else {
                //Log.d("AAA", "NO FILE or NOT DIR");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAppDataAll(Context context){

        deleteAppDatabases(context);
        deleteCacheData(context);
        deleteAppFiles(context);
        deleteSharedPreferences(context);

    }

}
