package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraCaptureSession;

/**
 * Specifies the methods required for the {@link CameraCaptureSessionStateCallback} object to inform
 * its results.
 */
public interface CameraCaptureSessionStateInterface {

    /**
     * Defines the capture session for the camera.
     *
     * @param cameraCaptureSession The session to be defined for the camera.
     */
    void setCameraCaptureSession(CameraCaptureSession cameraCaptureSession);

    /**
     * Requests the media recorder to start the image capture.
     */
    void startMediaRecorder();
}
