package org.marceloleite.projetoanna.videorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import org.marceloleite.projetoanna.utils.CompareSizesByArea;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.SizeRatio;
import org.marceloleite.projetoanna.utils.chonometer.Chronometer;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraCaptureSessionStateCallback;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraDeviceStateCallback;
import org.marceloleite.projetoanna.videorecorder.listeners.CameraSurfaceTextureListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marcelo Leite on 02/05/2017.
 */

public class VideoRecorder {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = VideoRecorder.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(VideoRecorder.class);
    }


    private static final double[] PREFERRED_ASPECT_RATIOS = {16.0d / 9.0d, 4.0d / 3.0d};

    private static final int[] PREFERRED_WIDTHS = {1280, 640};

    private static final int REQUEST_CAMERA_PERMISSION = 5010;

    /* private static final int ENCODING_BIT_RATE = 10 * 1024 * 1024; */
    private static final int ENCODING_BIT_RATE = 2 * 1024 * 1024;

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;

    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();

    private String selectedCameraId;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private static final int FRAME_RATE = 30;

    // private AppCompatActivity appCompatActivity;
    private VideoRecorderActivityInterface videoRecorderActivityInterface;

    private TextureView textureView;

    private CameraDevice cameraDevice;

    private CameraDevice.StateCallback stateCallback;

    private Size videoSize;

    private Size previewSize;

    private CaptureRequest.Builder captureRequestBuilder;

    private CameraCaptureSession cameraCaptureSession;

    private Handler handler;

    private HandlerThread backgroundThread;

    private TextureView.SurfaceTextureListener textureListener;

    private MediaRecorder mediaRecorder;

    // private Surface surface;

    private File videoFile;

    private boolean recording;

    private Chronometer videoStartDelayChronometer;


    /*public VideoRecorder(AppCompatActivity appCompatActivity, TextureView textureView) {
        this.textureView = textureView;
        this.appCompatActivity = appCompatActivity;
        this.recording = false;
        this.stateCallback = new CameraDeviceStateCallback(this);
        this.textureListener = new CameraSurfaceTextureListener(this);
    }*/

    public VideoRecorder(VideoRecorderActivityInterface videoRecorderActivityInterface, TextureView textureView) {
        this.textureView = textureView;
        this.videoRecorderActivityInterface = videoRecorderActivityInterface;
        this.recording = false;
        this.stateCallback = new CameraDeviceStateCallback(this);
        this.textureListener = new CameraSurfaceTextureListener(this);
    }

    public boolean isRecording() {
        return recording;
    }

    public void setCameraDevice(CameraDevice cameraDevice) {
        this.cameraDevice = cameraDevice;
        if (cameraDevice != null) {
            createCameraPreview();
        }
    }

    public void setCameraCaptureSession(CameraCaptureSession cameraCaptureSession) {
        if (cameraDevice != null) {
            this.cameraCaptureSession = cameraCaptureSession;
            updatePreview();
        }
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void openCamera(Size previewSize) {
        mediaRecorder = new MediaRecorder(); /* TODO: Should be created here? */

        try {
            selectedCameraId = selectCamera();
            selectVideoAndPreviewSize(selectedCameraId, previewSize);
            openSelectedCamera(selectedCameraId);
        } catch (CameraAccessException cameraAccessException) {
            cameraAccessException.printStackTrace();
        }
    }

    private String selectCamera() throws CameraAccessException {
        CameraManager cameraManager = getCameraManager();
        String selectedCameraId = null;
        String analysedCameraId;
        CameraCharacteristics cameraCharacteristics;
        String[] cameraIds = cameraManager.getCameraIdList();
        boolean cameraSelected = false;
        int counter = 0;

        while (!cameraSelected) {
            if (counter >= cameraIds.length) {
                Log.d(VideoRecorder.class, LOG_TAG, "selectCamera (192): Could not find a camera facing backwards.");
                selectedCameraId = cameraIds[0];
                cameraSelected = true;
            } else {
                analysedCameraId = cameraIds[counter];
                cameraCharacteristics = cameraManager.getCameraCharacteristics(analysedCameraId);
                //if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    selectedCameraId = analysedCameraId;
                    cameraSelected = true;
                } else {
                    counter++;
                }
            }
        }
        return selectedCameraId;
    }

    private void selectVideoAndPreviewSize(String selectedCameraId, Size previewSize) throws CameraAccessException {
        CameraManager cameraManager = getCameraManager();
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        this.videoSize = chooseSize(streamConfigurationMap.getOutputSizes(MediaRecorder.class));
        Log.d(VideoRecorder.class, LOG_TAG, "selectVideoAndPreviewSize (215): Video size: " + videoSize);
        this.previewSize = chooseMinorSizeBiggerThan(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), previewSize, SizeRatio.getSizeRatio(videoSize));
        Log.d(VideoRecorder.class, LOG_TAG, "selectVideoAndPreviewSize (217): Preview size: " + previewSize);
    }

    private int getSensorOrientation() {
        CameraManager cameraManager = getCameraManager();
        int sensorOrientation;

        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
            sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException exception) {
            exception.printStackTrace();
            sensorOrientation = SENSOR_ORIENTATION_DEFAULT_DEGREES;
        }
        return sensorOrientation;
    }

    private CameraManager getCameraManager() {
        return (CameraManager) videoRecorderActivityInterface.getAppCompatActivity().getSystemService(Context.CAMERA_SERVICE);
    }

    private Size chooseSize(Size[] size) {
        Size selectedVideoSize = null;
        boolean videoSizeSelected = false;
        int preferredAspectRatioCounter = 0;

        List<Size> preferredVideoSizes;

        while (!videoSizeSelected) {
            if (preferredAspectRatioCounter < PREFERRED_ASPECT_RATIOS.length) {
                preferredVideoSizes = selectSizesWithAspectRatio(size, PREFERRED_ASPECT_RATIOS[preferredAspectRatioCounter]);

                if (preferredVideoSizes.size() > 0) {
                    selectedVideoSize = selectSizeWithWidth(preferredVideoSizes, PREFERRED_WIDTHS);
                    videoSizeSelected = true;
                }
            } else {
                selectedVideoSize = size[0];
                Log.e(VideoRecorder.class, LOG_TAG, "chooseSize (255): Could not find a size with a preferable aspect ratio. Selected " + selectedVideoSize + " instead.");
                videoSizeSelected = true;
            }
        }

        return selectedVideoSize;
    }

    private List<Size> selectSizesWithAspectRatio(Size[] sizes, double aspectRatio) {
        List<Size> sizesSelected = new ArrayList<>();
        double sizeAspectRatio;

        for (Size size : sizes) {
            sizeAspectRatio = SizeRatio.getSizeRatio(size);
            if (sizeAspectRatio == aspectRatio) {
                sizesSelected.add(size);
            }
        }
        return sizesSelected;
    }

    private Size selectSizeWithWidth(List<Size> sizeList, int[] widths) {
        Size sizeSelected = null;
        int widthsCounter = 0;
        boolean videoSizeSelected = false;

        if (sizeList != null && sizeList.size() > 0 && widths != null && widths.length > 0) {

            while (!videoSizeSelected) {
                if (widthsCounter < widths.length) {
                    for (Size size : sizeList) {
                        if (size.getWidth() == widths[widthsCounter]) {
                            sizeSelected = size;
                            videoSizeSelected = true;
                        }
                    }
                } else {
                    if (sizeSelected == null) {
                        sizeSelected = sizeList.get(0);
                        Log.e(VideoRecorder.class, LOG_TAG, "selectSizeWithWidth (294): Could not find a size with the desired width. Selecting " + sizeSelected + ".'");
                        videoSizeSelected = true;
                    }
                }
                widthsCounter++;
            }
        }
        return sizeSelected;
    }

    private void openSelectedCamera(String selectedCameraId) throws CameraAccessException {
        CameraManager cameraManager = getCameraManager();
        AppCompatActivity appCompatActivity = videoRecorderActivityInterface.getAppCompatActivity();
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        cameraManager.openCamera(selectedCameraId, stateCallback, null);
    }

    protected void updatePreview() {
        if (cameraDevice == null) {
            Log.e(VideoRecorder.class, LOG_TAG, "updatePreview (316): The device does not have a camera.");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(Size size) {
        Log.d(VideoRecorder.class, LOG_TAG, "resume (345): ");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera(size);
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    public void pause() {
        Log.d(VideoRecorder.class, LOG_TAG, "pause (355): ");
        closeCamera();
        stopBackgroundThread();
    }

    private void closePreviewSession() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private static Size chooseMinorSizeBiggerThan(Size[] sizes, Size minimumSize, double aspectRatio) {
        Size selectedSize;
        List<Size> selectedSizes = new ArrayList<>();
        double sizeAspectRatio;

        for (Size size : sizes) {
            sizeAspectRatio = SizeRatio.getSizeRatio(size);
            if (sizeAspectRatio == aspectRatio && size.getWidth() >= minimumSize.getWidth() && size.getHeight() >= minimumSize.getHeight()) {
                selectedSizes.add(size);
            }
        }

        if (selectedSizes.size() > 0) {
            selectedSize = Collections.min(selectedSizes, new CompareSizesByArea());
        } else {
            selectedSize = sizes[0];
            Log.e(VideoRecorder.class, LOG_TAG, "chooseMinorSizeBiggerThan (394): Couldn't find any size minor than " + minimumSize + ". Selected " + selectedSize + " instead.");
        }

        return selectedSize;
    }

    public void startRecord() {

        videoStartDelayChronometer = new Chronometer();
        videoStartDelayChronometer.start();

        Log.d(VideoRecorder.class, LOG_TAG, "startRecord (405): Start record.");

        if (cameraDevice == null) {
            Log.d(VideoRecorder.class, LOG_TAG, "startRecord (408): Camera device is null.");
        }

        if (!textureView.isAvailable()) {
            Log.d(VideoRecorder.class, LOG_TAG, "startRecord (412): Texture view is not available.");
        }

        if (previewSize == null) {
            Log.d(VideoRecorder.class, LOG_TAG, "startRecord (416): Preview size is not defined.");
        }

        try {
            closePreviewSession();
            setUpMediaRecorder();
            Surface previewSurface = createSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();
            Surface mediaRecorderSurface = mediaRecorder.getSurface();
            surfaces.add(previewSurface);
            surfaces.add(mediaRecorderSurface);
            captureRequestBuilder.addTarget(previewSurface);
            captureRequestBuilder.addTarget(mediaRecorderSurface);

            CameraCaptureSessionStateCallback cameraCaptureSessionStateCallback = new CameraCaptureSessionStateCallback(this, true);
            cameraDevice.createCaptureSession(surfaces, cameraCaptureSessionStateCallback, handler);
        } catch (CameraAccessException | IOException exception) {
            Log.d(VideoRecorder.class, LOG_TAG, "startRecord (435): " + exception.getMessage());
            exception.printStackTrace();
        }

        Log.d(VideoRecorder.class, LOG_TAG, "startRecord (439): Finished starting record.");
    }

    public void startMediaRecorder() {
        Log.d(VideoRecorder.class, LOG_TAG, "startMediaRecorder (443): ");
        videoRecorderActivityInterface.getAppCompatActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoStartDelayChronometer.stop();
                recording = true;
                mediaRecorder.start();
                videoRecorderActivityInterface.startVideoRecordingResult(GenericReturnCodes.SUCCESS);
            }
        });
    }

    public void stopRecord() {
        Log.d(VideoRecorder.class, LOG_TAG, "stopRecord (456): ");
        recording = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        createCameraPreview();
        videoRecorderActivityInterface.stopVideoRecordingResult(GenericReturnCodes.SUCCESS);
    }

    protected void createCameraPreview() {
        try {
            Surface surface = createSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            CameraCaptureSessionStateCallback cameraCaptureSessionStateCallback = new CameraCaptureSessionStateCallback(this, false);
            cameraDevice.createCaptureSession(Arrays.asList(surface), cameraCaptureSessionStateCallback, null);
        } catch (CameraAccessException cameraAccessException) {
            cameraAccessException.printStackTrace();
        }
    }

    private Surface createSurface() {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(surfaceTexture);
        return surface;
    }


    private void setUpMediaRecorder() throws IOException {

        videoFile = FileUtils.createFile(FileType.MOVIE_FILE);
        int orientationHint = setMediaRecorderOrientation();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(ENCODING_BIT_RATE);
        mediaRecorder.setVideoFrameRate(FRAME_RATE);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOrientationHint(orientationHint);
        mediaRecorder.prepare();
    }

    private int setMediaRecorderOrientation() {
        int orientationHint = 0;

        int rotation = videoRecorderActivityInterface.getAppCompatActivity().getWindowManager().getDefaultDisplay().getRotation();
        int sensorOrientation = getSensorOrientation();
        switch (sensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                orientationHint = DEFAULT_ORIENTATIONS.get(rotation);
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                orientationHint = INVERSE_ORIENTATIONS.get(rotation);
                break;
        }
        return orientationHint;
    }

    public long getStartRecordingDelay() {
        return videoStartDelayChronometer.getDifference() / 1000L;
    }
}
