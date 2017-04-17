package org.marceloleite.projetoanna.bluetooth.btpackage.content;

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

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public ConfirmationContent(byte[] byteArray) {
        if (byteArray.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + byteArray.length + " bytes. The confirmation content has " + CONTENT_SIZE + " byte(s).");
        }

        this.packageId = java.nio.ByteBuffer.wrap(byteArray).getInt();
    }
}
