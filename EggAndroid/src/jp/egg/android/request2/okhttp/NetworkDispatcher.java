/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.egg.android.request2.okhttp;

import android.os.Process;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.BlockingQueue;

import jp.egg.android.util.Log;


public class NetworkDispatcher extends Thread {

    protected final String TAG = getClass().getSimpleName();
    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<Request<?>> mQueue;
    /**
     * The network interface for processing requests.
     */
    private final Network mNetwork;
    /**
     * For posting responses and errors.
     */
    private final ResponseDelivery mDelivery;
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue    Queue of incoming requests for triage
     * @param network  Network interface to use for performing requests
     * @param delivery Delivery interface to use for posting responses
     */
    public NetworkDispatcher(BlockingQueue<Request<?>> queue,
                             Network network,
                             ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mDelivery = delivery;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            Request<?> request;
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

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                // Perform the network request.
                NetworkResponse networkResponse = mNetwork.performRequest(request);

                // If the server returned 304 AND we delivered a response already,
                // we're done -- don't deliver a second identical response.
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("not-modified");
                    continue;
                }

                // Parse the response here on the worker thread.
                Response<?> response = request.parseNetworkResponse(networkResponse);

                // Post the response back.
                request.markDelivered();

                mDelivery.postResponse(request, response);
            } catch (VolleyError volleyError) {
                parseAndDeliverNetworkError(request, volleyError);
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception " + e.toString());
                mDelivery.postError(request, new VolleyError(e));
            }
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }


}
