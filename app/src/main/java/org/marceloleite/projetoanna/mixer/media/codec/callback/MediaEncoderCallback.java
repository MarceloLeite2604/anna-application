package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.AudioData;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.ByteBufferWriteMediaMuxerWrapper;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Encodes
 */
public class MediaEncoderCallback extends MediaCodecCallback {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaEncoderCallback.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The wrapper which contains the {@link android.media.MediaMuxer} object where the encoded audio track will be written.
     */
    private MediaMuxerWrapper mediaMuxerWrapper;

    /**
     * Controls the writing of the encoded audio data pieces on the {@link android.media.MediaMuxer} object.
     */
    private ByteBufferWriteMediaMuxerWrapper byteBufferWriteMediaMuxerWrapper;

    /**
     * The input stream which will be used to read the raw audio file.
     */
    private FileInputStream fileInputStream;

    /**
     * Informs is the audio encoding process is finished.
     */
    private volatile boolean finishedEncoding;

    /**
     * Stores the total of raw audio bytes read from audio file.
     */
    private long totalBytesRead;

    /**
     * Constructor.
     *
     * @param inputFile         The file which contains the raw audio data.
     * @param mediaMuxerWrapper The wrapper which contains the {@link android.media.MediaMuxer} object where the encoded audio will be written.
     */
    public MediaEncoderCallback(File inputFile, MediaMuxerWrapper mediaMuxerWrapper) {
        try {
            this.fileInputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException("Exception thrown while creating the input stream to read the file which to be encoded.", fileNotFoundException);
        }
        this.mediaMuxerWrapper = mediaMuxerWrapper;
        this.byteBufferWriteMediaMuxerWrapper = new ByteBufferWriteMediaMuxerWrapper(mediaMuxerWrapper);
        this.finishedEncoding = false;
        this.totalBytesRead = 0;
    }


    // this.lastPresentationTimeUs = 0;
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
            Log.d(LOG_TAG, "onOutputBufferAvailable (76): End of encoding.");
            closeInputFile();
            byteBufferWriteMediaMuxerWrapper.finishWriting();
            this.finishedEncoding = true;
        } else {
            ByteBuffer byteBuffer = MediaCodecCallback.copyByteBuffer(outputBuffer);
            AudioData audioData = new AudioData(byteBuffer, bufferInfo);
            byteBufferWriteMediaMuxerWrapper.add(audioData);
        }

        mediaCodec.releaseOutputBuffer(outputBufferId, false);
    }

    @Override
    public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException codecException) {
        Log.e(LOG_TAG, "onError (99): An error occurred while encoding.");
        codecException.printStackTrace();
        closeInputFile();
        this.finishedEncoding = true;

    }

    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
        mediaMuxerWrapper.addMediaMuxerAudioTrack(mediaFormat);
        mediaMuxerWrapper.getMediaMuxer().start();
    }


    /**
     * Reads content from the input stream associated with the raw audio file.
     *
     * @param fileInputStream The input stream associated with the raw audio file.
     * @param byteBuffer      The {@link ByteBuffer} object where the raw audio data will be stored.
     * @return The amount of bytes read from input stream.
     */
    private int readInputStream(FileInputStream fileInputStream, ByteBuffer byteBuffer) {

        int bufferSize = byteBuffer.limit();
        byte[] buffer = new byte[bufferSize];
        int totalRead;

        try {
            totalRead = fileInputStream.read(buffer, 0, bufferSize);
            if (totalRead != -1) {
                byteBuffer.put(buffer, 0, totalRead);

            } else {
                Log.d(LOG_TAG, "readInputStream (125): End of file.");
                totalRead = 0;
            }
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "readInputStream (150): Exception thrown while reading raw audio file for encoding.");
            closeInputFile();
            throw new RuntimeException("Exception thrown while reading raw audio file for encoding.", ioException);
        }

        totalBytesRead += totalRead;
        return totalRead;
    }

    /**
     * Elaborates the flags associated with a {@link ByteBuffer} object.
     *
     * @param byteBuffer         The {@link ByteBuffer} object which the flags will be elaborated for.
     * @param originalBufferInfo The original {@link MediaCodec.BufferInfo} information about the {@link ByteBuffer} object.
     * @return A {@link MediaCodec.BufferInfo} object with the new flags for the {@link ByteBuffer} informed.
     */
    private MediaCodec.BufferInfo elaborateFlags(ByteBuffer byteBuffer, MediaCodec.BufferInfo originalBufferInfo) {
        if (originalBufferInfo.size > 0) {
            if (originalBufferInfo.size < byteBuffer.capacity()) {
                originalBufferInfo.flags = originalBufferInfo.flags | MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            }
            originalBufferInfo.presentationTimeUs = AudioUtils.calculateAudioTime(totalBytesRead);
        } else {
            originalBufferInfo.size = 0;
            originalBufferInfo.flags = originalBufferInfo.flags | MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            originalBufferInfo.presentationTimeUs = -1;
        }
        return originalBufferInfo;
    }

    @Override
    public boolean finished() {
        return finishedEncoding;
    }

    /**
     * Closes the input file.
     */
    private void closeInputFile() {
        try {
            fileInputStream.close();
        } catch (IOException ioException) {
            Log.d(LOG_TAG, "closeInputFile (165): Error while closing input file.");
            ioException.printStackTrace();
            this.finishedEncoding = true;
        }
    }
}
