package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.NonNull;
import android.util.Log;

import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class CameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

    private static final String LOG_TAG = CameraCaptureSessionStateCallback.class.getSimpleName();

    private VideoRecorder videoRecorder;

    private boolean startMediaRecorder;

    public CameraCaptureSessionStateCallback(VideoRecorder videoRecorder, boolean startMediaRecorder) {
        this.videoRecorder = videoRecorder;
        this.startMediaRecorder = startMediaRecorder;

    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
        videoRecorder.setCameraCaptureSession(cameraCaptureSession);
        if (startMediaRecorder) {
            videoRecorder.startMediaRecorder();
        }
    }

    @Override
    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
        Log.d(LOG_TAG, "onConfigureFailed, 31: Camera capture configuration failed.");
    }
}
