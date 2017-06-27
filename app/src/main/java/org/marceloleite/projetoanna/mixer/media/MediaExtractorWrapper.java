package org.marceloleite.projetoanna.mixer.media;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;

import org.marceloleite.projetoanna.utils.Log;

import java.io.File;
import java.io.IOException;

/**
 * A {@link MediaExtractor} wrapper which helps finding tracks by its mimetype and retrieve the
 * media duration.
 */
public class MediaExtractorWrapper {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaExtractorWrapper.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The {@link MediaExtractor} object wrapped.
     */
    private MediaExtractor mediaExtractor;

    /**
     * The media file associated with the {@link MediaExtractor} wrapped.
     */
    private File mediaFile;

    /**
     * The duration of the media stored on file.
     */
    private long mediaDuration;

    /**
     * The information about the selected track on {@link MediaExtractor} object wrapped.
     */
    private MediaTrackInfos selectedMediaTrackInfos;

    public MediaExtractorWrapper(File mediaFile, String mediaMimetype) {
        this.mediaFile = mediaFile;
        this.mediaDuration = retrieveMediaDuration(mediaFile);
        this.mediaExtractor = createMediaExtractor(mediaFile);
        this.selectedMediaTrackInfos = selectMediaTrack(mediaMimetype);
    }

    /**
     * Returns the {@link MediaExtractor} object wrapped.
     *
     * @return The {@link MediaExtractor} object wrapped.
     */
    public MediaExtractor getMediaExtractor() {
        return mediaExtractor;
    }

    /**
     * Returns the information about the selected track on {@link MediaExtractor} object wrapped.
     *
     * @return The information about the selected track on {@link MediaExtractor} object wrapped.
     */
    public MediaTrackInfos getSelectedMediaTrackInfos() {
        return selectedMediaTrackInfos;
    }

    /**
     * Returns the media file associated with the {@link MediaExtractor} wrapped.
     *
     * @return The media file associated with the {@link MediaExtractor} wrapped.
     */
    public File getMediaFile() {
        return mediaFile;
    }

    /**
     * Returns the duration of the media stored on file.
     *
     * @return The duration of the media stored on file.
     */
    public long getMediaDuration() {
        return mediaDuration;
    }

    /**
     * Retrieves the duration of a media file.
     *
     * @param mediaFile The media file which its duration should be retrieves.
     * @return The duration of the media file. TODO: Which time unit is it?
     */
    private long retrieveMediaDuration(File mediaFile) {
        int duration = 0;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mediaFile.getAbsolutePath());
        String stringDuration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (stringDuration != null) {
            duration = Integer.parseInt(stringDuration);
        }
        return duration;
    }

    /**
     * Creates a {@link MediaExtractor} object for the media file informed.
     *
     * @param mediaFile The media file which the {@link MediaExtractor} should be created for.
     * @return The {@link MediaExtractor} object created for the media file informed.
     */
    private MediaExtractor createMediaExtractor(File mediaFile) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(mediaFile.getAbsolutePath());
        } catch (IOException ioException) {
            throw new RuntimeException("Exception thrown while setting the data source for media extractor.");
        }

        return mediaExtractor;
    }

    /**
     * Selects on {@link MediaExtractor} object the track with the mimetype defined.
     *
     * @param mediaMimetype The mimetype of the track to be selected.
     * @return A {@link MediaTrackInfos} object with the information about the track selected.
     */
    private MediaTrackInfos selectMediaTrack(String mediaMimetype) {
        MediaTrackInfos mediaTrackInfos = findMediaTrack(this.mediaExtractor, mediaMimetype);
        this.mediaExtractor.selectTrack(mediaTrackInfos.getTrackIndex());
        return mediaTrackInfos;
    }

    /**
     * Finds a track on the {@link MediaExtractor} object with the mimetype specified.
     *
     * @param mediaExtractor The {@link MediaExtractor} object where the track must be searched.
     * @param mimetype       The mimetype of the track to be searched.
     * @return A {@link MediaTrackInfos} object with the information about the track found.
     */
    private static MediaTrackInfos findMediaTrack(MediaExtractor mediaExtractor, String mimetype) {
        MediaTrackInfos mediaTrackInfos = null;

        int totalTracks = mediaExtractor.getTrackCount();

        Log.d(LOG_TAG, "findMediaTrack (94): Total of tracks: " + totalTracks);
        for (int trackCounter = 0; trackCounter < totalTracks; trackCounter++) {
            MediaFormat trackMediaFormat = mediaExtractor.getTrackFormat(trackCounter);
            String mime = trackMediaFormat.getString(MediaFormat.KEY_MIME);
            Log.d(LOG_TAG, "findMediaTrack (98): Mimetype: " + mime);
            if (mimetype.equals(mime)) {
                mediaTrackInfos = new MediaTrackInfos(trackCounter, trackMediaFormat);
                break;
            }
        }

        if (mediaTrackInfos == null) {
            Log.e(LOG_TAG, "findMediaTrack (106): Could not find a track with mimetype \"" + mimetype + "\".");
        }

        return mediaTrackInfos;
    }
}
