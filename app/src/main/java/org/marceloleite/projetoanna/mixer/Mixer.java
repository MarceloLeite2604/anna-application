package org.marceloleite.projetoanna.mixer;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class Mixer {

    private static final int BUFFER_SIZE = 1024 * 1024;

    private static final String LOG_TAG = Mixer.class.getSimpleName();

    private String movieFileAbsolutePath;

    private String audioFileAbsolutePath;

    private File temporaryVideoFile;

    private File videoOutputFile;

    public Mixer(String movieFileAbsolutePath, String audioFileAbsolutePath) {
        this.movieFileAbsolutePath = movieFileAbsolutePath;
        this.audioFileAbsolutePath = audioFileAbsolutePath;
    }

    public void test() throws IOException {
        extractVideoFromMovie();
        mixAudioAndVideo();
    }

    public void extractVideoFromMovie() throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();

        try {
            mediaExtractor.setDataSource(movieFileAbsolutePath);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        int videoFileTracks = mediaExtractor.getTrackCount();
        Log.d(LOG_TAG, "test, 37: Total of tracks on this file: " + videoFileTracks);

        for (int counter = 0; counter < videoFileTracks; counter++) {
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(counter);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            Log.d(LOG_TAG, "test, 45: Mime of track " + counter + ": " + mime);
            if (MediaFormat.MIMETYPE_VIDEO_AVC.equals(mime)) {
                mediaExtractor.selectTrack(counter);
                break;
            }
        }
        ByteBuffer inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        temporaryVideoFile = FileUtils.createFile(FileType.VIDEO_FILE);
        FileOutputStream fileOutputStream = new FileOutputStream(temporaryVideoFile);

        boolean trackExtractionConcluded = false;
        int bytesRead;
        int sampleTrackIndex = -1;
        int totalOfBytes = 0;

        while (!trackExtractionConcluded) {
            bytesRead = mediaExtractor.readSampleData(inputBuffer, 0);
            if (bytesRead > 0) {
                sampleTrackIndex = mediaExtractor.getSampleTrackIndex();
                fileOutputStream.write(inputBuffer.array());
                Log.d(LOG_TAG, "test, 57: Extracted " + bytesRead + " byte(s) from track index " + sampleTrackIndex + ".");
                totalOfBytes += bytesRead;
                mediaExtractor.advance();
            } else {
                trackExtractionConcluded = true;
            }
        }
        Log.d(LOG_TAG, "test, 63: " + totalOfBytes + " byte(s) written of file \"" + temporaryVideoFile.getAbsolutePath() + "\".");
        fileOutputStream.close();

        mediaExtractor.release();
    }

    public void mixAudioAndVideo() throws IOException {

        videoOutputFile = FileUtils.createFile(FileType.VIDEO_FILE);

        MediaMuxer mediaMuxer = new MediaMuxer(videoOutputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        MediaExtractor audioMediaExtractor = new MediaExtractor();
        audioMediaExtractor.setDataSource(audioFileAbsolutePath);

        MediaExtractor movieMediaExtractor = new MediaExtractor();
        movieMediaExtractor.setDataSource(movieFileAbsolutePath);

        GetMediaFormatFromFileResult getAudioMediaFormatFromFileResult;
        getAudioMediaFormatFromFileResult = getMediaFormatFromFile(audioMediaExtractor, MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioMediaFormat = getAudioMediaFormatFromFileResult.mediaFormat;
        audioMediaExtractor.selectTrack(getAudioMediaFormatFromFileResult.trackIndex);

        GetMediaFormatFromFileResult getVideoMediaFormatFromFileResult = getMediaFormatFromFile(movieMediaExtractor, MediaFormat.MIMETYPE_VIDEO_AVC);
        MediaFormat videoMediaFormat = getVideoMediaFormatFromFileResult.mediaFormat;
        movieMediaExtractor.selectTrack(getVideoMediaFormatFromFileResult.trackIndex);

        int videoTrackIndex = mediaMuxer.addTrack(videoMediaFormat);
        int audioTrackIndex = mediaMuxer.addTrack(audioMediaFormat);

        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

        movieMediaExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

        boolean finished = false;
        int frameCount = 0;
        int offset = 100; /* TODO: Why not 0? */

        mediaMuxer.start();
        while (!finished) {

            videoBufferInfo.offset = offset;
            videoBufferInfo.size = movieMediaExtractor.readSampleData(byteBuffer, offset);

            if (videoBufferInfo.size < 0) {
                Log.d(LOG_TAG, "mixAudioAndVideo, 146: End of video copy.");
                finished = true;
                videoBufferInfo.size = 0;
            } else {
                videoBufferInfo.presentationTimeUs = movieMediaExtractor.getSampleTime();
                videoBufferInfo.flags = movieMediaExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, videoBufferInfo);
                movieMediaExtractor.advance();
                frameCount++;
                Log.d(LOG_TAG, "mixAudioAndVideo, 156: Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024);
            }
        }

        audioMediaExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

        finished = false;
        frameCount = 0;
        offset = 0;
        while (!finished) {
            audioBufferInfo.offset = offset;
            audioBufferInfo.size = audioMediaExtractor.readSampleData(byteBuffer, offset);

            if (audioBufferInfo.size < 0) {
                Log.d(LOG_TAG, "mixAudioAndVideo, 163: End of audio copy.");
                finished = true;
                audioBufferInfo.size = 0;
            } else {
                audioBufferInfo.presentationTimeUs = audioMediaExtractor.getSampleTime();
                audioBufferInfo.flags = audioMediaExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, audioBufferInfo);
                audioMediaExtractor.advance();
                frameCount++;

                Log.d(LOG_TAG, "mixAudioAndVideo, 175: Frame (" + frameCount + ") Audio PresentationTimeUs:" + audioBufferInfo.presentationTimeUs + " Flags:" + audioBufferInfo.flags + " Size(KB) " + audioBufferInfo.size / 1024);
            }
        }


        mediaMuxer.stop();
        mediaMuxer.release();
    }

    private GetMediaFormatFromFileResult getMediaFormatFromFile(MediaExtractor mediaExtractor, String mimeType) throws IOException {
        GetMediaFormatFromFileResult getMediaFormatFromFileResult = new GetMediaFormatFromFileResult();

        int totalAudioTracks = mediaExtractor.getTrackCount();

        for (int counter = 0; counter < totalAudioTracks; counter++) {
            MediaFormat trackMediaFormat = mediaExtractor.getTrackFormat(counter);
            String mime = trackMediaFormat.getString(MediaFormat.KEY_MIME);
            if (mimeType.equals(mime)) {
                getMediaFormatFromFileResult.trackIndex = counter;
                getMediaFormatFromFileResult.mediaFormat = trackMediaFormat;
            }
        }
        return getMediaFormatFromFileResult;
    }

    private class GetMediaFormatFromFileResult {
        public MediaFormat mediaFormat;
        public int trackIndex;
    }
}
