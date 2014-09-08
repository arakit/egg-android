package jp.egg.android.task;

import java.io.File;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import jp.egg.android.R;
import jp.egg.android.request.volley.EggVolley;
import jp.egg.android.request.volley.VolleyTag;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import javax.microedition.khronos.opengles.GL10;

public class EggTaskCentral {

    private static final int DEFAULT_VOLLEY_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_DISC_CACHE_SIZE = 32 * 1024 * 1024;


    public static EggTaskCentral sInstance = null;

    public static EggTaskCentral initialize(Context context){
        if(sInstance != null) return sInstance;
        EggTaskCentral central = new EggTaskCentral();
        sInstance = central;
        central.onInitialize(context);
        return central;
    }
    public static void destroy(){
        EggTaskCentral central = sInstance;
        if(central == null) return ;
        central.onDestroy();
        sInstance = null;
    }
    public static EggTaskCentral getInstance(){
        if(sInstance == null) throw new IllegalStateException("not initialize. must call EggTaskCentral.initialize().");
        return sInstance;
    }



    //インスタンス

    private Context mContext;
    private RequestQueue mVolleyQueue;
    private EggTaskQueue mQueue;
    //private ImageLoader mVolleyImageLoader;
    private com.nostra13.universalimageloader.core.ImageLoader mUnivImageLoader;


    private EggTaskCentral() {

    }


    private void onInitialize(Context context){
        mContext = context.getApplicationContext();
        mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        mQueue = new EggTaskQueue();
        //mVolleyImageLoader = new ImageLoader(mVolleyQueue, new BitmapLruCache(DEFAULT_IMAGE_CACHE_SIZE));

        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        File cacheDir = StorageUtils.getCacheDirectory(mContext);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .memoryCache(new LruMemoryCache(DEFAULT_IMAGE_CACHE_SIZE))
                //.memoryCacheExtraOptions( (int)(dm.widthPixels / dm.density / 2), (int)(dm.heightPixels / dm.density / 2) )
                        //.memoryCacheSize(DEFAULT_IMAGE_CACHE_SIZE)
                .diskCache(new LimitedAgeDiscCache(cacheDir, DEFAULT_IMAGE_DISC_CACHE_SIZE))
                        //.diskCacheSize(DEFAULT_IMAGE_DISC_CACHE_SIZE)
                .build();

        mUnivImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        mUnivImageLoader.init(config);


        startTask();
        startVolleyRequest();
    }
    private void onDestroy(){
        mContext = null;
        cancelVolleyRequestAll();
        stopVolleyRquest();
        stopTask();

        mUnivImageLoader.stop();
        mUnivImageLoader.destroy();
    }

    private void startVolleyRequest(){
        mVolleyQueue.start();
    }
    private void stopVolleyRquest(){
        mVolleyQueue.stop();
    }

    public void resetVolley(){
        cancelVolleyRequestAll();
        stopVolleyRquest();
        mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        startVolleyRequest();
    }



    public void cancelVolleyRquest(RequestFilter filter){
        mVolleyQueue.cancelAll(filter);
    }
    public void cancelVolleyRquestByObject(final Object obj){
        mVolleyQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if(request.getTag()!=null && request.getTag() instanceof VolleyTag ){
                    return ((VolleyTag) request.getTag()).object == obj;
                }
                return false;
            }
        });
    }
    public void cancelVolleyRequestAll(){
        mVolleyQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }



    private void startTask(){
        mQueue.start();
    }
    private void stopTask(){
        mQueue.stop();
    }


    public void addTask(EggTask<?,?> task){
        mQueue.add(task);
    }


    public void addVolleyRequestByObject(Request<?> request, Object obj){
        if(request == null) return ;

        VolleyTag tag = new VolleyTag();
        tag.object = obj;
        request.setTag(tag);

        mVolleyQueue.add(request);
    }

    public void clearVolleyCache(){
        mVolleyQueue.getCache().clear();
    }




    public static class LoadImageContainer{

//		final ImageContainer ic;
//
//		LoadImageContainer(ImageContainer ic) {
//			this.ic = ic;
//		}

        public void cancelRequest(){
            //ic.cancelRequest();
        }
    }

    public interface LoadImageListener{
        //public
        public void onLoaded(Bitmap bmp, ImageView view);
        public void onError();
    }

    public void displayImage(ImageView view, int resource){
        mUnivImageLoader.cancelDisplayTask(view);
        if( resource == 0 ) {
            view.setImageDrawable(null);
        }else{
            view.setImageResource(resource);
        }
    }

    public LoadImageContainer displayImage(final ImageView view, String url, int loadingRes){
        return displayImage(view, url, loadingRes, null);
    }

    public LoadImageContainer displayImage(final ImageView view, String url, int loadingRes, final LoadImageListener listener){

//        BitmapFactory.Options decode = new BitmapFactory.Options();
//        decode.inSampleSize = 8;


        DisplayImageOptions option = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                //.decodingOptions(decode)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))
                .showImageOnLoading(loadingRes)
                .showImageOnFail(loadingRes)
                .showImageForEmptyUri(loadingRes)
                .build()
                ;

        mUnivImageLoader.displayImage(
                url,
                view,
                option,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        if(listener!=null) listener.onError();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        if (loadedImage.getHeight() > GL10.GL_MAX_TEXTURE_SIZE) {
//                            float scale = (float) loadedImage.getWidth() / visibleWidth;
//                            //Logger.d(TAG, "onLoadingComplete :: scale = " + scale);
//                            Bitmap bitmap = Bitmap.createBitmap(loadedImage, 0, 0,
//                                    loadedImage.getWidth(),
//                                    (int) (GL10.GL_MAX_TEXTURE_SIZE * scale));
//                            item.mContentsItemImage.setImageBitmap(bitmap);
//                        }
                        if(listener!=null) listener.onLoaded(loadedImage, (ImageView)view);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                }
        );

        //ImageContainer ic = mVolleyImageLoader.get(url, listener, maxWidth, maxHeight);

        return new LoadImageContainer(){
            @Override
            public void cancelRequest() {
                super.cancelRequest();
                mUnivImageLoader.cancelDisplayTask(view);
            }
        };
    }


    public void loadImage(String url, final LoadImageListener listener, int maxWidth, int maxHeight){


        mUnivImageLoader.loadImage(
                url,
                new ImageSize(maxWidth, maxHeight),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        listener.onError();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        listener.onLoaded(loadedImage, (ImageView)view);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }
        );

    }



}
