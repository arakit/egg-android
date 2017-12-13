package jp.egg.android.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chikara on 2015/01/12.
 */
public final class JobWaiter {

    public static final String TAG = "JobWaiter";

    private final List<Object> jobs = new ArrayList<Object>();

    public JobWaiter() {

    }

    public final synchronized Object job() {
        Object job = new Object();
        jobs.add(job);
        Log.d(TAG, "job start " + job);
        return job;
    }

    public final synchronized void complete(Object job) {
        jobs.remove(job);
        Log.d(TAG, "job end " + job);
        this.notifyAll();
    }

    public final synchronized void waitForCompleteAll() {
        while (jobs.size() > 0) {
            try {
                JobWaiter.this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "job wait, rest " + jobs.size());
        }
    }

    public final synchronized boolean isEmpty() {
        return jobs.isEmpty();
    }

    public final void listenCompleteAll(final Runnable callback) {
        HandlerUtil.postBackground(new Runnable() {
            @Override
            public void run() {
                waitForCompleteAll();
                if (callback != null) {
                    HandlerUtil.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.run();
                        }
                    });
                }
            }
        });
    }

}
