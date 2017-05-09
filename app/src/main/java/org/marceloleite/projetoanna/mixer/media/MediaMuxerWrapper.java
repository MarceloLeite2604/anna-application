package org.marceloleite.projetoanna.mixer.media;

import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.util.Log;

import org.marceloleite.projetoanna.mixer.MediaExtractorWrapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public class MediaMuxerWrapper {

    private static final String LOG_TAG = MediaMuxerWrapper.class.getSimpleName();

    private File outputFile;

    private MediaMuxer mediaMuxer;

    private int audioTrackIndex;

    private int videoTrackIndex;

    public MediaMuxerWrapper(File outputFile) throws IOException {
        this.outputFile = outputFile;
        this.mediaMuxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
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

    public void addMediaMuxerVideoTrack(MediaExtractorWrapper videoFileMediaExtractorWrapper) {
        File videoFile = videoFileMediaExtractorWrapper.getMediaFile();
        int videoRotation = retrieveVideoRotation(videoFile);
        this.videoTrackIndex = mediaMuxer.addTrack(videoFileMediaExtractorWrapper.getSelectedMediaTrackInfos().getMediaFormat());
        mediaMuxer.setOrientationHint(videoRotation);
    }

    public void stopMediaMuxer() {
        mediaMuxer.stop();
        mediaMuxer.release();
    }

    private static int retrieveVideoRotation(File videoFile) {
        int rotationDegrees = 0;

        MediaMetadataRetriever retrieverSrc = new MediaMetadataRetriever();
        retrieverSrc.setDataSource(videoFile.getAbsolutePath());
        String degreesString = retrieverSrc.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (degreesString != null) {
            rotationDegrees = Integer.parseInt(degreesString);
        } else {
            Log.e(LOG_TAG, "retrieveVideoRotation, 195: Could not obtain video rotation.");
        }

        return rotationDegrees;
    }

}
