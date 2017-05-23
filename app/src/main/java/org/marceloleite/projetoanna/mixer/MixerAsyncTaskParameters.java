package org.marceloleite.projetoanna.mixer;

import java.io.File;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTaskParameters {

    private File audioFile;
    private File movieFile;
    private long startAudioDelay;

    public MixerAsyncTaskParameters(File audioFile, File movieFile, long startAudioDelay) {
        this.audioFile = audioFile;
        this.movieFile = movieFile;
        this.startAudioDelay = startAudioDelay;
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
}
