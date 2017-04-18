package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class ConfirmationContent extends Content {

    private static final int CONTENT_SIZE = 4;

    private int packageId;

    public ConfirmationContent(int packageId) {
        this.packageId = packageId;
    }

    public int getPackageId() {
        return packageId;
    }

    public ConfirmationContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The confirmation content has " + CONTENT_SIZE + " byte(s).");
        }

        this.packageId = java.nio.ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(packageId).array();
    }
}
