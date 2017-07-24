package org.marceloleite.projetoanna.mixer;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import org.marceloleite.projetoanna.mixer.media.MediaExtractorWrapper;
import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.MediaCodecWrapper;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.MediaUtils;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReport;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReporter;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Mixes the recorded audio and video files.
 */
class Mixer implements ProgressReporter {

    private static final String DEFAULT_PROGRESS_REPORT_MESSAGE = "Mixing audio and video.";

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Mixer.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Size of the buffer to used to copy the video.
     */
    private static final int VIDEO_COPY_BUFFER_SIZE = 1024 * 1024;

    /**
     * Maximum size of a buffer of to encode (in bytes).
     */
    private static final int AAC_ENCODING_MAX_INPUT_SIZE = 16 * 1024;

    private ProgressReporter progressReporterMixing;

    private MixingStage currentMixingStage;

    public Mixer(ProgressReporter progressReporterMixing) {
        this.progressReporterMixing = progressReporterMixing;
    }

    /**
     * Request the mixing of recorded audio and video files.
     *
     * @param context         The context of the application in execution.
     * @param audioFile       The audio file to be mixed.
     * @param videoFile       The video file to be mixed.
     * @param startAudioDelay The delay between the audio and video start point.
     * @return The file which contains the mixed audio and video.
     */
    public File mixAudioAndVideo(Context context, File audioFile, File videoFile, long startAudioDelay) {

        /* Creates and configures the media extractor for video file. */
        MediaExtractorWrapper videoFileMediaExtractorWrapper = new MediaExtractorWrapper(videoFile, MediaFormat.MIMETYPE_VIDEO_AVC);

        File rawAudioFile = convertMp3ToRaw(context, audioFile, startAudioDelay);
        File mixedMp4File = createMixedMp4File(context, rawAudioFile, videoFileMediaExtractorWrapper);

        /* Deletes the temporary file. */
        if (!rawAudioFile.delete()) {
            Log.e(LOG_TAG, "mixAudioAndVideo (80): Could not delete temporary file " + rawAudioFile + "\".");
        }

        return mixedMp4File;
    }

    /**
     * Converts an mp3 file to raw audio file.
     *
     * @param context          The context of the application in execution.
     * @param mp3File          The mp3 file to be converted.
     * @param audioTimeIgnored The amount of audio time which should be ignored in the conversion.
     * @return The raw audio file created.
     */
    private File convertMp3ToRaw(Context context, File mp3File, long audioTimeIgnored) {
        Log.d(LOG_TAG, "convertMp3ToRaw (54): Converting mp3 file to raw audio.");

        currentMixingStage = MixingStage.CONVERT_MP3_TO_RAW;

        /* Creates and configures the mp3 file media extractor. */
        MediaExtractorWrapper mp3FileMediaExtractorWrapper = new MediaExtractorWrapper(mp3File, MediaFormat.MIMETYPE_AUDIO_MPEG);

        /* Creates the new raw audio file. */
        File rawAudioFile = FileUtils.createTemporaryFile(context, FileType.AUDIO_RAW_FILE);

        long audioDuration = mp3FileMediaExtractorWrapper.getMediaDuration() * 1000;
        Log.d(LOG_TAG, "mixAudioAndVideo (46): Audio file duration: " + audioDuration);

        /* Creates and configures the mp3 media decoder. */
        MediaCodecWrapper mp3MediaDecoderWrapper = createMp3MediaDecoder(mp3FileMediaExtractorWrapper, rawAudioFile, audioTimeIgnored, audioDuration);

        /* Waits the decodification to conclude. */
        Log.d(LOG_TAG, "convertMp3ToRaw (66): Decoding mp3 file.");
        mp3MediaDecoderWrapper.startAndWaitCodec();
        Log.d(LOG_TAG, "convertMp3ToRaw (68): Decodification complete.");

        return rawAudioFile;
    }

    /**
     * Creates a {@link MediaCodecWrapper} which contains the mp3 media decoder for the media file
     * manipulated by the {@link MediaExtractor} wrapped on the {@link MediaCodecWrapper} informed.
     *
     * @param mediaExtractorWrapper The wrapper of the mp3 file media extractor for which the media
     *                              decoder must be created.
     * @param rawAudioFile          The file which will store the raw audio extracted from the mp3
     *                              file.
     * @param audioTimeIgnored      The amount of audio time which should be ignored in the
     *                              conversion.
     * @param audioDuration         The duration of the audio file.
     * @return A {@link MediaCodecWrapper} object wrapping the mp3 decoder for the media file
     * manipulated by the {@link MediaExtractor} wrapped on the {@link MediaCodecWrapper} informed.
     */
    private MediaCodecWrapper createMp3MediaDecoder(MediaExtractorWrapper mediaExtractorWrapper, File rawAudioFile, long audioTimeIgnored, long audioDuration) {
        Log.d(LOG_TAG, "createMp3MediaDecoder (74): Creating mp3 media decoder.");
        MediaCodecWrapper mediaCodecWrapper;

        MediaFormat mp3MediaFormat = mediaExtractorWrapper.getSelectedMediaTrackInfos().getMediaFormat();
        mediaCodecWrapper = new MediaCodecWrapper(mp3MediaFormat, mediaExtractorWrapper, rawAudioFile, audioTimeIgnored, audioDuration, this);
        return mediaCodecWrapper;
    }

    /**
     * Creates an mp4 file with the raw audio file content mixed with the video file stored
     * manipulated by the {@link MediaExtractor} wrapped on the {@link MediaExtractorWrapper} object
     * informed.
     *
     * @param context                        The context of the application in execution.
     * @param rawAudioFile                   The file which contains the raw audio data.
     * @param videoFileMediaExtractorWrapper The wrapper of the {@link MediaExtractor} object which
     *                                       manipulates the video file to be mixed.
     * @return An mp4 file with the audio and video mixed.
     */
    private File createMixedMp4File(Context context, File rawAudioFile, MediaExtractorWrapper videoFileMediaExtractorWrapper) {

        /* Creates the mixed video file. */
        File mixedVideoTemporaryFile = FileUtils.createTemporaryFile(context, FileType.MOVIE_FILE);

        /* Creates the media muxer to mix audio and video. */
        MediaMuxerWrapper mediaMuxerWrapper = new MediaMuxerWrapper(mixedVideoTemporaryFile);

        /* Creates the video track on media muxer. */
        mediaMuxerWrapper.addMediaMuxerVideoTrack(videoFileMediaExtractorWrapper);

        /* Creates the AAC audio encoder */
        MediaCodecWrapper aacMediaEncoderWrapper = createAacMediaEncoder(rawAudioFile, mediaMuxerWrapper);

        currentMixingStage = MixingStage.ENCODE_RAW_TO_AAC;

        Log.d(LOG_TAG, "createMixedMp4File (96): Encoding raw audio to AAC format.");
        aacMediaEncoderWrapper.startAndWaitCodec();

        /*
        Once the media encoder has finished its work, this function's mediaMuxer has to be
        updated so we can conclude the video copy.
         */
        // mediaMuxer = ((MediaEncoderCallback) aacMediaEncoderWrapper.getCallback()).getMediaMuxer();

        Log.d(LOG_TAG, "createMixedMp4File (105): Encoding complete.");

        Log.d(LOG_TAG, "createMixedMp4File (107): Copying video to mix file.");
        copyVideoToMixedFile(videoFileMediaExtractorWrapper, mediaMuxerWrapper);
        Log.d(LOG_TAG, "createMixedMp4File (109): Video copying complete.");

        /* Stops the media muxer. */
        mediaMuxerWrapper.stopMediaMuxer();

        /* Creates the final movie file. */
        File movieFile = FileUtils.createFile(context, FileType.MOVIE_FILE);

        /* Copies the successfully mixed file to it output. */
        FileUtils.copyFile(mixedVideoTemporaryFile, movieFile);

        /* Deletes the temporary file. */
        if (!mixedVideoTemporaryFile.delete()) {
            Log.e(LOG_TAG, "createMixedMp4File (168): Could not delete temporary file \"" + mixedVideoTemporaryFile + "\".");
        }

        return movieFile;
    }

    /**
     * Creates an AAC media encoder which will encode the content of the raw audio file informed and
     * store the result on the {@link MediaMuxer} wrapped on the {@link MediaMuxerWrapper} informed.
     *
     * @param rawAudioInputFile The raw audio file which will be encoded.
     * @param mediaMuxerWrapper The wrapper of the {@link MediaMuxer} where the encoded audio will
     *                          be stored.
     * @return The AAc media encoder created.
     */
    private MediaCodecWrapper createAacMediaEncoder(File rawAudioInputFile, MediaMuxerWrapper mediaMuxerWrapper) {
        MediaFormat aacMediaFormat = createAacMediaFormat();
        return new MediaCodecWrapper(aacMediaFormat, rawAudioInputFile, mediaMuxerWrapper, this);
    }

    /**
     * Creates the AAC media format used to encode the raw audio.
     *
     * @return The AAC media format used to encode the raw audio.
     */
    private MediaFormat createAacMediaFormat() {
        MediaFormat aacMediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AudioUtils.SAMPLE_RATE, 2);
        aacMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        aacMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, AudioUtils.AAC_ENCODING_BIT_RATE);
        aacMediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, AAC_ENCODING_MAX_INPUT_SIZE);
        return aacMediaFormat;
    }

    /**
     * Copies the video content to the {@link MediaMuxer} wrapped by the {@link MediaMuxerWrapper}
     * informed.
     *
     * @param videoFileMediaExtractorWrapper The wrapper of the {@link MediaExtractor} which
     *                                       controls the video file to be copied.
     * @param mediaMuxerWrapper              The wrapper of the {@link MediaMuxer} where the video
     *                                       content will be copied to.
     */
    private void copyVideoToMixedFile(MediaExtractorWrapper videoFileMediaExtractorWrapper, MediaMuxerWrapper mediaMuxerWrapper) {

        currentMixingStage = MixingStage.COPYING_VIDEO_TO_MOVIE;

        boolean videoExtractionConcluded = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer byteBuffer = ByteBuffer.allocate(VIDEO_COPY_BUFFER_SIZE);
        bufferInfo.offset = AudioUtils.BUFFER_OFFSET;

        long videoFileDuration = MediaUtils.retrieveMediaDuration(videoFileMediaExtractorWrapper.getMediaFile()) * 1000;
        int percentageConcluded;

        MediaExtractor videoFileMediaExtractor = videoFileMediaExtractorWrapper.getMediaExtractor();
        MediaMuxer mediaMuxer = mediaMuxerWrapper.getMediaMuxer();

        while (!videoExtractionConcluded) {
            bufferInfo.size = videoFileMediaExtractor.readSampleData(byteBuffer, AudioUtils.BUFFER_OFFSET);

            if (bufferInfo.size < 0) {
                Log.d(LOG_TAG, "copyVideoToMixedFile (146): End of video copy.");
                videoExtractionConcluded = true;
                bufferInfo.size = 0;
            } else {
                bufferInfo.presentationTimeUs = videoFileMediaExtractor.getSampleTime();
                percentageConcluded = (int) (((double) bufferInfo.presentationTimeUs / (double) videoFileDuration) * 100);
                ProgressReport progressReport = new ProgressReport(DEFAULT_PROGRESS_REPORT_MESSAGE, percentageConcluded);
                reportProgress(progressReport);
                //noinspection WrongConstant
                bufferInfo.flags = videoFileMediaExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(mediaMuxerWrapper.getVideoTrackIndex(), byteBuffer, bufferInfo);
                videoFileMediaExtractor.advance();
            }
        }
    }

    @Override
    public void reportProgress(ProgressReport progressReport) {
        int mixingProgressPercentage = MixingStage.getStartPercentageFor(currentMixingStage);
        // Log.d(LOG_TAG, "getStartPercentageFor (51): Start percentage for " + currentMixingStage + " is " + mixingProgressPercentage);

        int additionalPercentage = (int) (progressReport.getPercentageConcluded() * currentMixingStage.getRelativeWeight());
        // Log.d(LOG_TAG, "getStartPercentageFor (51): Additional percentage informed for " + currentMixingStage + " is " + additionalPercentage);
        mixingProgressPercentage += additionalPercentage;

        ProgressReport mixingProgressReport = new ProgressReport(DEFAULT_PROGRESS_REPORT_MESSAGE, mixingProgressPercentage);
        progressReporterMixing.reportProgress(mixingProgressReport);
    }
}
