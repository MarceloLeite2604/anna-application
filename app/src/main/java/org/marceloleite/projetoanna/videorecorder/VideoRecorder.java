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

import org.marceloleite.projetoanna.utils.CompareSizesByArea;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.chonometer.Chronometer;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraCaptureSessionStateCallback;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraCaptureSessionStateInterface;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraDeviceStateCallback;
import org.marceloleite.projetoanna.videorecorder.callbacks.CameraDeviceStateInterface;
import org.marceloleite.projetoanna.videorecorder.listeners.CameraSurfaceTextureListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controls the video recording.
 */
public class VideoRecorder implements CameraCaptureSessionStateInterface, CameraDeviceStateInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = VideoRecorder.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The preferred aspect ratios to record videos.
     */
    private static final double[] PREFERRED_ASPECT_RATIOS = {16.0d / 9.0d, 4.0d / 3.0d};

    /**
     * The preferred width to record videos.
     */
    private static final int[] PREFERRED_WIDTHS = {1280, 640};

    /**
     * Code used on intent to request access to the video camera.
     */
    private static final int REQUEST_CAMERA_PERMISSION = 5010;

    /**
     * The bit rate used to encode the video.
     */
    private static final int ENCODING_BIT_RATE = 2 * 1024 * 1024;

    /**
     * The frame rate used to record the videos.
     */
    private static final int FRAME_RATE = 30;

    /**
     * Value returned by teh sensor orientation when the default position is being used.
     */
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;

    /**
     * Value returned by teh sensor orientation when the inverted position is being used.
     */
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    /**
     * Array of values to remap the default orientation informed by the surface.
     */
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();

    /**
     * The ID of the camera selected to record videos.
     */
    private String selectedCameraId;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Array of values to remap the inverted orientation informed by the surface.
     */
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    /**
     * The object which contains the parameters to construct this object and the methods to be
     * executed once the image capture has started or stopped.
     */
    private VideoRecorderInterface videoRecorderInterface;

    /**
     * The parameters informed to construct this object.
     */
    private VideoRecorderParameters videoRecorderParameters;

    /**
     * The camera device used to record the videos.
     */
    private CameraDevice cameraDevice;

    /**
     * The callback used to control the change of the camera state.
     */
    private CameraDevice.StateCallback stateCallback;

    /**
     * Size used to record the videos.
     */
    private Size videoSize;

    /**
     * Size used to preview the video recorded.
     */
    private Size previewSize;

    /**
     * The builder of the video capture request.
     */
    private CaptureRequest.Builder captureRequestBuilder;

    /**
     * The configuration of the camera capture session.
     */
    private CameraCaptureSession cameraCaptureSession;

    /**
     * A handler to control the camera capture session.
     */
    private Handler handler;

    /**
     * A thread to control the endless request of image capture from the camera.
     */
    private HandlerThread backgroundThread;

    //private CameraSurfaceTextureListener textureListener;

    /**
     * Controls the recording of the video.
     */
    private MediaRecorder mediaRecorder;

    /**
     * The file which stores the video recorded.
     */
    private File videoFile;

    /**
     * Indicates if it is recording a video.
     */
    private boolean recording;

    /**
     * Calculates the delay between the request to start the video and the starting of the image capture.
     */
    private Chronometer videoStartDelayChronometer;

    /**
     * Constructor.
     *
     * @param videoRecorderInterface The object which contains the parameters to construct this object and the methods to be
     *                               executed once the image capture has started or stopped.
     */
    public VideoRecorder(VideoRecorderInterface videoRecorderInterface) {
        this.videoRecorderInterface = videoRecorderInterface;
        this.videoRecorderParameters = videoRecorderInterface.getVideoRecorderParameters();
        this.recording = false;
        this.stateCallback = new CameraDeviceStateCallback(this);
        //this.textureListener = new CameraSurfaceTextureListener(this);
    }

    /**
     * Indicates if a video is being recorded.
     *
     * @return True if a video os being recorded. False otherwise.
     */
    public boolean isRecording() {
        return recording;
    }

    @Override
    public void setCameraDevice(CameraDevice cameraDevice) {
        this.cameraDevice = cameraDevice;
        if (cameraDevice != null) {
            createCameraPreview();
        }
    }

    @Override
    public void setCameraCaptureSession(CameraCaptureSession cameraCaptureSession) {
        if (cameraDevice != null) {
            this.cameraCaptureSession = cameraCaptureSession;
            updatePreview();
        }
    }

    /**
     * Returns the file which stores the video recorded.
     *
     * @return The file which stores the video recorded.
     */
    public File getVideoFile() {
        return videoFile;
    }

    public void openCamera(Size previewSize) {
        mediaRecorder = new MediaRecorder();

        try {
            selectedCameraId = selectCamera();
            selectVideoAndPreviewSize(selectedCameraId, previewSize);
            openSelectedCamera(selectedCameraId);
        } catch (CameraAccessException cameraAccessException) {
            cameraAccessException.printStackTrace();
        }
    }

    /**
     * Selects the camera to be used for recording.
     *
     * @return The ID of the camera selected.
     */
    private String selectCamera() {
        CameraManager cameraManager = getCameraManager();
        String selectedCameraId = null;
        String analysedCameraId;
        CameraCharacteristics cameraCharacteristics;
        String[] cameraIds;
        try {
            cameraIds = cameraManager.getCameraIdList();
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception thrown while requesting the list of camera IDs.", cameraAccessException);
        }
        boolean cameraSelected = false;
        int counter = 0;

        while (!cameraSelected) {
            if (counter >= cameraIds.length) {
                Log.d(LOG_TAG, "selectCamera (192): Could not find a camera facing backwards.");
                selectedCameraId = cameraIds[0];
                cameraSelected = true;
            } else {
                analysedCameraId = cameraIds[counter];
                try {
                    cameraCharacteristics = cameraManager.getCameraCharacteristics(analysedCameraId);
                } catch (CameraAccessException cameraAccessException) {
                    throw new RuntimeException("Exception thrown while requesting the characteristics of a camera.", cameraAccessException);
                }
                Integer lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null) {
                    //if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        selectedCameraId = analysedCameraId;
                        cameraSelected = true;
                    } else {
                        counter++;
                    }
                } else {
                    throw new RuntimeException("Could not define the position of the camera ID \"" + analysedCameraId + "\".");
                }
            }
        }
        return selectedCameraId;
    }

    /**
     * TODO: Conclude.
     *
     * @param selectedCameraId
     * @param previewSize
     * @throws CameraAccessException
     */
    private void selectVideoAndPreviewSize(String selectedCameraId, Size previewSize) throws CameraAccessException {
        CameraManager cameraManager = getCameraManager();
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap != null) {
            Size[] outputSizes = streamConfigurationMap.getOutputSizes(MediaRecorder.class);
            this.videoSize = chooseSize(outputSizes);
            Log.d(LOG_TAG, "selectVideoAndPreviewSize (215): Video size: " + videoSize);
            this.previewSize = chooseMinorSizeBiggerThan(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), previewSize, getSizeRatio(videoSize));
            Log.d(LOG_TAG, "selectVideoAndPreviewSize (217): Preview size: " + previewSize);
        } else {
            /* TODO: What should be done? */
        }
    }

    private int getSensorOrientation() {
        CameraManager cameraManager = getCameraManager();
        Integer sensorOrientation;

        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
            sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == null) {
                sensorOrientation = SENSOR_ORIENTATION_DEFAULT_DEGREES;
            }
        } catch (CameraAccessException exception) {
            exception.printStackTrace();
            sensorOrientation = SENSOR_ORIENTATION_DEFAULT_DEGREES;
        }
        return sensorOrientation;
    }

    private CameraManager getCameraManager() {
        return (CameraManager) videoRecorderParameters.getAppCompatActivity().getSystemService(Context.CAMERA_SERVICE);
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
                Log.e(LOG_TAG, "chooseSize (255): Could not find a size with a preferable aspect ratio. Selected " + selectedVideoSize + " instead.");
                videoSizeSelected = true;
            }
        }

        return selectedVideoSize;
    }

    private List<Size> selectSizesWithAspectRatio(Size[] sizes, double aspectRatio) {
        List<Size> sizesSelected = new ArrayList<>();
        double sizeAspectRatio;

        for (Size size : sizes) {
            sizeAspectRatio = getSizeRatio(size);
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
                    sizeSelected = sizeList.get(0);
                    Log.e(LOG_TAG, "selectSizeWithWidth (294): Could not find a size with the desired width. Selecting " + sizeSelected + ".'");
                    videoSizeSelected = true;
                }
                widthsCounter++;
            }
        }
        return sizeSelected;
    }

    private void openSelectedCamera(String selectedCameraId) throws CameraAccessException {
        CameraManager cameraManager = getCameraManager();
        AppCompatActivity appCompatActivity = videoRecorderParameters.getAppCompatActivity();
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        cameraManager.openCamera(selectedCameraId, stateCallback, null);
    }

    private void updatePreview() {
        if (cameraDevice == null) {
            Log.e(LOG_TAG, "updatePreview (316): The device does not have a camera.");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
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
        Log.d(LOG_TAG, "resume (345): ");
        startBackgroundThread();
        if (videoRecorderParameters.getTextureView().isAvailable()) {
            openCamera(size);
        } else {
            videoRecorderParameters.getTextureView().setSurfaceTextureListener(new CameraSurfaceTextureListener(this));
        }
    }

    public void pause() {
        Log.d(LOG_TAG, "pause (355): ");
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
            sizeAspectRatio = getSizeRatio(size);
            if (sizeAspectRatio == aspectRatio && size.getWidth() >= minimumSize.getWidth() && size.getHeight() >= minimumSize.getHeight()) {
                selectedSizes.add(size);
            }
        }

        if (selectedSizes.size() > 0) {
            selectedSize = Collections.min(selectedSizes, new CompareSizesByArea());
        } else {
            selectedSize = sizes[0];
            Log.e(LOG_TAG, "chooseMinorSizeBiggerThan (394): Couldn't find any size minor than " + minimumSize + ". Selected " + selectedSize + " instead.");
        }

        return selectedSize;
    }

    public void startRecord() {

        videoStartDelayChronometer = new Chronometer();
        videoStartDelayChronometer.start();

        Log.d(LOG_TAG, "startRecord (405): Start record.");

        if (cameraDevice == null) {
            Log.d(LOG_TAG, "startRecord (408): Camera device is null.");
        }

        if (!videoRecorderParameters.getTextureView().isAvailable()) {
            Log.d(LOG_TAG, "startRecord (412): Texture view is not available.");
        }

        if (previewSize == null) {
            Log.d(LOG_TAG, "startRecord (416): Preview size is not defined.");
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
            Log.d(LOG_TAG, "startRecord (435): " + exception.getMessage());
            exception.printStackTrace();
        }

        Log.d(LOG_TAG, "startRecord (439): Finished starting record.");
    }

    @Override
    public void startMediaRecorder() {
        Log.d(LOG_TAG, "startMediaRecorder (443): ");
        videoRecorderParameters.getAppCompatActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoStartDelayChronometer.stop();
                recording = true;
                mediaRecorder.start();
                videoRecorderInterface.startVideoRecordingResult(GenericReturnCodes.SUCCESS);
            }
        });
    }

    public void stopRecord() {
        Log.d(LOG_TAG, "stopRecord (456): ");
        recording = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        createCameraPreview();
        videoRecorderInterface.stopVideoRecordingResult(GenericReturnCodes.SUCCESS);
    }

    private void createCameraPreview() {
        try {
            Surface surface = createSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            CameraCaptureSessionStateCallback cameraCaptureSessionStateCallback = new CameraCaptureSessionStateCallback(this, false);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), cameraCaptureSessionStateCallback, null);
        } catch (CameraAccessException cameraAccessException) {
            cameraAccessException.printStackTrace();
        }
    }

    private Surface createSurface() {
        SurfaceTexture surfaceTexture = videoRecorderParameters.getTextureView().getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        return new Surface(surfaceTexture);
    }


    private void setUpMediaRecorder() throws IOException {

        videoFile = FileUtils.createFile(videoRecorderParameters.getAppCompatActivity(), FileType.MOVIE_FILE);
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

        int rotation = videoRecorderParameters.getAppCompatActivity().getWindowManager().getDefaultDisplay().getRotation();
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

    /**
     * Calculates the ratio of a square defined by the size informed.
     *
     * @param size The size of the square to calculate it ratio.
     * @return The ratio of the square defined by the size informed.
     */
    private static double getSizeRatio(Size size) {
        return (double) size.getWidth() / (double) size.getHeight();
    }
}
