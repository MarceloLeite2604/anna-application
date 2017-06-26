package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import org.marceloleite.projetoanna.audiorecorder.communicator.commander.Commander;

import java.io.File;

/**
 * The result of {@link Commander#requestLatestAudioFile()} method execution.
 */
public class RequestLatestAudioFileResult {

    /**
     * The code returned from {@link Commander#requestLatestAudioFile()} method execution.
     */
    private int returnCode;

    /**
     * The audio file received from audio recorder.
     */
    private File audioFile;

    /**
     * Constructor.
     *
     * @param returnCode The code returned from {@link Commander#requestLatestAudioFile()} method execution.
     * @param audioFile  The audio file received from audio recorder.
     */
    public RequestLatestAudioFileResult(int returnCode, File audioFile) {
        this.returnCode = returnCode;
        this.audioFile = audioFile;
    }

    /**
     * Returns the code returned from {@link Commander#requestLatestAudioFile()} method execution.
     *
     * @return The code returned from {@link Commander#requestLatestAudioFile()} method execution.
     */
    public int getReturnCode() {
        return returnCode;
    }

    public File getAudioFile() {
        return audioFile;
    }
}
