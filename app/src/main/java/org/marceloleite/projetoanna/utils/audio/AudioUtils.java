package org.marceloleite.projetoanna.utils.audio;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Stores the constants utilized to inform the audio encoding and decoding quality and some generic methods related to audio.
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

    /**
     * The offset utilized on audio buffers.
     */
    public static final int BUFFER_OFFSET = 0;

    /**
     * Sample rate of the audio received.
     */
    public static final int SAMPLE_RATE = 44100;

    /**
     * Number of channels on audio received.
     */
    public static final int CHANNELS = 2;

    /**
     * Size of the sample format (in bytes).
     * The original sample format characteristics is 16 bits, signed and little endian.
     */
    private static final int SAMPLE_FORMAT = 2;

    /**
     * The bit rate used to encode audio.
     */
    public static final int AAC_ENCODING_BIT_RATE = 128000;

    /**
     * Calculates the audio duration of an raw audio sample with the size specified (in microseconds).
     *
     * @param rawAudioSampleSize Size of the raw audio sample to calculate the duration
     * @return The audio duration of an raw audio with the specified size.
     */
    public static long calculateAudioTime(long rawAudioSampleSize) {
        Log.d(AudioUtils.class, LOG_TAG, "calculateAudioTime (55): ");
        return (long) (1000000f * ((float) rawAudioSampleSize / (float) (CHANNELS * SAMPLE_RATE * SAMPLE_FORMAT)));
    }

    /**
     * Calculates the size of an raw audio sample with the specified duration.
     *
     * @param audioDuration The duration of the audio (in microseconds).
     * @return The size of the audio sample which represents the audio duration.
     */
    public static long calculateSizeOfAudioSample(long audioDuration) {
        Log.d(AudioUtils.class, LOG_TAG, "calculateSizeOfAudioSample (65): ");
        long samplesOnTime = Math.round((float) SAMPLE_RATE * (float) audioDuration / 1000000f);
        return samplesOnTime * CHANNELS * SAMPLE_FORMAT;
    }


}
