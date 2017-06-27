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
import org.marceloleite.projetoanna.videorecorder.listeners.CameraSurfaceTextureInterface;
import org.marceloleite.projetoanna.videorecorder.listeners.CameraSurfaceTextureListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controls the video recording.
 */
public class VideoRecorder implements CameraCaptureSessionStateInterface, CameraDeviceStateInterface, CameraSurfaceTextureInterface {

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
     * The default value for the rotation angle of the camera image.
     */
    private static final int DEFAULT_CAMERA_IMAGE_ROTATION_ANGLE = 90;

    /**
     * The inverted value for the rotation angle of the camera image.
     */
    private static final int INVERTED_CAMERA_IMAGE_ROTATION_ANGLE = 270;

    /**
     * Array of values to map the angle the image should be rotated to be shown in its correct
     * position when the camera is in the default rotation angle.
     */
    private static final SparseIntArray MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION = new SparseIntArray();

    /**
     * The ID of the camera selected to record videos.
     */
    private String selectedCameraId;

    /* Defines the orientation of the recorded media based on screen rotation when the camera is in
    its default position. */
    static {
        MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION.append(Surface.ROTATION_0, 90);
        MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION.append(Surface.ROTATION_90, 0);
        MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION.append(Surface.ROTATION_180, 270);
        MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION.append(Surface.ROTATION_270, 180);
    }

    /**
     * Array of values to map the angle the image should be rotated to be shown in its correct
     * position when the camera is in the inverted rotation angle.
     */
    private static final SparseIntArray MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION = new SparseIntArray();

    /* Defines the orientation of the recorded media based on screen rotation when the camera is in
    its inverted position. */
    static {
        MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION.append(Surface.ROTATION_0, 270);
        MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION.append(Surface.ROTATION_90, 180);
        MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION.append(Surface.ROTATION_180, 90);
        MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION.append(Surface.ROTATION_270, 0);
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
     * A captureSessionHandler to control the camera capture session.
     */
    private Handler captureSessionHandler;

    /**
     * A thread to control the endless request of image capture from the camera.
     */
    private HandlerThread captureSessionHandlerThread;

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
            requestImageCaptureForCamera();
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

    @Override
    public void openCamera(Size surfacePreviewSize) {
        mediaRecorder = new MediaRecorder();

        selectedCameraId = selectCamera();
        definedVideoAndPreviewSize(selectedCameraId, surfacePreviewSize);
        openSelectedCamera(selectedCameraId);
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
     * Defines the size of the video and its preview.
     *
     * @param selectedCameraId The ID of the camera selected.
     * @param previewSize      The size of the view which the video preview is displayed.
     */
    private void definedVideoAndPreviewSize(String selectedCameraId, Size previewSize) {
        CameraManager cameraManager = getCameraManager();
        CameraCharacteristics cameraCharacteristics;

        try {
            cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception thrown while getting the characteristics of the camera ID \"" + selectedCameraId + "\".");
        }

        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap != null) {
            Size[] outputSizes = streamConfigurationMap.getOutputSizes(MediaRecorder.class);
            this.videoSize = chooseSize(outputSizes);
            Log.d(LOG_TAG, "definedVideoAndPreviewSize (215): Video size: " + videoSize);
            this.previewSize = chooseMinorSizeBiggerThan(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), previewSize, getSizeRatio(videoSize));
            Log.d(LOG_TAG, "definedVideoAndPreviewSize (217): Preview size: " + previewSize);
        } else {
            throw new RuntimeException("Could not get the stream configuration map of the camera ID \"" + selectedCameraId + "\".");
        }
    }

    /**
     * Retrieves the angle which the camera image should be rotated to be shown in its correct position.
     *
     * @return The angle which the camera image should be rotated to be shown in its correct position.
     */
    private int retrieveCameraImageRotationAngle() {
        CameraManager cameraManager = getCameraManager();
        Integer cameraImageRotationAngle;

        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(selectedCameraId);
            cameraImageRotationAngle = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (cameraImageRotationAngle == null) {
                cameraImageRotationAngle = DEFAULT_CAMERA_IMAGE_ROTATION_ANGLE;
            }
        } catch (CameraAccessException exception) {
            exception.printStackTrace();
            cameraImageRotationAngle = DEFAULT_CAMERA_IMAGE_ROTATION_ANGLE;
        }
        return cameraImageRotationAngle;
    }

    /**
     * Retrieves the camera manager.
     *
     * @return The camera manager.
     */
    private CameraManager getCameraManager() {
        return (CameraManager) videoRecorderParameters.getAppCompatActivity().getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * Choose a size according with the aspect ratios preferred by the application and the
     *
     * @param size The sizes available to select.
     * @return The size chosen.
     */
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

    /**
     * Selects the sizes which contains the aspect ratio informed.
     *
     * @param sizes       The sizes to be analyzed.
     * @param aspectRatio The aspect ratio searched.
     * @return A list with the sizes with the aspect ratio informed.
     */
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

    /**
     * Selects the sizes which contains the width specified.
     *
     * @param sizeList The list of sizes to be analyzed.
     * @param widths   The width to be searched.
     * @return A list of sizes with the width informed.
     */
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

    /**
     * Opens the camera selected.
     *
     * @param selectedCameraId The ID of the camera selected.
     */
    private void openSelectedCamera(String selectedCameraId) {
        CameraManager cameraManager = getCameraManager();
        AppCompatActivity appCompatActivity = videoRecorderParameters.getAppCompatActivity();
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        try {
            cameraManager.openCamera(selectedCameraId, stateCallback, null);
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception raised when requesting to open the camera ID \"" + selectedCameraId + "\".", cameraAccessException);
        }
    }

    /**
     * Requests the start of the image capture for the selected camera.
     */
    private void requestImageCaptureForCamera() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, captureSessionHandler);
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception thrown while requesting the start of the image capture for the selected camera.", cameraAccessException);
        }
    }

    /**
     * Creates and starts the {@link HandlerThread} associated with the {@link Handler} which controls the video capture session.
     */
    private void startCaptureSessionHandlerThread() {
        captureSessionHandlerThread = new HandlerThread("Video capture session");
        captureSessionHandlerThread.start();
        captureSessionHandler = new Handler(captureSessionHandlerThread.getLooper());
    }

    /**
     * Stops the {@link HandlerThread} associated with the {@link Handler} which controls the video capture session.
     */
    private void stopCaptureSessionHandlerThread() {
        captureSessionHandlerThread.quitSafely();

        try {
            captureSessionHandlerThread.join();
        } catch (InterruptedException interruptedException) {
            throw new RuntimeException("Exception thrown while waiting for the capture session handler thread to stop.", interruptedException);
        }

        captureSessionHandlerThread = null;
        captureSessionHandler = null;
    }

    /**
     * Executed after the application resume its execution.
     *
     * @param previewSize The new size of the view which shows the camera preview.
     */
    public void resume(Size previewSize) {
        Log.d(LOG_TAG, "resume (345): ");
        startCaptureSessionHandlerThread();
        if (videoRecorderParameters.getTextureView().isAvailable()) {
            openCamera(previewSize);
        } else {
            videoRecorderParameters.getTextureView().setSurfaceTextureListener(new CameraSurfaceTextureListener(this));
        }
    }

    /**
     * Executed before the application is paused.
     */
    public void pause() {
        Log.d(LOG_TAG, "pause (355): ");
        closeCamera();
        stopCaptureSessionHandlerThread();
    }

    /**
     * Closes the preview session.
     */
    private void closePreviewSession() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    /**
     * Closes the camera and release the media recorder.
     */
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

    /**
     * From a list os sizes, select the minor size bigger than the specified size and with the aspect ratio specified.
     *
     * @param sizes       List of sizes to be analyzed.
     * @param minimumSize The minimum requirements for a size to be an options. If the size analyzed has a minor width or height than this, it won't be an eligible size.
     * @param aspectRatio The aspect ratio which the size must have to be eligible.
     * @return Once all the eligible sizes were gathered, this method returns the on which has the minor area.
     */
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

    /**
     * Start video recording.
     */
    public void startRecord() {

        videoStartDelayChronometer = new Chronometer();
        videoStartDelayChronometer.start();

        Log.d(LOG_TAG, "startRecord (405): Start record.");

        if (cameraDevice == null) {
            Log.e(LOG_TAG, "startRecord (408): Camera device is null.");
            throw new RuntimeException("Cannot start video recording. No camera device selected.");
        }

        if (!videoRecorderParameters.getTextureView().isAvailable()) {
            Log.e(LOG_TAG, "startRecord (412): Texture view is not available.");
            throw new RuntimeException("Cannot start video recording. There is not view to show the camera preview.");
        }

        if (previewSize == null) {
            Log.d(LOG_TAG, "startRecord (416): Preview size is not defined.");
            throw new RuntimeException("Cannot start video recording. The preview size is not defined..");
        }


        closePreviewSession();
        configureMediaRecorder();
        Surface previewSurface = createSurface();

        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception thrown while creating the capture request for the camera device.", cameraAccessException);
        }
        List<Surface> surfaces = new ArrayList<>();
        Surface mediaRecorderSurface = mediaRecorder.getSurface();
        surfaces.add(previewSurface);
        surfaces.add(mediaRecorderSurface);
        captureRequestBuilder.addTarget(previewSurface);
        captureRequestBuilder.addTarget(mediaRecorderSurface);

        CameraCaptureSessionStateCallback cameraCaptureSessionStateCallback = new CameraCaptureSessionStateCallback(this, true);
        try {
            cameraDevice.createCaptureSession(surfaces, cameraCaptureSessionStateCallback, captureSessionHandler);
        } catch (CameraAccessException cameraAccessException) {
            throw new RuntimeException("Exception thrown while creating the capture session for the camera device.", cameraAccessException);
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

    /**
     * Stop video recording.
     */
    public void stopRecord() {
        Log.d(LOG_TAG, "stopRecord (456): ");
        recording = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        createCameraPreview();
        videoRecorderInterface.stopVideoRecordingResult(GenericReturnCodes.SUCCESS);
    }

    /**
     * Creates the camera preview.
     */
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

    /**
     * Creates the surface based on the surface texture of the view which the camera preview will be shown.
     *
     * @return The surface created.
     */
    private Surface createSurface() {
        SurfaceTexture surfaceTexture = videoRecorderParameters.getTextureView().getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        return new Surface(surfaceTexture);
    }


    /**
     * Configures the media recorder.
     */
    private void configureMediaRecorder() {

        videoFile = FileUtils.createFile(videoRecorderParameters.getAppCompatActivity(), FileType.MOVIE_FILE);
        int orientationHint = getMediaRecorderOrientation();

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
        try {
            mediaRecorder.prepare();
        } catch (IOException ioException) {
            throw new RuntimeException("Exception thrown while preparing the media recorder for video capture.", ioException);
        }
    }

    /**
     * Retrieves the image orientation that should be defined for the media recorded based on the screen rotation and the camera rotation angle.
     *
     * @return The image orientation that should be defined for the media recorded based on the screen rotation and the camera rotation angle.
     */
    private int getMediaRecorderOrientation() {
        int mediaRecorderOrientation = 0;

        /* Retrieves the rotation of the screen. */
        int screenRotation = videoRecorderParameters.getAppCompatActivity().getWindowManager().getDefaultDisplay().getRotation();

        /* Retrieves the rotation angle for the camera image. */
        int cameraImageRotationAngle = retrieveCameraImageRotationAngle();

        switch (cameraImageRotationAngle) {

            /* If the camera is in its default rotation. */
            case DEFAULT_CAMERA_IMAGE_ROTATION_ANGLE:
                /* The media orientation should be acquired from the default position vector. */
                mediaRecorderOrientation = MEDIA_ORIENTATION_FOR_DEFAULT_CAMERA_POSITION.get(screenRotation);
                break;
            /* If the camera is in its inverted rotation. */
            case INVERTED_CAMERA_IMAGE_ROTATION_ANGLE:
                /* The media orientation should be acquired from the inverted position vector. */
                mediaRecorderOrientation = MEDIA_ORIENTATION_FOR_INVERTED_CAMERA_POSITION.get(screenRotation);
                break;
        }
        return mediaRecorderOrientation;
    }

    /**
     * Returns the delay time calculated between the request to start the video capture and the actual image capture (in milliseconds).
     *
     * @return The delay time calculated between the request to start the video capture and the actual image capture (in milliseconds).
     */
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
