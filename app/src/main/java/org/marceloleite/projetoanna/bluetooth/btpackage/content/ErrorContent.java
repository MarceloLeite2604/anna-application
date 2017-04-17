package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.util.Arrays;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class ErrorContent extends Content {

    private static final int MINIMUM_CONTENT_SIZE = 8;

    private static final int ERROR_CODE_BYTE_ARRAY_START_POSITION = 0;

    private static final int ERROR_MESSAGE_SIZE_BYTE_ARRAY_START_POSITION = 4;

    private static final int ERROR_MESSAGE_CONTENT_BYTE_ARRAY_START_POSITION = 8;

    private int errorCode;

    private String errorMessage;

    public ErrorContent(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorContent(byte[] byteArray) {
        if (byteArray.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + byteArray.length + " bytes. The error content has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        byte[] byteArraySlice = Arrays.copyOfRange(byteArray, ERROR_CODE_BYTE_ARRAY_START_POSITION, 4);
        this.errorCode = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        byteArraySlice = Arrays.copyOfRange(byteArray, ERROR_MESSAGE_SIZE_BYTE_ARRAY_START_POSITION, 4);
        int errorMessageSize = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (byteArray.length != MINIMUM_CONTENT_SIZE + errorMessageSize) {
            throw new IllegalArgumentException("The message size informed on byte array is incorrect. Message size indicated: " + errorMessageSize + ", byte array size: " + byteArray.length + ".");
        }

        byteArraySlice = Arrays.copyOfRange(byteArray, ERROR_MESSAGE_CONTENT_BYTE_ARRAY_START_POSITION, errorMessageSize);
        this.errorMessage = new String(byteArraySlice);
    }
}
