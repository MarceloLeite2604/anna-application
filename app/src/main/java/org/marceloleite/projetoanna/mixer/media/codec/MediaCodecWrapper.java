package org.marceloleite.projetoanna.mixer.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import org.marceloleite.projetoanna.mixer.media.MediaExtractorWrapper;
import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaCodecCallback;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaDecoderCallback;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaEncoderCallback;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.IOException;

/**
 * A wrapper for a {@link MediaCodec} object and the {@link MediaCodecCallback} object created for
 * it. This {@link MediaCodec} will encode or decode according to the constructor used and the
 * {@link MediaFormat} informed.
 */
public class MediaCodecWrapper {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaCodecWrapper.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The {@link MediaCodec} object wrapped by the object.
     */
    private MediaCodec mediaCodec;

    /**
     * The {@link MediaCodecCallback} object to be associated with the {@link MediaCodec} object wrapped in this class.
     */
    private MediaCodecCallback callback;

    /**
     * Constructor which creates a media decoder wrapped in this object. This decoder will generate
     * an raw audio file with the content read from the {@link MediaExtractor} wrapped on
     * {@link MediaExtractorWrapper} object and write it on the output file specified.
     *
     * @param mediaFormat           The format of the audio to be decoded.
     * @param mediaExtractorWrapper The wrapper which contains the {@link MediaExtractor} object which will be read and decoded.
     * @param outputFile            The output file where the decoded data will be stored.
     * @param audioTimeIgnored      Informs how much audio time must be ignored before consider start writing the raw audio on file.
     * @param audioDuration         The duration of the audio to be written on file.
     */
    public MediaCodecWrapper(MediaFormat mediaFormat, MediaExtractorWrapper mediaExtractorWrapper, File outputFile, long audioTimeIgnored, long audioDuration) {
        createMediaDecoder(mediaFormat);
        createMediaDecoderCallback(mediaExtractorWrapper.getMediaExtractor(), outputFile, audioTimeIgnored, audioDuration);
        configureMediaCodec(mediaFormat, 0);
    }

    /**
     * Constructor which creates a media encoder wrapped in this object. This encoder will read a
     * raw audio file, convert it on the format specified and write it on the
     * {@link android.media.MediaMuxer} wrapped on the {@link MediaMuxerWrapper} specified.
     *
     * @param mediaFormat       The format which the raw audio must be codified.
     * @param inputFile         The raw audio file to be read and encoded.
     * @param mediaMuxerWrapper The wrapped which contains the {@link android.media.MediaMuxer} object where the encoded audio must be written on.
     */
    public MediaCodecWrapper(MediaFormat mediaFormat, File inputFile, MediaMuxerWrapper mediaMuxerWrapper) {
        createMediaEncoder(mediaFormat);
        createMediaEncoderCallback(inputFile, mediaMuxerWrapper);
        configureMediaCodec(mediaFormat, MediaCodec.CONFIGURE_FLAG_ENCODE);

    }

    /**
     * Starts the codec wrapped in this object and awaits its conclusion.
     */
    public void startAndWaitCodec() {
        mediaCodec.start();

        while (!callback.finished()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mediaCodec.stop();
    }

    /**
     * Creates a decoder for the media format specified.
     *
     * @param mediaFormat The media format which must be decoded.
     */
    private void createMediaDecoder(MediaFormat mediaFormat) {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        try {
            mediaCodec = MediaCodec.createByCodecName(mediaCodecList.findDecoderForFormat(mediaFormat));
        } catch (IOException ioException) {
            throw new RuntimeException("Exception while finding a codec for the media format \"" + mediaFormat + "\".", ioException);
        }

    }

    /**
     * Creates an encoder for the media format specified.
     *
     * @param mediaFormat The format which the media must be encoded to.
     */
    private void createMediaEncoder(MediaFormat mediaFormat) {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        MediaFormat mediaFormatForEncoder;

        /*
         For some reason, when the AAC media format informed is configured, the
        "findEncoderForFormat" function does not find a suitable encoder. To fix this, it is created
        a new AAC format encoder with minimal configuration. This results the function to find a
        suitable codec.
        */
        if (mediaFormat.getString(MediaFormat.KEY_MIME).equals(MediaFormat.MIMETYPE_AUDIO_AAC)) {
            mediaFormatForEncoder = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AudioUtils.SAMPLE_RATE, AudioUtils.CHANNELS);
        } else {
            mediaFormatForEncoder = mediaFormat;
        }

        try {
            mediaCodec = MediaCodec.createByCodecName(mediaCodecList.findEncoderForFormat(mediaFormatForEncoder));
        } catch (IOException ioException) {
            throw new RuntimeException("Exception while creating a codec by name. Media format specified: " + mediaFormat + ".", ioException);
        }

    }

    /**
     * Creates the callback for the media decoder.
     *
     * @param mediaExtractor  The {@link MediaExtractor} object which contains the audio to be extracted.
     * @param outputFile      The file where the raw audio will be written.
     * @param audioTimeIgnore The amount of audio time which will be ignored before start its decoding.
     * @param audioDuration   The duration of the audio to be decoded.
     */
    private void createMediaDecoderCallback(MediaExtractor mediaExtractor, File outputFile, long audioTimeIgnore, long audioDuration) {
        callback = new MediaDecoderCallback(mediaExtractor, outputFile, audioTimeIgnore, audioDuration);
        mediaCodec.setCallback(callback);
    }

    /**
     * Creates the callback for the media encoder.
     *
     * @param inputFile         The file which contains the raw audio to be encoded.
     * @param mediaMuxerWrapper The {@link MediaMuxerWrapper } object which contains the {@link android.media.MediaMuxer} object where the encoded audio will be written.
     */
    private void createMediaEncoderCallback(File inputFile, MediaMuxerWrapper mediaMuxerWrapper) {
        callback = new MediaEncoderCallback(inputFile, mediaMuxerWrapper);
        mediaCodec.setCallback(callback);
    }

    /**
     * Configures the media codec.
     *
     * @param mediaFormat The format of the media do be decoded or which the media must be encoded to.
     * @param flags       The flags which informs if the media codec must encode or decode the data.
     */
    private void configureMediaCodec(MediaFormat mediaFormat, int flags) {
        mediaCodec.configure(mediaFormat, null, null, flags);
    }
}
