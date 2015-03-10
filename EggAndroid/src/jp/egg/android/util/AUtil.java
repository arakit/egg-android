package jp.egg.android.util;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Pair;
import android.view.View;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
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


    public static final boolean checkImageAndSize(Context context, Uri uri,long bytes){
        if(uri==null) return false;

        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, imageOptions);
            //android.util.Log.d("test77", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);
            is.close();
            is = null;
            if(imageOptions.outWidth <= 0 || imageOptions.outHeight<=0 ){
                return false;
            }
            is = context.getContentResolver().openInputStream(uri);
            long fileSizeCounter = 0;
            long size;
            byte[] buf = new byte[8192];
            while( (size = is.read(buf, 0 , buf.length)) != -1 ){
                //android.util.Log.d("test77", "size="+size);
                fileSizeCounter += size;
            }
            is.close();
            is=null;
            if( fileSizeCounter > bytes ){
                return false;
            }
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ex) {}
        }
    }

    public static Bitmap normalizeExifRotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Bitmap getBitmapFromUri(Context context, Uri uri) {
        if (uri == null) return null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeStream(is, null, imageOptions);
            return bmp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ex) {}
        }
    }

    public static final Bitmap getBitmapFromUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        if (uri == null) return null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // Set height and width in options, does not return an image and no resource taken
            BitmapFactory.decodeStream(is, null, options);
            int pow = 0;
            while (options.outHeight >> pow > maxHeight || options.outWidth >> pow > maxWidth) {
                pow += 1;
            }
            is.close();
            is = null;
            is = context.getContentResolver().openInputStream(uri);
            options.inSampleSize = 1 << pow;
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
            return bmp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ex) {}
        }
    }

    public static final Bitmap getBitmapFromFile(File file, int maxWidth, int maxHeight) {
        if (file == null) return null;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // Set height and width in options, does not return an image and no resource taken
            BitmapFactory.decodeStream(is, null, options);
            int pow = 0;
            while (options.outHeight >> pow > maxHeight || options.outWidth >> pow > maxWidth) {
                pow += 1;
            }
            is.close();
            is = null;
            is = new BufferedInputStream(new FileInputStream(file));
            options.inSampleSize = 1 << pow;
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
            return bmp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ex) {}
        }
    }


    public static final Bitmap getBitmapFromRaw(Context context, int resId) {
        if (resId <= 0) return null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resId);
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeStream(is, null, imageOptions);
            return bmp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try { if (is != null) is.close(); } catch (Exception ex) {}
        }
    }

    public static class BitmapOutInfo {
        /**
         *
         * <p>outWidth will be set to -1 if there is an error trying to decode.</p>
         */
        public int outWidth;

        /**
         *
         * <p>outHeight will be set to -1 if there is an error trying to decode.</p>
         */
        public int outHeight;

        /**
         * If known, this string is set to the mimetype of the decoded image.
         * If not know, or there is an error, it is set to null.
         */
        public String outMimeType;
    }

    public static final BitmapOutInfo getBitmapOutInfoFromUri(Context context, Uri uri) {
        if (uri == null) return null;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // Set height and width in options, does not return an image and no resource taken
            BitmapFactory.decodeStream(is, null, options);

            BitmapOutInfo result = new BitmapOutInfo();
            result.outWidth = options.outWidth;
            result.outHeight = options.outHeight;
            result.outMimeType = options.outMimeType;
            return result;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static final BitmapOutInfo getBitmapOutInfoFromFile(File file) {
        if (file == null) return null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // Set height and width in options, does not return an image and no resource taken
            BitmapFactory.decodeStream(is, null, options);

            BitmapOutInfo result = new BitmapOutInfo();
            result.outWidth = options.outWidth;
            result.outHeight = options.outHeight;
            result.outMimeType = options.outMimeType;
            return result;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    public static File makeTmpFile(Context context, String prefix, String suffix){
        File tmpOutFile = null;
        try {
//            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File dir = ContextCompat.getExternalCacheDirs(context)[0];
            tmpOutFile = File.createTempFile(prefix , suffix, dir);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return tmpOutFile;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private String encodeParameters(List<Pair<String, String>> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Pair<String, String> entry : params) {
                encodedParams.append(URLEncoder.encode(entry.first, paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.second, paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }


    public static final Bitmap newBitmapSquareCenterCrop (Bitmap bitmap) {

        if ( bitmap == null ) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left, top, right, bottom;

        if ( width < height ) {
            int cropSize = width;
            left = 0;
            right = cropSize;
            top = height / 2 - cropSize / 2;
            bottom = top + cropSize;
        } else {
            int cropSize = height;
            top = 0;
            bottom = cropSize;
            left = width / 2 - cropSize / 2;
            right = left + cropSize;
        }

        return Bitmap.createBitmap(bitmap, left, top, right-left, bottom-top);
    }



    public static void drawVerticalCenterText(Canvas canvas, String text, float x, float y, TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        // フォントの高さを求める
        float fh = fm.descent - fm.ascent;
        // センター合わせにしたあと、Baselineの位置を求めるためにdescentを引く
        float ty = + (fh/2f) - fm.descent;
        canvas.drawText(text, x, y + ty, textPaint);
    }

    public static void drawVerticalBottomText(Canvas canvas, String text, float x, float y, TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float ty = - fm.descent;
        canvas.drawText(text, x, y + ty, textPaint);
    }

    public static void drawVerticalTopText(Canvas canvas, String text, float x, float y, TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float ty = - fm.ascent;
        canvas.drawText(text, x, y + ty, textPaint);
    }




    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

}
