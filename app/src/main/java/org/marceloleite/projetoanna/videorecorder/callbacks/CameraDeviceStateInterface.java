package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraDevice;

/**
 * Specifies the methods required for the {@link CameraDeviceStateCallback} to inform its results.
 */
public interface CameraDeviceStateInterface {

    /**
     * Defines the camera device to be used to record videos.
     *
     * @param cameraDevice The camera device to be used to record videos.
     */
    void setCameraDevice(CameraDevice cameraDevice);
}
