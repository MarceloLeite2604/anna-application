package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

import org.marceloleite.projetoanna.utils.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The content of a command result package.
 */
public class CommandResultContent extends Content {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = CommandResultContent.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(CommandResultContent.class);
    }

    /**
     * The size of the content (in bytes).
     */
    private static final int CONTENT_SIZE = 12;

    private static final int RESULT_CODE_BYTE_ARRAY_SIZE = 4;

    private static final int EXECUTION_DELAY_SECONDS_BYTE_ARRAY_SIZE = 4;

    private static final int EXECUTION_DELAY_MICROSECONDS_BYTE_ARRAY_SIZE = 4;

    /**
     * The result code.
     */
    private int resultCode;

    private int executionDelaySeconds;

    private int executionDelayMicroseconds;

    /**
     * Creates a new "command result" content.
     *
     * @param resultCode The result code to be stored in the content.
     */
    public CommandResultContent(int resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Returns the result code stored in the content.
     *
     * @return The result code stored in the content.
     */
    public int getResultCode() {
        return resultCode;
    }

    public int getExecutionDelaySeconds() {
        return executionDelaySeconds;
    }

    public int getExecutionDelayMicroseconds() {
        return executionDelayMicroseconds;
    }

    /**
     * Creates a command result content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public CommandResultContent(byte[] bytes) {
        if (bytes.length != CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The command result content has " + CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + RESULT_CODE_BYTE_ARRAY_SIZE);
        resultCode = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());
        byteArrayCounter += RESULT_CODE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + EXECUTION_DELAY_SECONDS_BYTE_ARRAY_SIZE);
        executionDelaySeconds = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());
        byteArrayCounter += EXECUTION_DELAY_SECONDS_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + EXECUTION_DELAY_MICROSECONDS_BYTE_ARRAY_SIZE);
        executionDelayMicroseconds = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());
        Log.d(CommandResultContent.class, LOG_TAG, "CommandResultContent (93): Execution delay: " + executionDelaySeconds + " seconds, " + executionDelayMicroseconds + " microseconds.");
    }

    @Override
    public byte[] convertToBytes() {
        byte[] resultCodeBytes = ByteBuffer.allocate(RESULT_CODE_BYTE_ARRAY_SIZE).putInt(resultCode).array();
        byte[] executionDelaySecondsBytes = ByteBuffer.allocate(EXECUTION_DELAY_SECONDS_BYTE_ARRAY_SIZE).putInt(executionDelaySeconds).array();
        byte[] executionDelayMicrosecondsBytes = ByteBuffer.allocate(EXECUTION_DELAY_MICROSECONDS_BYTE_ARRAY_SIZE).putInt(executionDelaySeconds).array();

        byte[] byteArray = new byte[CONTENT_SIZE];
        int byteArrayCounter = 0;

        System.arraycopy(resultCodeBytes, 0, byteArray, byteArrayCounter, resultCodeBytes.length);
        byteArrayCounter += RESULT_CODE_BYTE_ARRAY_SIZE;

        System.arraycopy(executionDelaySecondsBytes, 0, byteArray, byteArrayCounter, executionDelaySecondsBytes.length);
        byteArrayCounter += EXECUTION_DELAY_SECONDS_BYTE_ARRAY_SIZE;

        System.arraycopy(executionDelayMicrosecondsBytes, 0, byteArray, byteArrayCounter, executionDelayMicrosecondsBytes.length);

        return byteArray;

    }
}
