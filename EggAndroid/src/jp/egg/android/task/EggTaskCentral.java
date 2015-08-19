package jp.egg.android.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import jp.egg.android.R;
import jp.egg.android.request.volley.EggVolley;
import jp.egg.android.request.volley.VolleyTag;
import jp.egg.android.request2.task.BaseFileDownloadTask;

public class EggTaskCentral {

    public static final int DEFAULT_VOLLEY_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_DISK_CACHE_SIZE = 32 * 1024 * 1024;


    public static EggTaskCentral sInstance = null;
    private Context mContext;
    private RequestQueue mVolleyQueue;
    private EggTaskQueue mQueue;


    //インスタンス
    //private ImageLoader mVolleyImageLoader;
    private com.nostra13.universalimageloader.core.ImageLoader mUnivImageLoader;

    private EggTaskCentral() {

    }

    public static EggTaskCentral initialize(Context context) {
        if (sInstance != null) return sInstance;
        EggTaskCentral central = new EggTaskCentral();
        sInstance = central;
        central.onInitialize(context);
        return central;
    }

    public static void destroy() {
        EggTaskCentral central = sInstance;
        if (central == null) return;
        central.onDestroy();
        sInstance = null;
    }

    public static EggTaskCentral getInstance() {
        if (sInstance == null)
            throw new IllegalStateException("not initialize. must call EggTaskCentral.initialize().");
        return sInstance;
    }

    private void onInitialize(Context context) {
        mContext = context.getApplicationContext();
        mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        mQueue = new EggTaskQueue();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        File cacheDir = StorageUtils.getCacheDirectory(mContext);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .memoryCache(new LruMemoryCache(DEFAULT_IMAGE_CACHE_SIZE))
                        //.memoryCacheExtraOptions( (int)(dm.widthPixels / dm.density / 2), (int)(dm.heightPixels / dm.density / 2) )
                        //.memoryCacheSize(DEFAULT_IMAGE_CACHE_SIZE)
                .diskCache(new LimitedAgeDiskCache(cacheDir, DEFAULT_IMAGE_DISK_CACHE_SIZE))
                        //.diskCacheSize(DEFAULT_IMAGE_DISC_CACHE_SIZE)
                .build();

        mUnivImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        mUnivImageLoader.init(config);


        startTask();
        startVolleyRequest();
    }

    private void onDestroy() {
        mContext = null;
        cancelVolleyRequestAll();
        stopVolleyRequest();
        stopTask();

        mUnivImageLoader.stop();
        mUnivImageLoader.destroy();
    }

    public void startVolleyRequest() {
        mVolleyQueue.start();
    }

    public void stopVolleyRequest() {
        mVolleyQueue.stop();
    }

    public void resetVolley() {
        cancelVolleyRequestAll();
        stopVolleyRequest();
        mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        startVolleyRequest();
    }


    public void cancelVolleyRequest(RequestFilter filter) {
        mVolleyQueue.cancelAll(filter);
    }

    public void cancelVolleyRquestByObject(final Object obj) {
        mVolleyQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if (request.getTag() != null && request.getTag() instanceof VolleyTag) {
                    return ((VolleyTag) request.getTag()).object == obj;
                }
                return false;
            }
        });
    }

    public void cancelVolleyRequestAll() {
        mVolleyQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }


    private void startTask() {
        mQueue.start();
    }

    private void stopTask() {
        mQueue.stop();
    }


    public void addTask(EggTask<?, ?> task) {
        mQueue.add(task);
    }


    public void addVolleyRequest(Request<?> request) {
        if (request == null) return;
        mVolleyQueue.add(request);
    }

    public void addVolleyRequestByObject(Request<?> request, Object obj) {
        if (request == null) return;

        VolleyTag tag = new VolleyTag();
        tag.object = obj;
        request.setTag(tag);

        mVolleyQueue.add(request);
    }

    public void clearVolleyCache() {
        mVolleyQueue.getCache().clear();
    }

    public void displayImage(ImageView view, int resource) {
        mUnivImageLoader.cancelDisplayTask(view);
        if (resource == 0) {
            view.setImageDrawable(null);
        } else {
            view.setImageResource(resource);
        }
    }

    public LoadImageContainer displayImageThumbOrDetail(final ImageView view, String url, String detailUrl, int defRes, boolean isDetail) {

        String curLoadUrl = (String) view.getTag(R.id.tag_loading_image);

        boolean curIsDetail = false;
        boolean reset = true;
        if (!TextUtils.isEmpty(curLoadUrl) && (curLoadUrl.equals(url) || curLoadUrl.equals(detailUrl))) {
            reset = false;
            curIsDetail = curLoadUrl.equals(detailUrl);
        }

        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        options
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .showImageForEmptyUri(defRes);

        if (reset) {
            options
                    .showImageOnLoading(defRes)
                    .displayer(new FadeInBitmapDisplayer(250, true, true, false));

        }

        if (isDetail || curIsDetail) {
            //bo.cacheInMemory(false);
            return displayImage(view, detailUrl, options.build(), null);
        } else {
            options.showImageOnFail(defRes);
            return displayImage(view, url, options.build(), null);
        }

    }

    public LoadImageContainer displayImage(final ImageView view, String url, int loadingRes) {
        return displayImage(view, url, loadingRes, null);
    }

    public LoadImageContainer displayImage(final ImageView view, String url, int loadingRes, final LoadImageListener listener) {

        String curLoadUrl = (String) view.getTag(R.id.tag_loading_image);

        boolean reset = true;
        if (!TextUtils.isEmpty(curLoadUrl) && curLoadUrl.equals(url)) {
            reset = false;
        }

        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnFail(loadingRes)
                .showImageForEmptyUri(loadingRes);

        if (reset) {
            options.showImageOnLoading(loadingRes);
        }

        return displayImage(view, url, options.build(), listener);
    }

    public LoadImageContainer displayImage(final ImageView view, String url, DisplayImageOptions options, final LoadImageListener listener) {

        view.setTag(R.id.tag_loading_image, url);

        mUnivImageLoader.displayImage(
                url,
                view,
                options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        if (listener != null) listener.onError();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (listener != null) listener.onLoaded(loadedImage, (ImageView) view);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                }
        );

        //ImageContainer ic = mVolleyImageLoader.get(url, listener, maxWidth, maxHeight);

        return new LoadImageContainer() {
            @Override
            public void cancelRequest() {
                super.cancelRequest();
                mUnivImageLoader.cancelDisplayTask(view);
            }
        };
    }

    public void loadImage(String url, final LoadImageListener listener, int maxWidth, int maxHeight) {


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
                        listener.onLoaded(loadedImage, (ImageView) view);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }
        );

    }

    public void downloadFile(String url, File output, final OnDownloadFileListener listener) {

        BaseFileDownloadTask task = new BaseFileDownloadTask(mContext, url, output) {
            @Override
            protected Object getInput() {
                return null;
            }

            @Override
            protected RequestParams getRequestParams(Object in) {
                return new RequestParams();
            }

            @Override
            protected void onDownloadProgress(long bytesWritten, long totalSize) {
                super.onDownloadProgress(bytesWritten, totalSize);
                if (listener != null) {
                    listener.onDownloadProgress(bytesWritten, totalSize);
                }
            }
        };
        task.setOnListener(new EggTaskListener() {
            @Override
            public void onSuccess(Object response) {
                if (listener != null) {
                    listener.onDownloadSuccess();
                }
            }

            @Override
            public void onError(EggTaskError error) {
                if (listener != null) {
                    listener.onDownloadFailed();
                }
            }

            @Override
            public void onCancel() {

            }
        });

        addTask(task);


    }


    public interface LoadImageListener {
        //public
        public void onLoaded(Bitmap bmp, ImageView view);

        public void onError();
    }

    public interface OnDownloadFileListener {

        void onDownloadProgress(long bytesWritten, long totalSize);

        void onDownloadSuccess();

        void onDownloadFailed();
    }

    public static class LoadImageContainer {

        public void cancelRequest() {
            //ic.cancelRequest();
        }
    }


}
