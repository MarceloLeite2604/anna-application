package org.marceloleite.projetoanna.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Marcelo Leite on 02/05/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera camera;

    private Camera.Size previewSize;

    private List<Camera.Size> cameraSizesList;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        cameraSizesList = this.camera.getParameters().getSupportedPreviewSizes();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceCreated, 40: ");
        //cameraSizesList = camera.getParameters().getSupportedPreviewSizes();
        //Camera.Parameters parameters = camera.getParameters();
        //parameters.setPreviewSize(previewSize.width, previewSize.height);
        //camera.setParameters(parameters);

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the CameraController preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        camera.setParameters(parameters);

        // start preview with new settings
        try {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();

        } catch (Exception e) {
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Log.d(LOG_TAG, "onMeasure, 90: Width:" + width + ", height: " + height);
        setMeasuredDimension(width, height);

        if (cameraSizesList != null) {
            previewSize = getOptimalPreviewSize(cameraSizesList, width, height);
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {

        double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Log.d(LOG_TAG, "getOptimalPreviewSize, 136: Optimal size: " + optimalSize.width + "x" + optimalSize.height + ".");
        return optimalSize;
    }
}
