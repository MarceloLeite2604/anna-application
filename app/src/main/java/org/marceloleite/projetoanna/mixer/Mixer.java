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
import org.marceloleite.projetoanna.utils.audio.AudioUtils;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Mixes the recorded audio and video files.
 */
abstract class Mixer {

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


    /**
     * Request the mixing of recorded audio and video files.
     *
     * @param context         The context of the application in execution.
     * @param audioFile       The audio file to be mixed.
     * @param videoFile       The video file to be mixed.
     * @param startAudioDelay The delay between the audio and video start point.
     * @return The file which contains the mixed audio and video.
     */
    static File mixAudioAndVideo(Context context, File audioFile, File videoFile, long startAudioDelay) {

        /* Creates and configures the media extractor for video file. */
        MediaExtractorWrapper videoFileMediaExtractorWrapper = new MediaExtractorWrapper(videoFile, MediaFormat.MIMETYPE_VIDEO_AVC);

        long audioFileDuration = videoFileMediaExtractorWrapper.getMediaDuration() * 1000;
        Log.d(LOG_TAG, "mixAudioAndVideo (46): Audio file duration: " + audioFileDuration);

        File rawAudioFile = convertMp3ToRaw(context, audioFile, startAudioDelay, audioFileDuration);
        return createMixedMp4File(context, rawAudioFile, videoFileMediaExtractorWrapper);
    }

    /**
     * Converts an mp3 file to raw audio file.
     *
     * @param context          The context of the application in execution.
     * @param mp3File          The mp3 file to be converted.
     * @param audioTimeIgnored The amount of audio time which should be ignored in the conversion.
     * @param audioDuration    The duration of the raw audio time created.
     * @return The raw audio file created.
     */
    private static File convertMp3ToRaw(Context context, File mp3File, long audioTimeIgnored, long audioDuration) {
        Log.d(LOG_TAG, "convertMp3ToRaw (54): Converting mp3 file to raw audio.");

        /* Creates and configures the mp3 file media extractor. */
        MediaExtractorWrapper mp3FileMediaExtractorWrapper = new MediaExtractorWrapper(mp3File, MediaFormat.MIMETYPE_AUDIO_MPEG);

        /* Creates the new raw audio file. */
        File rawAudioFile = FileUtils.createFile(context, FileType.AUDIO_RAW_FILE);

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
     * @param audioDuration         The duration of the raw audio time created.
     * @return A {@link MediaCodecWrapper} object wrapping the mp3 decoder for the media file
     * manipulated by the {@link MediaExtractor} wrapped on the {@link MediaCodecWrapper} informed.
     */
    private static MediaCodecWrapper createMp3MediaDecoder(MediaExtractorWrapper mediaExtractorWrapper, File rawAudioFile, long audioTimeIgnored, long audioDuration) {
        Log.d(LOG_TAG, "createMp3MediaDecoder (74): Creating mp3 media decoder.");
        MediaCodecWrapper mediaCodecWrapper;

        MediaFormat mp3MediaFormat = mediaExtractorWrapper.getSelectedMediaTrackInfos().getMediaFormat();
        mediaCodecWrapper = new MediaCodecWrapper(mp3MediaFormat, mediaExtractorWrapper, rawAudioFile, audioTimeIgnored, audioDuration);
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
    private static File createMixedMp4File(Context context, File rawAudioFile, MediaExtractorWrapper videoFileMediaExtractorWrapper) {

        /* Creates the mixed video file. */
        File mixedVideoFile = FileUtils.createFile(context, FileType.MOVIE_FILE);

        /* Creates the media muxer to mix audio and video. */
        MediaMuxerWrapper mediaMuxerWrapper = new MediaMuxerWrapper(mixedVideoFile);

        /* Creates the video track on media muxer. */
        mediaMuxerWrapper.addMediaMuxerVideoTrack(videoFileMediaExtractorWrapper);

        /* Creates the AAC audio encoder */
        MediaCodecWrapper aacMediaEncoderWrapper = createAacMediaEncoder(rawAudioFile, mediaMuxerWrapper);

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

        return mixedVideoFile;
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
    private static MediaCodecWrapper createAacMediaEncoder(File rawAudioInputFile, MediaMuxerWrapper mediaMuxerWrapper) {
        MediaFormat aacMediaFormat = createAacMediaFormat();
        return new MediaCodecWrapper(aacMediaFormat, rawAudioInputFile, mediaMuxerWrapper);
    }

    /**
     * Creates the AAC media format used to encode the raw audio.
     *
     * @return The AAC media format used to encode the raw audio.
     */
    private static MediaFormat createAacMediaFormat() {
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
    private static void copyVideoToMixedFile(MediaExtractorWrapper videoFileMediaExtractorWrapper, MediaMuxerWrapper mediaMuxerWrapper) {

        boolean videoExtractionConcluded = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer byteBuffer = ByteBuffer.allocate(VIDEO_COPY_BUFFER_SIZE);
        bufferInfo.offset = AudioUtils.BUFFER_OFFSET;

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
                bufferInfo.flags = videoFileMediaExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(mediaMuxerWrapper.getVideoTrackIndex(), byteBuffer, bufferInfo);
                videoFileMediaExtractor.advance();
            }
        }
    }
}
