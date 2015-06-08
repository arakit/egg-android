package jp.egg.android.util;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;

import java.util.List;

/**
 * Created by chikara on 2014/09/25.
 */
public class CameraUtil {

    public static final boolean isDifferenceHorizontalVerticalOrientation(int rotate) {
        return (rotate != 0 && rotate != 180);
    }

    public static final int getWidth(int rotate, Camera.Size size) {
        return (!isDifferenceHorizontalVerticalOrientation(rotate)) ? size.width : size.height;
    }

    public static final int getHeight(int rotate, Camera.Size size) {
        return (!isDifferenceHorizontalVerticalOrientation(rotate)) ? size.height : size.width;
    }

    public static final Camera.Size findMaxSize(List<Camera.Size> supportedSizes, int rotate, SizeFilter filter) {
        Camera.Size maxPreviewSize = null;
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < supportedSizes.size(); i++) {
            Camera.Size size = supportedSizes.get(i);
            int width, height;
            width = getWidth(rotate, size);
            height = getHeight(rotate, size);
            if (filter == null || filter.accept(width, height, size, rotate)) {
                if (maxPreviewSize == null || (height >= maxHeight && width >= maxWidth)) {
                    maxPreviewSize = size;
                    maxHeight = height;
                    maxWidth = width;
                }
            }
        }
        return maxPreviewSize;
    }

    public static final Camera.Size findMaxPreviewSize(Camera camera, int rotate, SizeFilter filter) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
        Camera.Size maxPreviewSize = findMaxSize(supportedSizes, rotate, filter);
        return maxPreviewSize;
    }

    public static final Camera.Size findMaxPictureSize(Camera camera, int rotate, SizeFilter filter) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedSizes = parameters.getSupportedPictureSizes();
        Camera.Size maxPreviewSize = findMaxSize(supportedSizes, rotate, filter);
        return maxPreviewSize;
    }

    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {
        return setCameraDisplayOrientation(activity.getWindowManager().getDefaultDisplay(), cameraId, camera);
    }

    public static int setCameraDisplayOrientation(Display display,
                                                  int cameraId, android.hardware.Camera camera) {
        int result = calcCameraDisplayOrientation(display, cameraId, camera);
        camera.setDisplayOrientation(result);
        return result;
    }

    public static int calcCameraDisplayOrientation(Display display,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public interface SizeFilter {
        /**
         * @param width  画面に合わせた向きの幅です。
         * @param height 画面に合わせた向きの縦幅です。
         * @param size   オリジナルSize
         * @return
         */
        public boolean accept(int width, int height, Camera.Size size, int rotate);
    }
}
