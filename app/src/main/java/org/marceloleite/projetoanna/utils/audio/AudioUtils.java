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
     * Bit rate used to encode audio on AAC format.
     */
    public static final int AAC_ENCODING_BIT_RATE = 128000;

    /**
     * Maximum input size of an audio sample to encode on AAC format.
     */
    public static final int AAC_ENCODING_MAX_INPUT_SIZE = 16 * 1024;

    /**
     * Calculates the duration of an audio based on its number of bytes.
     *
     * @param totalBytes The number of bytes that compose the audio.
     * @return The audio time duration (in microseconds).
     */
    public static long calculateAudioDuration(long totalBytes) {
        Log.d(AudioUtils.class, LOG_TAG, "calculateAudioDuration (60): ");
        return (long) (1000000f * ((float) totalBytes / (float) (CHANNELS * SAMPLE_RATE * SAMPLE_FORMAT)));
    }

    /**
     * Calculates the quantity of bytes required to compose an audio with the duration specified.
     *
     * @param audioDuration The duration of the audio requested
     * @return The quantity of bytes required to compose an audio with the duration specified.
     */
    public static long calculateBytesOnAudioDuration(long audioDuration) {
        long samplesOnTime = Math.round((float) SAMPLE_RATE * (float) audioDuration / 1000000f);
        return samplesOnTime * CHANNELS * SAMPLE_FORMAT;
    }


}
