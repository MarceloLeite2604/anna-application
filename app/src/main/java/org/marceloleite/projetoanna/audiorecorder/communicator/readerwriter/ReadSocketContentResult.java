package org.marceloleite.projetoanna.audiorecorder.communicator.readerwriter;

/**
 * The result of the bluetooth socket reading process.
 */
public class ReadSocketContentResult {

    /**
     * The code returned from bluetooth socket reading process.
     */
    private final int returnCode;

    /**
     * The content read from the socket.
     */
    private final byte[] contentRead;

    /**
     * Constructor.
     *
     * @param returnCode  The code returned from bluetooth socket reading process.
     * @param contentRead The content read from the socket.
     */
    public ReadSocketContentResult(int returnCode, byte[] contentRead) {
        this.returnCode = returnCode;
        this.contentRead = contentRead;
    }

    /**
     * Returns the code returned from bluetooth socket reading process.
     *
     * @return The code returned from bluetooth socket reading process.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the content read from the socket.
     *
     * @return The content read from the socket.
     */
    public byte[] getContentRead() {
        return contentRead;
    }
}
