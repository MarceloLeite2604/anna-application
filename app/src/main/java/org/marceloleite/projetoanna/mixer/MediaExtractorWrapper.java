package org.marceloleite.projetoanna.mixer;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import org.marceloleite.projetoanna.mixer.media.MediaTrackInfos;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public class MediaExtractorWrapper {

    private static final String LOG_TAG = MediaExtractorWrapper.class.getSimpleName();

    private MediaExtractor mediaExtractor;

    private File mediaFile;

    private long mediaDuration;

    // private String mediaMimetype;

    private MediaTrackInfos selectedMediaTrackInfos;

    public MediaExtractorWrapper(File mediaFile, String mediaMimetype) throws IOException {
        this.mediaFile = mediaFile;
        //this.mediaMimetype = mediaMimetype;
        this.mediaDuration = calculateMediaDuration(mediaFile);
        this.mediaExtractor = createMediaExtractor(mediaFile);
        this.selectedMediaTrackInfos = selectMediaTrack(mediaMimetype);
    }

    public MediaExtractor getMediaExtractor() {
        return mediaExtractor;
    }

    public MediaTrackInfos getSelectedMediaTrackInfos() {
        return selectedMediaTrackInfos;
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public long getMediaDuration() {
        return mediaDuration;
    }

    private long calculateMediaDuration(File mediaFile) {
        int duration = 0;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mediaFile.getAbsolutePath());
        String stringDuration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (stringDuration != null) {
            duration = Integer.parseInt(stringDuration);
        }
        return duration;
    }

    private MediaExtractor createMediaExtractor(File mediaFile) throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(mediaFile.getAbsolutePath());

        return mediaExtractor;
    }

    private MediaTrackInfos selectMediaTrack(String mediaMimetype) throws IOException {
        MediaTrackInfos mediaTrackInfos = findMediaTrack(this.mediaExtractor, mediaMimetype);
        this.mediaExtractor.selectTrack(mediaTrackInfos.getTrackIndex());
        return mediaTrackInfos;
    }

    private static MediaTrackInfos findMediaTrack(MediaExtractor mediaExtractor, String mimetype) throws IOException {
        MediaTrackInfos mediaTrackInfos = null;

        int totalTracks = mediaExtractor.getTrackCount();

        Log.d(LOG_TAG, "findMediaTrack, 67: Total of tracks: " + totalTracks);
        for (int trackCounter = 0; trackCounter < totalTracks; trackCounter++) {
            MediaFormat trackMediaFormat = mediaExtractor.getTrackFormat(trackCounter);
            String mime = trackMediaFormat.getString(MediaFormat.KEY_MIME);
            Log.d(LOG_TAG, "findMediaTrack, 70: Mimetype: " + mime);
            if (mimetype.equals(mime)) {
                mediaTrackInfos = new MediaTrackInfos(trackCounter, trackMediaFormat);
                break;
            }
        }

        if (mediaTrackInfos == null) {
            Log.e(LOG_TAG, "findMediaTrack, 35: Could not find a track with mimetype \"" + mimetype + "\".");
        }

        return mediaTrackInfos;
    }
}
