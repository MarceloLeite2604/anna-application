package org.marceloleite.projetoanna.utils;

import android.media.MediaMetadataRetriever;

import java.io.File;

/**
 * Created by marcelo on 24/07/17.
 */

public abstract class MediaUtils {

    /**
     * Retrieves the duration of a media file.
     *
     * @param mediaFile The media file which its duration should be retrieves.
     * @return The duration of the media file in miliseconds.
     */
    public static long retrieveMediaDuration(File mediaFile) {
        int duration = 0;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mediaFile.getAbsolutePath());
        String stringDuration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (stringDuration != null) {
            duration = Integer.parseInt(stringDuration);
        }
        return duration;
    }


}
