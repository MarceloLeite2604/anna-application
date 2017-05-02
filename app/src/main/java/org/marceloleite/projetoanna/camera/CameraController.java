package org.marceloleite.projetoanna.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marcelo Leite on 02/05/2017.
 */

public class CameraController {

    private static final String LOG_TAG = CameraController.class.getSimpleName();

    private MediaRecorder mediaRecorder;

    private Camera camera;

    private CameraPreview cameraPreview;

    public CameraController() {
        this.mediaRecorder = null;
        this.camera = getCameraInstance();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCameraPreview(CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
    }

    public void startRecording() {
        prepareMediaRecorder();
        mediaRecorder.start();
        Log.d(LOG_TAG, "startRecording, 34: Video recording started.");
    }

    public void stopRecording() {
        mediaRecorder.stop();
        releaseMediaRecorder();
        camera.lock();
        Log.d(LOG_TAG, "stopRecording, 39: Video recording stopped.");
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }


    private void prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        File outputFile = getOutputMediaFile();
        Log.d(LOG_TAG, "prepareMediaRecorder, 71: Video file output: " + outputFile.getAbsolutePath());

        mediaRecorder.setOutputFile(outputFile.toString());
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "captureVideo, 48: IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            //return false;
        } catch (IOException e) {
            Log.d(LOG_TAG, "captureVideo, 53: IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            //return false;
        }
    }


    public static Camera getCameraInstance() {

        Camera camera;
        camera = Camera.open();
        if (camera != null) {
            setCameraParameters(camera);
        }
        return camera;
    }

    private static void setCameraParameters(Camera camera) {
        int rotationDegrees = 90;
        camera.setDisplayOrientation(rotationDegrees);
    }

    private void captureVideo(Camera camera, CameraPreview cameraPreview) {


    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "VID_" + timeStamp + ".mp4";

        File mediaFile = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mediaFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), filename);
        }
        return mediaFile;
    }
}
