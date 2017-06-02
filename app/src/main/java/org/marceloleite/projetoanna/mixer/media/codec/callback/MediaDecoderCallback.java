package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.ByteBufferWriteOutputStream;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.AudioData;
import org.marceloleite.projetoanna.utils.ByteBufferUtils;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 08/05/2017.
 */

public class MediaDecoderCallback extends MediaCodecCallback {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaDecoderCallback.class.getSimpleName();


    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(MediaDecoderCallback.class);
    }

    private ByteBufferWriteOutputStream byteBufferWriteOutputStream;

    private MediaExtractor mediaExtractor;

    private volatile boolean finishedDecoding;

    public MediaDecoderCallback(MediaExtractor mediaExtractor, File outputFile, long audioDelay, long audioDuration) throws IOException {
        this.mediaExtractor = mediaExtractor;
        this.finishedDecoding = false;
        Log.d(MediaDecoderCallback.class, LOG_TAG, "MediaDecoderCallback (47): Audio duration: " + audioDuration + ", audio delay: " + audioDelay);

        long bytesToIgnore = AudioUtils.calculateBytesOnAudioTime(audioDelay);
        long bytesToRead = AudioUtils.calculateBytesOnAudioTime(audioDuration);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        this.byteBufferWriteOutputStream = new ByteBufferWriteOutputStream(fileOutputStream, bytesToIgnore, bytesToRead);
    }

    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int inputBufferId) {
        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        bufferInfo.size = mediaExtractor.readSampleData(inputBuffer, AudioUtils.BUFFER_OFFSET);

        if (bufferInfo.size > 0) {
            bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
            bufferInfo.flags = mediaExtractor.getSampleFlags();
            mediaExtractor.advance();
        } else {
            Log.d(MediaDecoderCallback.class, LOG_TAG, "onInputBufferAvailable (66): End of mp3 file.");
            inputBuffer.clear();
            bufferInfo.size = 0;
            bufferInfo.presentationTimeUs = -1;
            bufferInfo.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
        }
        mediaCodec.queueInputBuffer(inputBufferId, AudioUtils.BUFFER_OFFSET, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
    }

    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int outputBufferId, @NonNull MediaCodec.BufferInfo bufferInfo) {
        ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);

        ByteBuffer byteBuffer = ByteBufferUtils.copyByteBuffer(outputBuffer);
        AudioData audioData = new AudioData(byteBuffer, bufferInfo);


        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            Log.d(MediaDecoderCallback.class, LOG_TAG, "onOutputBufferAvailable (84): End of output stream.");

            byteBufferWriteOutputStream.concludeWriting();
            this.finishedDecoding = true;
        } else {
            byteBufferWriteOutputStream.add(audioData);
        }

        mediaCodec.releaseOutputBuffer(outputBufferId, false);
    }

    @Override
    public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
        Log.e(MediaDecoderCallback.class, LOG_TAG, "onError (97): An error occurred on media codec" + mediaCodec);
        e.printStackTrace();

    }

    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
        Log.d(MediaDecoderCallback.class, LOG_TAG, "onOutputFormatChanged (104): Output format changed to " + mediaFormat);
    }

    @Override
    public boolean finished() {
        return this.finishedDecoding;
    }
}
