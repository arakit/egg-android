package jp.egg.android.util;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by chikara on 2014/07/17.
 */
public class ToastUtil {

    private static final LinkedList<Data> sToastQueue = new LinkedList<Data>();
    private static final Runnable sToasQueueRunnable = new HandleToastQueueRunnable();
    private static WeakReference<Toast> sToastWeak;
    private static long sLastToastShowTime = 0;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static int sToastInterval = 500;
    private static int sMaxDelay = 1000;

    private static void clearToast() {
        if (sToastWeak != null) {
            Toast prevToast = sToastWeak.get();
            if (prevToast != null) {
                prevToast.cancel();
            }
            sToastWeak = null;
        }
    }

    private static void request(Data data) {
        sToastQueue.offer(data);
        handleNext();
    }

    private static void handleNext() {

        long nextTime = getNextHandlingTime();
        if (nextTime == -1) {
            return;
        }

        long now = SystemClock.uptimeMillis();
        long nextDelay = nextTime - now;

        if (nextDelay <= 0) {
            handleToastQueue();
            return;
        }

        sHandler.removeCallbacks(sToasQueueRunnable);
        sHandler.postDelayed(sToasQueueRunnable, nextDelay);
    }

    private static long getNextHandlingTime() {
        Data data = sToastQueue.peek();
        if (data == null) {
            return -1;
        }
        long deadline = data.time + sMaxDelay;
        long nextToastTime = sLastToastShowTime + sToastInterval;
        return Math.min(deadline, nextToastTime);
    }

    private static void handleToastQueue() {
        Data data;
        long now = SystemClock.uptimeMillis();
        while ((data = sToastQueue.peek()) != null) {
            long deadline = data.time + sMaxDelay;
            long nextToastTime = sLastToastShowTime + sToastInterval;
            if (now >= deadline) {
                sToastQueue.remove(data);
                show(data, true);
            } else if (now >= nextToastTime) {
                sToastQueue.remove(data);
                show(data, false);
                break;
            } else {
                break;
            }
        }
        handleNext();
    }

    private static void show(Data data, boolean directly) {

        Toast toast;
        boolean isNewToast;

        if (directly) {
            toast = sToastWeak != null ? sToastWeak.get() : null;
        } else {
            clearToast();
            toast = null;
        }
        if (data.messageResId != 0) {
            if (toast != null) {
                isNewToast = false;
                toast.setText(data.messageResId);
                toast.setDuration(data.messageResId);
            } else {
                isNewToast = true;
                toast = Toast.makeText(
                        data.context,
                        data.messageResId,
                        data.duration
                );
            }
        } else {
            if (toast != null) {
                isNewToast = false;
                toast.setText(data.message);
                toast.setDuration(data.duration);
            } else {
                isNewToast = true;
                toast = Toast.makeText(
                        data.context,
                        data.message,
                        data.duration
                );
            }
        }
        if (isNewToast) {
            sToastWeak = new WeakReference<Toast>(toast);
            sLastToastShowTime = SystemClock.uptimeMillis();
            toast.show();
        }
    }

    public static void shortMessage(Context context, String message) {
        Data data = new Data();
        data.context = context.getApplicationContext();
        data.time = SystemClock.uptimeMillis();
        data.message = message;
        data.duration = Toast.LENGTH_SHORT;
        request(data);
    }

    public static void shortMessage(Context context, int messageResId) {
        Data data = new Data();
        data.context = context.getApplicationContext();
        data.time = SystemClock.uptimeMillis();
        data.messageResId = messageResId;
        data.duration = Toast.LENGTH_SHORT;
        request(data);
    }

    public static void postShortMessage(final Context context, final String message) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                shortMessage(context, message);
            }
        });
    }

    public static void postShortMessage(final Context context, final int messageResId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                shortMessage(context, messageResId);
            }
        });
    }

    public static void longMessage(Context context, String message) {
        Data data = new Data();
        data.context = context.getApplicationContext();
        data.time = SystemClock.uptimeMillis();
        data.message = message;
        data.duration = Toast.LENGTH_LONG;
        request(data);
    }

    public static void longMessage(Context context, int messageResId) {
        Data data = new Data();
        data.context = context.getApplicationContext();
        data.time = SystemClock.uptimeMillis();
        data.messageResId = messageResId;
        data.duration = Toast.LENGTH_LONG;
        request(data);
    }

    public static void postLongMessage(final Context context, final String message) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                longMessage(context, message);
            }
        });
    }

    public static void postLongMessage(final Context context, final int messageResId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                longMessage(context, messageResId);
            }
        });
    }

    public static void todo(Context context, String message) {
        Data data = new Data();
        data.context = context.getApplicationContext();
        data.time = SystemClock.uptimeMillis();
        data.message = "TODO : " + message;
        data.duration = Toast.LENGTH_SHORT;
        request(data);
        Log.d("todo", message);
    }

    public static void popupToast(View view, CharSequence text) {
        final int[] screenPos = new int[2];
        final Rect displayFrame = new Rect();
        view.getLocationOnScreen(screenPos);
        view.getWindowVisibleDisplayFrame(displayFrame);

        final Context context = view.getContext();
        final int width = view.getWidth();
        final int height = view.getHeight();
        final int midy = screenPos[1] + height / 2;
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        Toast cheatSheet = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        if (midy < displayFrame.height()) {
            // Show along the top; follow action buttons
            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                    screenWidth - screenPos[0] - width / 2, screenPos[1] + height);
        } else {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        }
        cheatSheet.show();

    }

    private static final class Data {
        Context context;

        long time;

        String message;
        @StringRes
        int messageResId = 0;

        int duration;
    }

    private static final class HandleToastQueueRunnable implements Runnable {
        @Override
        public void run() {
            handleToastQueue();
        }
    }

}
