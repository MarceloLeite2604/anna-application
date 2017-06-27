package org.marceloleite.projetoanna.videorecorder.callbacks;

import android.hardware.camera2.CameraCaptureSession;

/**
 * Created by marcelo on 27/06/17.
 */

public interface CameraCaptureSessionStateInterface {

    void setCameraCaptureSession(CameraCaptureSession cameraCaptureSession);

    void startMediaRecorder();
}
