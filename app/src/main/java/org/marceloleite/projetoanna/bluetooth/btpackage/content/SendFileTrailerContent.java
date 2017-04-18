package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class SendFileTrailerContent extends Content {

    private static final int CONTENT_HEADER = 0x078061fa;

    private static final int CONTENT_SIZE = 4;

    public SendFileTrailerContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file trailer content has " + CONTENT_SIZE + " byte(s).");
        }

        int contentHeader = java.nio.ByteBuffer.wrap(bytes).getInt();

        if (contentHeader != CONTENT_HEADER) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file trailer code (" + Integer.toHexString(CONTENT_HEADER) + ").");
        }
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(CONTENT_HEADER).array();
    }
}
