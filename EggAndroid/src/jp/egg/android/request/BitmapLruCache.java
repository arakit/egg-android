package jp.egg.android.request;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapLruCache implements ImageCache {
    
    private LruCache<String, Bitmap> mCache;
    
    
    public BitmapLruCache() {
    	this(10 * 1024 * 1024);
    }
     
    public BitmapLruCache(int maxSize) {

        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }
 
    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }
 
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }
 
}