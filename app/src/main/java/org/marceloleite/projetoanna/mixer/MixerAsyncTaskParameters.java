package org.marceloleite.projetoanna.mixer;

import org.marceloleite.projetoanna.utils.chronometer.Chronometer;

import java.io.File;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTaskParameters {

    private File audioFile;
    private File movieFile;
    private long startAudioDelay;
    private long stopAudioDelay;


    public MixerAsyncTaskParameters(File audioFile, File movieFile, long startAudioDelay, long stopAudioDelay) {
        this.audioFile = audioFile;
        this.movieFile = movieFile;
        this.startAudioDelay = startAudioDelay;
        this.stopAudioDelay = stopAudioDelay;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public File getMovieFile() {
        return movieFile;
    }

    public long getStartAudioDelay() {
        return startAudioDelay;
    }

    public long getStopAudioDelay() {
        return stopAudioDelay;
    }
}
