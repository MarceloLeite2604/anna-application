package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.util.Log;

import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class CameraDeviceStateCallback extends CameraDevice.StateCallback {

    private static final String LOG_TAG = CameraDeviceStateCallback.class.getSimpleName();

    private VideoRecorder videoRecorder;


    public CameraDeviceStateCallback(VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
    }

    @Override
    public void onOpened(@NonNull CameraDevice cameraDevice) {
        Log.d(LOG_TAG, "onOpened, 18: Opened the camera.");

        videoRecorder.setCameraDevice(cameraDevice);


    }

    @Override
    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
        Log.d(LOG_TAG, "onDisconnected, 27: Camera disconnected.");
    }

    @Override
    public void onError(@NonNull CameraDevice cameraDevice, int i) {
        Log.d(LOG_TAG, "onError, 32: An error occurred with the camera.");
        cameraDevice.close();
        videoRecorder.setCameraDevice(null);
    }
}
