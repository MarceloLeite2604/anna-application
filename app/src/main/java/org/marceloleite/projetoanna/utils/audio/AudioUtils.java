package org.marceloleite.projetoanna.utils.audio;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public abstract class AudioUtils {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AudioUtils.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AudioUtils.class);
    }

    public static final int BUFFER_OFFSET = 0;

    public static final int SAMPLE_RATE = 44100;

    public static final int CHANNELS = 2;

    /**
     * Size of the sample format (in bytes).
     * The original sample format characteristics is 16 bits, signed and little endian.
     */
    private static final int SAMPLE_FORMAT = 2;

    public static final int AAC_ENCODING_BIT_RATE = 128000;

    public static final int AAC_ENCODING_MAX_INPUT_SIZE = 16 * 1024;

    public static long calculatePresentationTimeUs(long totalBytes) {
        long presentationTimeUs = (long) (1000000f * ((float) totalBytes / (float) (CHANNELS * SAMPLE_RATE * SAMPLE_FORMAT)));
        return presentationTimeUs;
    }

    public static long calculateBytesOnAudioTime(long timeUs) {
        long samplesOnTime = Math.round((float) SAMPLE_RATE * (float) timeUs / 1000000f);
        long totalBytes = samplesOnTime * CHANNELS * SAMPLE_FORMAT;
        return totalBytes;
    }


}
