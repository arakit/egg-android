package jp.egg.android.request2.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chikara on 16/07/26.
 */
public class RequestQueueImpl implements RequestQueue, QueueHandler {


    /**
     * Number of network request dispatcher threads to start.
     */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    /**
     * Staging area for requests that already have a duplicate request in flight.
     * <p/>
     * <ul>
     * <li>containsKey(cacheKey) indicates that there is a request in flight for the given cache
     * key.</li>
     * <li>get(cacheKey) returns waiting requests for the given cache key. The in flight request
     * is <em>not</em> contained in that list. Is null if no requests are staged.</li>
     * </ul>
     */
    // private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap<>();
    /**
     * Default array capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<Request<?>> mCurrentRequests = new HashSet<>();
    /**
     * The queue of requests that are actually going out to the network.
     */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue =
            new PriorityBlockingQueue<>(DEFAULT_INITIAL_CAPACITY, new Comparator<Request<?>>() {
                @Override
                public int compare(Request<?> lhs, Request<?> rhs) {
                    Request.Priority left = lhs.getPriority();
                    Request.Priority right = rhs.getPriority();

                    // High-priority requests are "lesser" so they are sorted to the front.
                    // Equal priorities are sorted by sequence number to provide FIFO ordering.
                    return left == right ?
                            lhs.getSequence() - rhs.getSequence() :
                            right.ordinal() - left.ordinal();
                }
            });
    /**
     * Network interface for performing requests.
     */
    private final Network mNetwork;
    /**
     * Response delivery mechanism.
     */
    private final ResponseDelivery mDelivery;
    /**
     * Used for generating monotonically-increasing sequence numbers for requests.
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    /**
     * The network dispatchers.
     */
    private NetworkDispatcher[] mDispatchers;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery       A ResponseDelivery interface for posting responses and errors
     */
    public RequestQueueImpl(Network network, int threadPoolSize,
                            ResponseDelivery delivery) {
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPoolSize];
        mDelivery = delivery;
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     */
    public RequestQueueImpl(Network network, int threadPoolSize) {
        this(network, threadPoolSize,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network A Network interface for performing HTTP requests
     */
    public RequestQueueImpl(Network network) {
        this(network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();  // Make sure any currently running dispatchers are stopped.
        // Create the cache dispatcher and start it.

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     *
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getTag() == tag;
            }
        });
    }


    /**
     * Adds a Request to the dispatch queue.
     *
     * @param request The request to service
     * @return The passed-in request
     */
    @Override
    public void add(Request<?> request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        synchronized (mCurrentRequests) {
            // Process requests in the order they are added.
            request.enqueued(this, getSequenceNumber());
            // add
            mCurrentRequests.add(request);
        }

        mNetworkQueue.add(request);

//        // If the request is uncacheable, skip the cache queue and go straight to the network.
//        if (!request.shouldCache()) {
//            mNetworkQueue.add(request);
//            return request;
//        }
//
//        // Insert request into stage if there's already a request with the same cache key in flight.
//        synchronized (mWaitingRequests) {
//            String cacheKey = request.getCacheKey();
//            if (mWaitingRequests.containsKey(cacheKey)) {
//                // There is already a request in flight. Queue up.
//                Queue<Request<?>> stagedRequests = mWaitingRequests.get(cacheKey);
//                if (stagedRequests == null) {
//                    stagedRequests = new LinkedList<Request<?>>();
//                }
//                stagedRequests.add(request);
//                mWaitingRequests.put(cacheKey, stagedRequests);
//            } else {
//                // Insert 'null' queue for this cacheKey, indicating there is now a request in
//                // flight.
//                mWaitingRequests.put(cacheKey, null);
//                mCacheQueue.add(request);
//            }
//            return request;
//        }
    }

    @Override
    public void finish(Request request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }

}
