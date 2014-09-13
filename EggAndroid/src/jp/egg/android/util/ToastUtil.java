package jp.egg.android.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by chikara on 2014/07/17.
 */
public class ToastUtil {

   private static Toast sToast;

    private static Toast toast(Context context){
        if(sToast == null) sToast = Toast.makeText(
                context.getApplicationContext(),
                "",
                Toast.LENGTH_SHORT
                );
        return sToast;
    }

    public static final void shortMessage(Context context, String message){
        Toast toast = toast(context);
        toast.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public static final void longMessage(Context context, String message){
        Toast toast = toast(context);
        toast.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static final void todo(Context context, String message){
        Toast toast = toast(context);
        toast.setText("TODO : " + message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        Log.d("todo", message);
    }


    public static final void popupToast(View view, CharSequence text){
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
                    screenWidth - screenPos[0] - width / 2, height);
        } else {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        }
        cheatSheet.show();

    }

}
