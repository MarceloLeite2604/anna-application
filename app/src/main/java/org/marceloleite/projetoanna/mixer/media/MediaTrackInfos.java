package org.marceloleite.projetoanna.mixer.media;

import android.media.MediaFormat;

/**
 * Information about a media track.
 */
public class MediaTrackInfos {

    /**
     * The format of the media track.
     */
    private final MediaFormat mediaFormat;

    /**
     * The index of the media track on its {@link android.media.MediaExtractor} object.
     */
    private final int trackIndex;

    /**
     * Constructor.
     *
     * @param trackIndex  The format of the media track.
     * @param mediaFormat The index of the media track on its {@link android.media.MediaExtractor} object.
     */
    public MediaTrackInfos(int trackIndex, MediaFormat mediaFormat) {
        this.mediaFormat = mediaFormat;
        this.trackIndex = trackIndex;
    }

    /**
     * Returns the format of the media track.
     *
     * @return The format of the media track.
     */
    public MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    /**
     * Returns the index of the media track on its {@link android.media.MediaExtractor} object.
     *
     * @return The index of the media track on its {@link android.media.MediaExtractor} object.
     */
    public int getTrackIndex() {
        return trackIndex;
    }
}
