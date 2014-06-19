package jp.egg.android.task.central;

import jp.egg.android.task.EggTask;
import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;

import com.android.volley.Request;

public class ExecuteThread extends Thread{


	private EggTaskQueue mQueue;
//	   /** The queue of requests to service. */
//    private final BlockingQueue<EggTask<?, ?>> mQueue;
//    /** The network interface for processing requests. */
//    private final Network mNetwork;
//    /** For posting responses and errors. */
//    private final ResponseDelivery mDelivery;
    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue Queue of incoming requests for triage
     * @param network Network interface to use for performing requests
     * @param cache Cache interface to use for writing responses to cache
     * @param delivery Delivery interface to use for posting responses
     */
    public ExecuteThread(EggTaskQueue queue) {
        mQueue = queue;
        //mNetwork = network;
        //mCache = cache;
        //mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request<?> request) {
        // Tag the request (if API >= 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        EggTask<?, ?> request;
        while (true) {
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                request.addMarker("network-queue-take");

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                	finishRequest(request, "network-discard-cancelled");
                    //request.finish("network-discard-cancelled");
                    continue;
                }

                request.execute();

                //addTrafficStatsTag(request);

//                // Perform the network request.
//                NetworkResponse networkResponse = mNetwork.performRequest(request);
//                request.addMarker("network-http-complete");
//
//                // If the server returned 304 AND we delivered a response already,
//                // we're done -- don't deliver a second identical response.
//                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
//                    request.finish("not-modified");
//                    continue;
//                }
//
//                // Parse the response here on the worker thread.
//                Response<?> response = request.parseNetworkResponse(networkResponse);
//                request.addMarker("network-parse-complete");

//                // Write to cache if applicable.
//                // TODO: Only update cache metadata instead of entire record for 304s.
//                if (request.shouldCache() && response.cacheEntry != null) {
//                    mCache.put(request.getCacheKey(), response.cacheEntry);
//                    request.addMarker("network-cache-written");
//                }

                // Post the response back.
                //request.markDelivered();
                //mDelivery.postResponse(request, response);
            }
//            catch (VolleyError volleyError) {
//                parseAndDeliverNetworkError(request, volleyError);
//            }
            catch (Exception e) {
                //VolleyLog.e(e, "Unhandled exception %s", e.toString());
                //mDelivery.postError(request, new VolleyError(e));
            }

        }
    }

//    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
//        error = request.parseNetworkError(error);
//        mDelivery.postError(request, error);
//    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     *
     * <p>Also dumps all events from this request's event log; for debugging.</p>
     */
    private void finishRequest(EggTask<?, ?> task, final String tag) {
//        if (mQueue != null) {
//            mQueue.finish(this);
//        }
//        if (MarkerLog.ENABLED) {
//            final long threadId = Thread.currentThread().getId();
//            if (Looper.myLooper() != Looper.getMainLooper()) {
//                // If we finish marking off of the main thread, we need to
//                // actually do it on the main thread to ensure correct ordering.
//                Handler mainThread = new Handler(Looper.getMainLooper());
//                mainThread.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mEventLog.add(tag, threadId);
//                        mEventLog.finish(this.toString());
//                    }
//                });
//                return;
//            }
//
//            mEventLog.add(tag, threadId);
//            mEventLog.finish(this.toString());
//        } else {
//            long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
//            if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
//                VolleyLog.d("%d ms: %s", requestTime, this.toString());
//            }
//        }
    }

}
