package jp.egg.android.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.egg.android.R;
import jp.egg.android.request.volley.EggVolley;
import jp.egg.android.request.volley.VolleyTag;
import jp.egg.android.request2.okhttp.OkHttpNetwork;
import jp.egg.android.request2.okhttp.RequestQueueImpl;
import jp.egg.android.request2.task.BaseFileDownloadTask;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public final class EggTaskCentral {

    public static final int DEFAULT_VOLLEY_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_CACHE_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_IMAGE_DISK_CACHE_SIZE = 32 * 1024 * 1024;


    public static EggTaskCentral sInstance = null;
    private Context mContext;
    @Deprecated private RequestQueue mVolleyQueue;
    private jp.egg.android.request2.okhttp.RequestQueue mRequestQueue;
    private EggTaskQueue mQueue;


    //インスタンス
    //private ImageLoader mVolleyImageLoader;
    private com.nostra13.universalimageloader.core.ImageLoader mUnivImageLoader;

    private EggTaskCentral() {

    }

    public static final class Options {
        public boolean volleyEnabled = false;
        public boolean requestQueueEnabled = false;
    }

    public static EggTaskCentral initialize(Context context, Options options) {
        if (sInstance != null) {
            return sInstance;
        }
        EggTaskCentral central = new EggTaskCentral();
        sInstance = central;
        central.onInitialize(context, options);
        return central;
    }

    public static void destroy() {
        EggTaskCentral central = sInstance;
        if (central == null) {
            return;
        }
        central.onDestroy();
        sInstance = null;
    }

    public static EggTaskCentral getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("not initialize. must call EggTaskCentral.initialize().");
        }
        return sInstance;
    }

    private jp.egg.android.request2.okhttp.RequestQueue createRequestQueue() {
        return new RequestQueueImpl(
                new OkHttpNetwork(
                        new OkHttpClient.Builder().build()
                ));
    }

    private void onInitialize(Context context, Options options) {
        mContext = context.getApplicationContext();
        mQueue = new EggTaskQueue();
        if (options.volleyEnabled) {
            mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        }
        if (options.requestQueueEnabled) {
            mRequestQueue = createRequestQueue();
        }

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
        startRequestQueue();
    }

    private void onDestroy() {
        mContext = null;

        cancelVolleyRequestAll();
        stopVolleyRequest();

        cancelRequestQueueAll();
        stopRequestQueue();

        stopTask();

        mUnivImageLoader.stop();
        mUnivImageLoader.destroy();
    }

    @Deprecated
    public void startVolleyRequest() {
        if (mVolleyQueue != null) {
            mVolleyQueue.start();
        }
    }

    @Deprecated
    public void stopVolleyRequest() {
        if (mVolleyQueue != null) {
            mVolleyQueue.stop();
        }
    }


    /**
     * リクエスト開始
     */
    public void startRequestQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.start();
        }
    }

    /**
     * リクエスト停止
     */
    public void stopRequestQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.stop();
        }
    }


    @Deprecated
    public void resetVolley() {
        if (mVolleyQueue==null) {
            return;
        }
        cancelVolleyRequestAll();
        stopVolleyRequest();
        mVolleyQueue = EggVolley.newRequestQueue(mContext, DEFAULT_VOLLEY_CACHE_SIZE);
        startVolleyRequest();
    }

    /**
     * リクエストをすべてクリアして、再生性して、リセットする
     */
    public void resetRequestQueue() {
        if (mRequestQueue == null) {
            return;
        }
        cancelRequestQueueAll();
        stopRequestQueue();
        mRequestQueue = createRequestQueue();
        startRequestQueue();
    }


    @Deprecated
    public void cancelVolleyRequest(RequestFilter filter) {
        if (mVolleyQueue!=null) {
            mVolleyQueue.cancelAll(filter);
        }
    }

    @Deprecated
    public void cancelVolleyRquestByObject(final Object obj) {
        if (mVolleyQueue == null) {
            return;
        }
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

    @Deprecated
    public void cancelVolleyRequestAll() {
        if (mVolleyQueue == null) {
            return;
        }
        mVolleyQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }


    public void cancelRequestQueue(jp.egg.android.request2.okhttp.RequestQueue.RequestFilter filter) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(filter);
        }
    }

    public void cancelRequestQueueAll() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(null);
        }
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


    @Deprecated
    public void addRequest(Request<?> request) {
        if (request == null) {
            return;
        }
        mVolleyQueue.add(request);
    }

    public void addRequest(jp.egg.android.request2.okhttp.Request<?> request) {
        if (request == null) {
            return;
        }
        mRequestQueue.add(request);
    }

    @Deprecated
    public void addRequestByObject(Request<?> request, Object obj) {
        if (request == null) {
            return;
        }

        VolleyTag tag = new VolleyTag();
        tag.object = obj;
        request.setTag(tag);

        mVolleyQueue.add(request);
    }

    public void addRequestByObject(jp.egg.android.request2.okhttp.Request<?> request, Object obj) {
        if (request == null) {
            return;
        }

        VolleyTag tag = new VolleyTag();
        tag.object = obj;
        request.setTag(tag);

        mRequestQueue.add(request);
    }

    @Deprecated
    public void clearVolleyCache() {
        if (mVolleyQueue!=null) {
            mVolleyQueue.getCache().clear();
        }
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
        return displayImageInternalWithImageSize(view, url, loadingRes, (ImageSize) null, listener);
    }
    public LoadImageContainer displayImageWithSize(final ImageView view, String url, int loadingRes, int width, int height) {
        ImageSize imageSize = (width < 0 || height < 0) ? (ImageSize)null : new ImageSize(width, height);
        return displayImageWithSize(view, url, loadingRes, width, height, (LoadImageListener)null);
    }
    public LoadImageContainer displayImageWithSize(final ImageView view, String url, int loadingRes, int width, int height, final LoadImageListener listener) {
        ImageSize imageSize = (width < 0 || height < 0) ? (ImageSize)null : new ImageSize(width, height);
        return displayImageInternalWithImageSize(view, url, loadingRes, imageSize, listener);
    }

    private LoadImageContainer displayImageInternalWithImageSize(final ImageView view, String url, int loadingRes, @Nullable ImageSize imageSize, final LoadImageListener listener) {

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

        return displayImageInternal(view, url, imageSize, options.build(), listener);
    }

    public LoadImageContainer displayImage(final ImageView view, String url, DisplayImageOptions options, final LoadImageListener listener) {
        return displayImageInternal(view, url, null, options, listener);
    }

    private LoadImageContainer displayImageInternal(final ImageView view, String url, @Nullable ImageSize imageSize, DisplayImageOptions options, final LoadImageListener listener) {

        view.setTag(R.id.tag_loading_image, url);

        mUnivImageLoader.displayImage(
                url,
                new ImageViewAware(view),
                options,
                imageSize,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (listener != null) {
                            listener.onStart();
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        if (listener != null) {
                            listener.onError();
                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (listener != null) {
                            listener.onLoaded(loadedImage, (ImageView) view);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                },
                null
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


    public Bitmap loadImageSync(String url, int maxWidth, int maxHeight) {
        return mUnivImageLoader.loadImageSync(url, new ImageSize(maxWidth, maxHeight));
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

    public void downloadFile(String url, final File output, final OnDownloadFileListener listener) {

        final Handler handlerForListener = listener != null ? new Handler(Looper.getMainLooper()) : null;
        BaseFileDownloadTask task = new BaseFileDownloadTask(mContext, Request.Method.GET, url) {

            private long lastNotifyProgressTime = 0;

            @Override
            protected Object getInput() {
                return null;
            }

            @Override
            protected RequestBody getRequestBody(Object in) {
                return null;
            }

            @Override
            protected Object getOutput(ResponseBody body) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(output);
                    IOUtils.copy(inputStream, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                    }
                    if (outputStream != null) {
                        IOUtils.closeQuietly(outputStream);
                    }
                }
                return output;
            }

            @Override
            protected void onResponseProgress(final long bytesWritten, final long contentLength) {
                super.onResponseProgress(bytesWritten, contentLength);
                if (listener == null) {
                    return;
                }
                long now = System.currentTimeMillis();
                if (now - this.lastNotifyProgressTime > 400) {
                    this.lastNotifyProgressTime = now;
                    handlerForListener.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDownloadProgress(bytesWritten, contentLength);
                        }
                    });
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

        void onStart();

        void onLoaded(Bitmap bmp, ImageView view);

        void onError();

        void onCancel();
    }

    public interface OnDownloadFileListener {

        void onDownloadProgress(long bytesWritten, long totalSize);

        void onDownloadSuccess();

        void onDownloadFailed();
    }

    public static class LoadImageContainer {

        public void cancelRequest() {

        }
    }


}
