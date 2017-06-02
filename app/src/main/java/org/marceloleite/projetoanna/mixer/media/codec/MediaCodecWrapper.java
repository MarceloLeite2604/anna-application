package org.marceloleite.projetoanna.mixer.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import org.marceloleite.projetoanna.mixer.MediaExtractorWrapper;
import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaCodecCallback;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaDecoderCallback;
import org.marceloleite.projetoanna.mixer.media.codec.callback.MediaEncoderCallback;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 09/05/2017.
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
        Log.addClassToLog(MediaCodecWrapper.class);
    }

    private MediaCodec mediaCodec;

    private MediaCodecCallback callback;

    public MediaCodecWrapper(MediaFormat mediaFormat, MediaExtractorWrapper mediaExtractorWrapper, File outputFile, long startAudioDelay, long audioDuration) throws IOException {
        createMediaDecoder(mediaFormat);
        createMediaDecoderCallback(mediaExtractorWrapper.getMediaExtractor(), outputFile, startAudioDelay, audioDuration);
        configureMediaCodec(mediaFormat, 0);
    }

    public MediaCodecWrapper(MediaFormat mediaFormat, File inputFile, MediaMuxerWrapper mediaMuxerWrapper) throws IOException {
        createMediaEncoder(mediaFormat);
        createMediaEncoderCallback(inputFile, mediaMuxerWrapper);
        configureMediaCodec(mediaFormat, MediaCodec.CONFIGURE_FLAG_ENCODE);

    }

    public MediaCodecCallback getCallback() {
        return callback;
    }

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

    private void createMediaDecoder(MediaFormat mediaFormat) throws IOException {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        mediaCodec = MediaCodec.createByCodecName(mediaCodecList.findDecoderForFormat(mediaFormat));

    }

    private void createMediaEncoder(MediaFormat mediaFormat) throws IOException {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        MediaFormat mediaFormatForEncoder;

        /*
         For some reason, when the AAC media format informed is configured, the
        "findEncoderForFormat" function does not find a suitable encoder. To fix this, it is created
        a new AAC format encoder with minimal configuration. This results the function to find a
        suitable codec.
        */
        if (mediaFormat.getString(MediaFormat.KEY_MIME) == MediaFormat.MIMETYPE_AUDIO_AAC) {
            mediaFormatForEncoder = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AudioUtils.SAMPLE_RATE, AudioUtils.CHANNELS);
        } else {
            mediaFormatForEncoder = mediaFormat;
        }

        mediaCodec = MediaCodec.createByCodecName(mediaCodecList.findEncoderForFormat(mediaFormatForEncoder));

    }

    private void createMediaDecoderCallback(MediaExtractor mediaExtractor, File outputFile, long startDelay, long audioDuration) throws IOException {
        callback = new MediaDecoderCallback(mediaExtractor, outputFile, startDelay, audioDuration);
        mediaCodec.setCallback(callback);
    }

    private void createMediaEncoderCallback(File inputFile, MediaMuxerWrapper mediaMuxerWrapper) throws IOException {
        callback = new MediaEncoderCallback(inputFile, mediaMuxerWrapper);
        mediaCodec.setCallback(callback);
    }

    private void configureMediaCodec(MediaFormat mediaFormat, int flags) {
        mediaCodec.configure(mediaFormat, null, null, flags);
    }
}
