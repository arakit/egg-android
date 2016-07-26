package jp.egg.android.request2.okhttp;

/**
 * Created by chikara on 16/07/26.
 */
public interface RequestQueue {

    public void start();

    public void stop();

    public void add(Request<?> request);

    public void cancelAll(RequestFilter filter);

    public interface RequestFilter {
        public boolean apply(Request<?> request);
    }

}
