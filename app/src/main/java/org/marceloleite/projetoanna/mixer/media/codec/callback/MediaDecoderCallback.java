package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 08/05/2017.
 */

public class MediaDecoderCallback extends MediaCodecCallback {

    private static final String LOG_TAG = MediaDecoderCallback.class.getSimpleName();

    private FileOutputStream fileOutputStream;

    private MediaExtractor mediaExtractor;

    private volatile boolean finishedDecoding;

    public MediaDecoderCallback(MediaExtractor mediaExtractor, File outputFile) throws IOException {
        this.mediaExtractor = mediaExtractor;
        this.fileOutputStream = new FileOutputStream(outputFile);
        this.finishedDecoding = false;
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
            Log.d(LOG_TAG, "onInputBufferAvailable, 51: End of mp3 file.");
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

        writeOutputStream(this.fileOutputStream, outputBuffer);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            Log.d(LOG_TAG, "onOutputBufferAvailable, 74: End of output stream.");

            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "onOutputBufferAvailable, 67: Error while closing output file.");
                e.printStackTrace();
            }

            this.finishedDecoding = true;
        }

        mediaCodec.releaseOutputBuffer(outputBufferId, false);
    }

    private void writeOutputStream(FileOutputStream fileOutputStream, ByteBuffer byteBuffer) {

        int bufferSize = byteBuffer.limit();
        byte[] buffer = new byte[bufferSize];

        byteBuffer.get(buffer);

        try {
            fileOutputStream.write(buffer);
        } catch (IOException e) {
            Log.e(LOG_TAG, "writeOutputStream, 88: Error while writing data on output file.");
            e.printStackTrace();
            this.finishedDecoding = true;
        }
    }

    @Override
    public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
        Log.e(LOG_TAG, "onError, 74: An error occurred on media codec" + mediaCodec);
        e.printStackTrace();

    }

    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
        Log.d(LOG_TAG, "onOutputFormatChanged, 82: Output format changed to " + mediaFormat);
    }

    @Override
    public boolean finished() {
        return this.finishedDecoding;
    }
}
