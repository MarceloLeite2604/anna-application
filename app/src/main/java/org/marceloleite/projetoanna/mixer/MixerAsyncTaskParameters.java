package org.marceloleite.projetoanna.mixer;

import android.content.Context;

import java.io.File;

/**
 * The parameters informed to execute a {@link MixerAsyncTask} operation.
 */
public class MixerAsyncTaskParameters {

    /**
     * The context of the application which requested the {@link MixerAsyncTask} execution.
     */
    private final Context context;

    /**
     * The audio file to be mixed.
     */
    private final File audioFile;

    /**
     * The video file to be mixed.
     */
    private final File videoFile;

    /**
     * The delay between the audio and video starting point.
     */
    private final long audioAndVideoDelayTime;

    /**
     * Constructor.
     *
     * @param context                The context of the application which requested the {@link MixerAsyncTask} execution.
     * @param audioFile              The audio file to be mixed.
     * @param movieFile              The video file to be mixed.
     * @param audioAndVideoDelayTime The delay between the audio and video starting point.
     */
    public MixerAsyncTaskParameters(Context context, File audioFile, File movieFile, long audioAndVideoDelayTime) {
        this.context = context;
        this.audioFile = audioFile;
        this.videoFile = movieFile;
        this.audioAndVideoDelayTime = audioAndVideoDelayTime;
    }

    /**
     * Returns the context of the application which requested the {@link MixerAsyncTask} execution.
     *
     * @return The context of the application which requested the {@link MixerAsyncTask} execution.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns the audio file to be mixed.
     *
     * @return The audio file to be mixed.
     */
    File getAudioFile() {
        return audioFile;
    }

    /**
     * Returns the video file to be mixed.
     *
     * @return The video file to be mixed.
     */
    File getVideoFile() {
        return videoFile;
    }

    /**
     * Returns the delay between the audio and video starting point.
     *
     * @return The delay between the audio and video starting point.
     */
    long getAudioAndVideoDelayTime() {
        return audioAndVideoDelayTime;
    }
}
