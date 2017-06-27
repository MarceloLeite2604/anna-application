package org.marceloleite.projetoanna.mixer.media;

import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;

import org.marceloleite.projetoanna.utils.Log;

import java.io.File;
import java.io.IOException;

/**
 * A wrapper for a {@link MediaMuxer} object,
 */
public class MediaMuxerWrapper {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaMuxerWrapper.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The {@link MediaMuxer} object wrapped.
     */
    private MediaMuxer mediaMuxer;

    private int audioTrackIndex;

    private int videoTrackIndex;

    public MediaMuxerWrapper(File outputFile) {
        try {
            this.mediaMuxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioException) {
            throw new RuntimeException("Exception thrown while creating the media muxer for the mp4 file.", ioException);
        }
    }

    public int getAudioTrackIndex() {
        return audioTrackIndex;
    }

    public int getVideoTrackIndex() {
        return videoTrackIndex;
    }

    public MediaMuxer getMediaMuxer() {
        return mediaMuxer;
    }

    public void addMediaMuxerAudioTrack(MediaFormat audioMediaFormat) {
        this.audioTrackIndex = mediaMuxer.addTrack(audioMediaFormat);
    }

    /**
     * Adds a video track on the wrapped {@link MediaMuxer} with the same characteristics of the
     * track select on {@link android.media.MediaExtractor} wrapped by the
     * {@link MediaExtractorWrapper} informed.
     *
     * @param videoFileMediaExtractorWrapper The wrapper of the {@link android.media.MediaExtractor}
     *                                       with the selected video track whose information will be
     *                                       used to create a new track on the {@link MediaMuxer}
     *                                       wrapped by this object.
     */
    public void addMediaMuxerVideoTrack(MediaExtractorWrapper videoFileMediaExtractorWrapper) {
        File videoFile = videoFileMediaExtractorWrapper.getMediaFile();
        int videoRotation = retrieveVideoRotation(videoFile);
        this.videoTrackIndex = mediaMuxer.addTrack(videoFileMediaExtractorWrapper.getSelectedMediaTrackInfos().getMediaFormat());
        mediaMuxer.setOrientationHint(videoRotation);
    }

    /**
     * Stops the {@link MediaMuxer} object wrapped.
     */
    public void stopMediaMuxer() {
        mediaMuxer.stop();
        mediaMuxer.release();
    }

    /**
     * Retrieves the rotation of a video file.
     *
     * @param videoFile The video file which the rotation will be retrieved.
     * @return The video file rotation.
     */
    private static int retrieveVideoRotation(File videoFile) {
        int rotationDegrees = 0;

        MediaMetadataRetriever retrieverSrc = new MediaMetadataRetriever();
        retrieverSrc.setDataSource(videoFile.getAbsolutePath());
        String degreesString = retrieverSrc.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (degreesString != null) {
            rotationDegrees = Integer.parseInt(degreesString);
        } else {
            Log.e(LOG_TAG, "retrieveVideoRotation (79): Could not obtain video rotation.");
        }

        return rotationDegrees;
    }

}
