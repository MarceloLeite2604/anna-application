package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class CameraDeviceStateCallback extends CameraDevice.StateCallback {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = CameraDeviceStateCallback.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(CameraDeviceStateCallback.class);
    }

    private VideoRecorder videoRecorder;


    public CameraDeviceStateCallback(VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
    }

    @Override
    public void onOpened(@NonNull CameraDevice cameraDevice) {
        Log.d(CameraDeviceStateCallback.class, LOG_TAG, "onOpened (36): Opened the camera.");
        videoRecorder.setCameraDevice(cameraDevice);


    }

    @Override
    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
        Log.d(CameraDeviceStateCallback.class, LOG_TAG, "onDisconnected (44): Camera disconnected.");
    }

    @Override
    public void onError(@NonNull CameraDevice cameraDevice, int i) {
        Log.d(CameraDeviceStateCallback.class, LOG_TAG, "onError (49): An error occurred with the camera.");
        cameraDevice.close();
        videoRecorder.setCameraDevice(null);
    }
}
