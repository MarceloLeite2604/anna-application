package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class CameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = CameraCaptureSessionStateCallback.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(CameraCaptureSessionStateCallback.class);
    }

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
        Log.d(CameraCaptureSessionStateCallback.class, LOG_TAG, "onConfigureFailed (47): Camera capture configuration failed.");
    }
}
