package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class CommandResultContent extends Content {

    private static final int CONTENT_SIZE = 4;

    private int resultCode;

    public CommandResultContent(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public CommandResultContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The command result content has " + CONTENT_SIZE + " byte(s).");
        }

        this.resultCode = java.nio.ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public byte[] convertToBytes() {
        return ByteBuffer.allocate(CONTENT_SIZE).putInt(resultCode).array();
    }
}
