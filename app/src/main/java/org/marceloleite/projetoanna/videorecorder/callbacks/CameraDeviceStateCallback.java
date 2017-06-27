package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link CameraDevice.StateCallback} object to receive updates about the state of a camera device
 * and define which camera should be used to record videos on application.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which defines the camera to be used to record videos for the application.
     */
    private CameraDeviceStateInterface cameraDeviceStateInterface;


    /**
     * Constructor.
     *
     * @param cameraDeviceStateInterface The object which defines the camera to be used to record videos for the application.
     */
    public CameraDeviceStateCallback(CameraDeviceStateInterface cameraDeviceStateInterface) {
        this.cameraDeviceStateInterface = cameraDeviceStateInterface;
    }

    @Override
    public void onOpened(@NonNull CameraDevice cameraDevice) {
        Log.d(LOG_TAG, "onOpened (36): Opened the camera.");
        cameraDeviceStateInterface.setCameraDevice(cameraDevice);


    }

    @Override
    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
        Log.d(LOG_TAG, "onDisconnected (44): Camera disconnected.");
        /* TODO: Test if this is correct. */
        cameraDeviceStateInterface.setCameraDevice(null);
    }

    @Override
    public void onError(@NonNull CameraDevice cameraDevice, int i) {
        Log.d(LOG_TAG, "onError (49): An error occurred with the camera.");
        cameraDevice.close();
        cameraDeviceStateInterface.setCameraDevice(null);
    }
}
