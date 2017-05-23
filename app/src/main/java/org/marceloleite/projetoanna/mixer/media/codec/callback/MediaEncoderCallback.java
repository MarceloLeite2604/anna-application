package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.ByteBufferWriteMediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.AudioData;
import org.marceloleite.projetoanna.utils.ByteBufferUtils;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 08/05/2017.
 */

public class MediaEncoderCallback extends MediaCodecCallback {

    private static final String LOG_TAG = MediaEncoderCallback.class.getSimpleName();

    private MediaMuxerWrapper mediaMuxerWrapper;

    private ByteBufferWriteMediaMuxerWrapper byteBufferWriteMediaMuxerWrapper;

    private FileInputStream fileInputStream;

    private volatile boolean finishedEncoding;

    private long totalBytesRead;

    //private long lastPresentationTimeUs;

    public MediaEncoderCallback(File inputFile, MediaMuxerWrapper mediaMuxerWrapper) throws IOException {
        this.fileInputStream = new FileInputStream(inputFile);
        this.mediaMuxerWrapper = mediaMuxerWrapper;
        this.byteBufferWriteMediaMuxerWrapper = new ByteBufferWriteMediaMuxerWrapper(mediaMuxerWrapper);
        this.finishedEncoding = false;
        this.totalBytesRead = 0;
        // this.lastPresentationTimeUs = 0;
    }


    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int inputBufferId) {
        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        bufferInfo.size = readInputStream(fileInputStream, inputBuffer);
        bufferInfo = elaborateFlags(inputBuffer, bufferInfo);

        mediaCodec.queueInputBuffer(inputBufferId, AudioUtils.BUFFER_OFFSET, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
    }

    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int outputBufferId, @NonNull MediaCodec.BufferInfo bufferInfo) {
        ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);


        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            Log.d(LOG_TAG, "onOutputBufferAvailable, 63: End of encoding.");
            closeInputFile();
            byteBufferWriteMediaMuxerWrapper.concludeWriting();
            this.finishedEncoding = true;
        } else {
            ByteBuffer byteBuffer = ByteBufferUtils.copyByteBuffer(outputBuffer);
            AudioData audioData = new AudioData(byteBuffer, bufferInfo);
            byteBufferWriteMediaMuxerWrapper.add(audioData);
            /*
            if (bufferInfo.presentationTimeUs >= lastPresentationTimeUs) {
                mediaMuxer.writeSampleData(audioTrackIndex, outputBuffer, bufferInfo);
                lastPresentationTimeUs = bufferInfo.presentationTimeUs;
            } else {
                Log.e(LOG_TAG, "onOutputBufferAvailable, 77: Skipped audio presentation time: " + bufferInfo.presentationTimeUs);
            }
            */
        }

        mediaCodec.releaseOutputBuffer(outputBufferId, false);
    }

    @Override
    public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException codecException) {
        Log.e(LOG_TAG, "onError, 74: An error occurred while encoding.");
        codecException.printStackTrace();
        closeInputFile();
        this.finishedEncoding = true;

    }

    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
        mediaMuxerWrapper.addMediaMuxerAudioTrack(mediaFormat);
        mediaMuxerWrapper.getMediaMuxer().start();
    }


    private int readInputStream(FileInputStream fileInputStream, ByteBuffer byteBuffer) {

        int bufferSize = byteBuffer.limit();
        byte[] buffer = new byte[bufferSize];
        int totalRead;

        try {
            totalRead = fileInputStream.read(buffer, 0, bufferSize);
            if (totalRead != -1) {
                byteBuffer.put(buffer, 0, totalRead);

            } else {
                Log.d(LOG_TAG, "readInputStream, 98: End of file.");
                totalRead = 0;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "readInputStream, 129: Error while reading input file.");
            e.printStackTrace();
            closeInputFile();
            finishedEncoding = true;
            totalRead = 0;
        }

        totalBytesRead += totalRead;
        return totalRead;
    }

    private MediaCodec.BufferInfo elaborateFlags(ByteBuffer byteBuffer, MediaCodec.BufferInfo oldBufferInfo) {
        MediaCodec.BufferInfo newBufferInfo = oldBufferInfo;

        if (oldBufferInfo.size > 0) {
            if (oldBufferInfo.size < byteBuffer.capacity()) {
                newBufferInfo.flags = newBufferInfo.flags | MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            }
            newBufferInfo.presentationTimeUs = AudioUtils.calculatePresentationTimeUs(totalBytesRead);
        } else {
            newBufferInfo.size = 0;
            newBufferInfo.flags = newBufferInfo.flags | MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            newBufferInfo.presentationTimeUs = -1;
        }
        return newBufferInfo;
    }

    @Override
    public boolean finished() {
        return finishedEncoding;
    }

    private void closeInputFile() {
        try {
            fileInputStream.close();
        } catch (IOException ioException) {
            Log.d(LOG_TAG, "onError, 79: Error while closing input file.");
            ioException.printStackTrace();
            this.finishedEncoding = true;
        }
    }
}
