package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraDevice;

/**
 * Created by marcelo on 27/06/17.
 */

public interface CameraDeviceStateInterface {

    /**
     * Defines the camera device to be used to record videos.
     *
     * @param cameraDevice The camera device to be used to record videos.
     */
    void setCameraDevice(CameraDevice cameraDevice);
}
