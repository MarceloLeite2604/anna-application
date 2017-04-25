package org.marceloleite.projetoanna.audiorecorder.datapackage.content;

import java.nio.ByteBuffer;

/**
 * The content of a command result package.
 */
public class CommandResultContent extends Content {

    private static final String LOG_TAG = CommandResultContent.class.getSimpleName();

    /**
     * The size of the content (in bytes).
     */
    private static final int CONTENT_SIZE = 4;

    /**
     * The result code.
     */
    private int resultCode;

    /**
     * Creates a new "command result" content.
     *
     * @param resultCode The result code to be stored in the content.
     */
    public CommandResultContent(int resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Returns the result code stored in the content.
     *
     * @return The result code stored in the content.
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Creates a command result content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public CommandResultContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The command result content has " + CONTENT_SIZE + " byte(s).");
        }

        this.resultCode = ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(resultCode).array();
    }
}
