package org.marceloleite.projetoanna.bluetooth.btpackage.content;

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

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public CommandResultContent(byte[] byteArray) {
        if (byteArray.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + byteArray.length + " bytes. The command result content has " + CONTENT_SIZE + " byte(s).");
        }

        this.resultCode = java.nio.ByteBuffer.wrap(byteArray).getInt();
    }
}
