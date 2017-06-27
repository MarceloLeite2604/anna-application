package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import java.io.File;

/**
 * The result of an audio recorder file receiving process.
 */
public class ReceiveFileResult {

    /**
     * The code returned from the audio recorder file receiving process.
     */
    private final int returnCode;

    /**
     * The file received from audio recorder.
     */
    private final File fileReceived;

    /**
     * Constructor.
     *
     * @param returnCode   The code returned from the audio recorder file receiving process.
     * @param fileReceived The file received from audio recorder.
     */
    ReceiveFileResult(int returnCode, File fileReceived) {
        this.returnCode = returnCode;
        this.fileReceived = fileReceived;
    }

    /**
     * Returns the code returned from the audio recorder file receiving process.
     *
     * @return The code returned from the audio recorder file receiving process.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the file received from audio recorder.
     *
     * @return The file received from audio recorder.
     */
    public File getFileReceived() {
        return fileReceived;
    }
}
