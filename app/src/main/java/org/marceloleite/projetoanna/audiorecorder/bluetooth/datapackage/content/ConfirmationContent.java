package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

import java.nio.ByteBuffer;

/**
 * The content of a confirmation package.
 */
public class ConfirmationContent extends Content {

    private static final String LOG_TAG = ConfirmationContent.class.getSimpleName();

    /**
     * Size of a confirmation content (in bytes).
     */
    private static final int CONTENT_SIZE = 4;

    /**
     * The identification of the package being confirmed.
     */
    private int packageId;

    /**
     * Creates a new confirmation content.
     *
     * @param packageId The identification of the package to be confirmed.
     */
    public ConfirmationContent(int packageId) {
        this.packageId = packageId;
    }

    /**
     * Returns the identification of the package being confirmed.
     *
     * @return The identification of the package being confirmed.
     */
    public int getPackageId() {
        return packageId;
    }

    /**
     * Creates a confirmation content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public ConfirmationContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The confirmation content has " + CONTENT_SIZE + " byte(s).");
        }

        this.packageId = ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(packageId).array();
    }
}
