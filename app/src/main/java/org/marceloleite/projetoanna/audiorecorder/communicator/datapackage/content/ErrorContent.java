package org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content;

import org.marceloleite.projetoanna.utils.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The content of an error package.
 */
public class ErrorContent extends Content {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ErrorContent.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Minimum acceptable content size (in bytes).
     */
    private static final int MINIMUM_CONTENT_SIZE = 8;

    /**
     * Size of the byte array representing the error code (in bytes).
     */
    private static final int ERROR_CODE_BYTE_ARRAY_SIZE = 4;

    /**
     * Size of the byte array representing the error message size (in bytes).
     */
    private static final int ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE = 4;

    /**
     * The error code stored in the content.
     */
    private int errorCode;

    /**
     * The error message stored in the content.
     */
    private String errorMessage;

    /**
     * Creates an error content.
     *
     * @param errorCode    The error code to be stored.
     * @param errorMessage The error message to be stored.
     */
    @SuppressWarnings("unused")
    public ErrorContent(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the error code stored on the content.
     *
     * @return The error code stored on the content.
     */
    @SuppressWarnings("unused")
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the error message stored on the content.
     *
     * @return The error message stored on the content.
     */
    @SuppressWarnings("unused")
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Creates an error content with the byte array content.
     *
     * @param bytes The byte array containing the information to be stored on the error content.
     */
    public ErrorContent(byte[] bytes) {
        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The error content has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + ERROR_CODE_BYTE_ARRAY_SIZE);
        this.errorCode = ByteBuffer.wrap(byteArraySlice).getInt();
        byteArrayCounter += ERROR_CODE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE);
        int errorMessageSize = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());
        byteArrayCounter += ERROR_MESSAGE_SIZE_BYTE_ARRAY_SIZE;

        if (bytes.length != MINIMUM_CONTENT_SIZE + errorMessageSize) {
            throw new IllegalArgumentException("The message size informed on byte array is incorrect. Message size indicated: " + errorMessageSize + ", byte array size: " + bytes.length + ".");
        }

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + errorMessageSize);
        this.errorMessage = new String(byteArraySlice);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] errorCodeBytes = ByteBuffer.allocate(ERROR_CODE_BYTE_ARRAY_SIZE).putInt(Integer.reverseBytes(this.errorCode)).array();
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
