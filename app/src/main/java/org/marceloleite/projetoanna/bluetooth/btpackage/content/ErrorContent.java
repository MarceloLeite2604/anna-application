package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import android.support.v4.content.res.TypedArrayUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class ErrorContent extends Content {

    private static final int MINIMUM_CONTENT_SIZE = 8;

    private static final int ERROR_CODE_BYTE_ARRAY_SIZE = 4;

    private static final int ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE = 4;

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

    public ErrorContent(byte[] bytes) {
        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The error content has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, ERROR_CODE_BYTE_ARRAY_SIZE);
        this.errorCode = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();
        byteArrayCounter += ERROR_CODE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE);
        int errorMessageSize = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();
        byteArrayCounter += ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE;

        if (bytes.length != MINIMUM_CONTENT_SIZE + errorMessageSize) {
            throw new IllegalArgumentException("The message size informed on byte array is incorrect. Message size indicated: " + errorMessageSize + ", byte array size: " + bytes.length + ".");
        }

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, errorMessageSize);
        this.errorMessage = new String(byteArraySlice);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] errorCodeBytes = ByteBuffer.allocate(ERROR_CODE_BYTE_ARRAY_SIZE).putInt(errorCode).array();
        int errorMessageSize = errorMessage.getBytes().length;
        byte[] errorMessageLengthBytes = ByteBuffer.allocate(ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE).putInt(errorMessageSize).array();

        int totalByteArrayLength = errorCodeBytes.length + errorMessageLengthBytes.length + errorMessageSize;
        byte[] byteArray = new byte[totalByteArrayLength];
        int byteArrayCounter = 0;

        System.arraycopy(errorCodeBytes, 0, byteArray, byteArrayCounter, errorCodeBytes.length);
        byteArrayCounter += errorCodeBytes.length;

        System.arraycopy(errorMessageLengthBytes, 0, byteArray, byteArrayCounter, errorMessageLengthBytes.length);
        byteArrayCounter += errorMessageSize;

        System.arraycopy(errorMessage.getBytes(), 0, byteArray, byteArrayCounter, errorMessageSize);

        return byteArray;
    }
}
