package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraCaptureSession;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link CameraCaptureSession.StateCallback} object to receive information about changes of the
 * camera capture session and start the video recording.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which contains the method to define the camera capture configuration session and
     * to request the start of the media capture.
     */
    private CameraCaptureSessionStateInterface cameraCaptureSessionStateInterface;

    private boolean startMediaRecorder;

    /**
     * Constructor.
     *
     * @param cameraCaptureSessionStateInterface The object which contains the method to define the
     *                                           camera capture configuration session and to request
     *                                           the start of the media capture.
     * @param startMediaRecorder                 Indicates if the recording should be started once the camera is configured.
     */
    public CameraCaptureSessionStateCallback(CameraCaptureSessionStateInterface cameraCaptureSessionStateInterface, boolean startMediaRecorder) {
        this.cameraCaptureSessionStateInterface = cameraCaptureSessionStateInterface;
        this.startMediaRecorder = startMediaRecorder;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
        cameraCaptureSessionStateInterface.setCameraCaptureSession(cameraCaptureSession);
        if (startMediaRecorder) {
            cameraCaptureSessionStateInterface.startMediaRecorder();
        }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
        Log.e(LOG_TAG, "onConfigureFailed (47): Camera capture configuration failed.");
        throw new RuntimeException("Camera capture configuration failed.");
    }
}
