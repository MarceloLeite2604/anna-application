package org.marceloleite.projetoanna.mixer.media;

import android.media.MediaFormat;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public class MediaTrackInfos {

    private MediaFormat mediaFormat;
    private int trackIndex;

    public MediaTrackInfos(int trackIndex, MediaFormat mediaFormat) {
        this.mediaFormat = mediaFormat;
        this.trackIndex = trackIndex;
    }

    public MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    public int getTrackIndex() {
        return trackIndex;
    }
}
