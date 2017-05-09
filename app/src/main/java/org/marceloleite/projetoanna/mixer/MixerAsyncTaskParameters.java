package org.marceloleite.projetoanna.mixer;

import java.io.File;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTaskParameters {

    private File audioFile;
    private File movieFile;


    public MixerAsyncTaskParameters(File audioFile, File movieFile) {
        this.audioFile = audioFile;
        this.movieFile = movieFile;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public File getMovieFile() {
        return movieFile;
    }
}
