package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

import java.nio.ByteBuffer;

/**
 * The content of a file trailer package.
 */
public class FileTrailerContent extends Content {

    private static final String LOG_TAG = FileTrailerContent.class.getSimpleName();

    /**
     * The code which identifies a file trailer content.
     */
    private static final int CONTENT_HEADER_CODE = 0x078061fa;

    /**
     * Size of a file trailer content (in bytes).
     */
    private static final int CONTENT_SIZE = 4;

    /**
     * Creates a new file trailer content.
     */
    public FileTrailerContent() {
    }

    /**
     * Creates a file trailer content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public FileTrailerContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file trailer content has " + CONTENT_SIZE + " byte(s).");
        }

        int contentHeader = java.nio.ByteBuffer.wrap(bytes).getInt();

        if (contentHeader != CONTENT_HEADER_CODE) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file trailer code (" + Integer.toHexString(CONTENT_HEADER_CODE) + ").");
        }
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(CONTENT_HEADER_CODE).array();
    }
}
