package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.AudioData;
import org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter.ByteBufferWriteOutputStream;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.MediaUtils;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReport;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * The {@link MediaCodecCallback} used to decode audio files to its raw format.
 */
public class MediaDecoderCallback extends MediaCodecCallback {

    private static final String DEFAULT_PROGRESS_REPORT_MESSAGE = "Decoding";

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MediaDecoderCallback.class.getSimpleName();


    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The {@link ByteBufferWriteOutputStream} to write the audio data pieces on the output stream.
     */
    private ByteBufferWriteOutputStream byteBufferWriteOutputStream;

    /**
     * The {@link MediaExtractor} object which contains the encoded audio.
     */
    private MediaExtractor mediaExtractor;

    /**
     * Indicates if the decoding is concluded.
     */
    private volatile boolean finishedDecoding;

    private ProgressReporter progressReporter;

    private long audioDuration;

    /**
     * Constructor.
     *
     * @param mediaExtractor   The {@link MediaExtractor} object which contains the encoded audio.
     * @param outputFile       The file which will store the raw audio content.
     * @param audioTimeIgnored The amount of audio time which will be ignored before writing the raw audio on output file.
     * @param audioDuration    The duration of the audio file.
     */
    public MediaDecoderCallback(MediaExtractor mediaExtractor, File outputFile, long audioTimeIgnored, long audioDuration, ProgressReporter progressReporter) {
        this.mediaExtractor = mediaExtractor;
        this.finishedDecoding = false;
        Log.d(LOG_TAG, "MediaDecoderCallback (47): Audio duration: " + audioDuration + ", audio delay: " + audioTimeIgnored);

        long bytesToIgnore = AudioUtils.calculateSizeOfAudioSample(audioTimeIgnored);
        long bytesToRead = AudioUtils.calculateSizeOfAudioSample(audioDuration);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException("Exception thrown while creating the file to store the decoded audio.", fileNotFoundException);
        }
        this.progressReporter = progressReporter;

        this.audioDuration = audioDuration;
        this.byteBufferWriteOutputStream = new ByteBufferWriteOutputStream(fileOutputStream, bytesToIgnore, bytesToRead);
    }

    @Override
    public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int inputBufferId) {
        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int percentageConcluded;

        if (inputBuffer != null) {
            bufferInfo.size = mediaExtractor.readSampleData(inputBuffer, AudioUtils.BUFFER_OFFSET);

            if (bufferInfo.size > 0) {
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                percentageConcluded = (int) (((double) bufferInfo.presentationTimeUs / (double) audioDuration) * 100);
                //noinspection WrongConstant
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                mediaExtractor.advance();
            } else {
                Log.d(LOG_TAG, "onInputBufferAvailable (66): End of mp3 file.");
                percentageConcluded = 100;
                inputBuffer.clear();
                bufferInfo.size = 0;
                bufferInfo.presentationTimeUs = -1;
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            }


            ProgressReport progressReport = new ProgressReport(DEFAULT_PROGRESS_REPORT_MESSAGE, percentageConcluded);
            progressReporter.reportProgress(progressReport);

            mediaCodec.queueInputBuffer(inputBufferId, AudioUtils.BUFFER_OFFSET, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
        }
    }

    @Override
    public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int outputBufferId, @NonNull MediaCodec.BufferInfo bufferInfo) {
        ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);

        ByteBuffer byteBuffer = MediaCodecCallback.copyByteBuffer(outputBuffer);
        AudioData audioData = new AudioData(byteBuffer, bufferInfo);


        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            Log.d(LOG_TAG, "onOutputBufferAvailable (84): End of output stream.");

            byteBufferWriteOutputStream.finishWriting();
            this.finishedDecoding = true;
        } else {
            byteBufferWriteOutputStream.add(audioData);
        }

        mediaCodec.releaseOutputBuffer(outputBufferId, false);
    }

    @Override
    public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
        Log.e(LOG_TAG, "onError (97): An error occurred on media codec" + mediaCodec);
        e.printStackTrace();

    }

    @Override
    public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
        Log.d(LOG_TAG, "onOutputFormatChanged (104): Output format changed to " + mediaFormat);
    }

    @Override
    public boolean finished() {
        return this.finishedDecoding;
    }
}
