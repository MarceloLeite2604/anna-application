package org.marceloleite.projetoanna.mixer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.mixer.media.codec.MediaCodecWrapper;
import org.marceloleite.projetoanna.utils.audio.AudioUtils;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public abstract class Mixer {

    private static final int VIDEO_COPY_BUFFER_SIZE = 1024 * 1024;

    private static final String LOG_TAG = Mixer.class.getSimpleName();

    public static File mixAudioAndVideo(File audioFile, File videoFile, long startAudioDelay, long stopAudioDelay) throws IOException {
        File rawAudioFile = convertMp3ToRaw(audioFile, startAudioDelay, stopAudioDelay);
        File mixedVideoFile = createMixedMp4File(rawAudioFile, videoFile);
        return mixedVideoFile;
    }

    private static File convertMp3ToRaw(File mp3File, long startAudioDelay, long stopAudioDelay) throws IOException {
        Log.d(LOG_TAG, "convertMp3ToRaw, 40: Converting mp3 file to raw audio.");

        /* Creates and configures the mp3 file media extractor. */
        MediaExtractorWrapper mp3FileMediaExtractorWrapper = new MediaExtractorWrapper(mp3File, MediaFormat.MIMETYPE_AUDIO_MPEG);

        /* Creates the new raw audio file. */
        File rawAudioFile = FileUtils.createFile(FileType.AUDIO_RAW_FILE);

        /* Creates and configures the mp3 media decoder. */
        MediaCodecWrapper mp3MediaDecoderWrapper = createMp3MediaDecoder(mp3FileMediaExtractorWrapper, rawAudioFile, startAudioDelay, stopAudioDelay);

        /* Waits the decodification to conclude. */
        Log.d(LOG_TAG, "convertMp3ToRaw, 56: Decoding mp3 file.");
        mp3MediaDecoderWrapper.startAndWaitCodec();
        Log.d(LOG_TAG, "convertMp3ToRaw, 262: Decodification complete.");

        return rawAudioFile;
    }

    private static MediaCodecWrapper createMp3MediaDecoder(MediaExtractorWrapper mediaExtractorWrapper, File rawAudioFile, long startAudioDelay, long stopAudioDelay) throws IOException {
        Log.d(LOG_TAG, "createMp3MediaDecoder, 119: Creating mp3 media decoder.");
        MediaCodecWrapper mediaCodecWrapper;

        MediaFormat mp3MediaFormat = mediaExtractorWrapper.getSelectedMediaTrackInfos().getMediaFormat();
        mediaCodecWrapper = new MediaCodecWrapper(mp3MediaFormat, mediaExtractorWrapper, rawAudioFile, startAudioDelay, stopAudioDelay);
        return mediaCodecWrapper;
    }

    private static File createMixedMp4File(File rawAudioFile, File videoFile) throws IOException {

        /* Creates and configures the media extractor for video file. */
        MediaExtractorWrapper videoFileMediaExtractorWrapper = new MediaExtractorWrapper(videoFile, MediaFormat.MIMETYPE_VIDEO_AVC);

        /* Creates the mixed video file. */
        File mixedVideoFile = FileUtils.createFile(FileType.MOVIE_FILE);

        /* Creates the media muxer to mix audio and video. */
        MediaMuxerWrapper mediaMuxerWrapper = new MediaMuxerWrapper(mixedVideoFile);

        /* Creates the video track on media muxer. */
        mediaMuxerWrapper.addMediaMuxerVideoTrack(videoFileMediaExtractorWrapper);

        /* Creates the AAC audio encoder */
        MediaCodecWrapper aacMediaEncoderWrapper = createAacMediaEncoder(rawAudioFile, mediaMuxerWrapper);

        Log.d(LOG_TAG, "createMixedMp4File, 138: Encoding raw audio to AAC format.");
        aacMediaEncoderWrapper.startAndWaitCodec();

        /*
        Once the media encoder has finished its work, this function's mediaMuxer has to be
        updated so we can conclude the video copy.
         */
        // mediaMuxer = ((MediaEncoderCallback) aacMediaEncoderWrapper.getCallback()).getMediaMuxer();

        Log.d(LOG_TAG, "createMixedMp4File, 118: Encoding complete.");

        Log.d(LOG_TAG, "createMixedMp4File, 106: Copying video to mix file.");
        copyVideoToMixedFile(videoFileMediaExtractorWrapper, mediaMuxerWrapper);
        Log.d(LOG_TAG, "createMixedMp4File, 106: Video copying complete.");

        /* Stops the media muxer. */
        mediaMuxerWrapper.stopMediaMuxer();

        return mixedVideoFile;
    }

    private static MediaCodecWrapper createAacMediaEncoder(File inputFile, MediaMuxerWrapper mediaMuxerWrapper) throws IOException {
        MediaFormat aacMediaFormat = createAacMediaFormat();
        MediaCodecWrapper mediaCodecWrapper = new MediaCodecWrapper(aacMediaFormat, inputFile, mediaMuxerWrapper);

        return mediaCodecWrapper;
    }

    private static MediaFormat createAacMediaFormat() {
        MediaFormat aacMediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AudioUtils.SAMPLE_RATE, 2);
        aacMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        aacMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, AudioUtils.AAC_ENCODING_BIT_RATE);
        aacMediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, AudioUtils.AAC_ENCODING_MAX_INPUT_SIZE);
        return aacMediaFormat;
    }

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
                Log.d(LOG_TAG, "mixAudioAndVideo, 146: End of video copy.");
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
