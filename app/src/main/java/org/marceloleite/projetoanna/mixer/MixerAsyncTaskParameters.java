package org.marceloleite.projetoanna.mixer;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTaskParameters {

    private String audioFileAbsolutePath;
    private String videoFileAbsolutePath;


    public MixerAsyncTaskParameters(String videoFileAbsolutePath, String audioFileAbsolutePath) {
        this.audioFileAbsolutePath = audioFileAbsolutePath;
        this.videoFileAbsolutePath = videoFileAbsolutePath;
    }

    public String getAudioFileAbsolutePath() {
        return audioFileAbsolutePath;
    }

    public String getVideoFileAbsolutePath() {
        return videoFileAbsolutePath;
    }
}
