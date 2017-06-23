package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

import org.marceloleite.projetoanna.utils.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The content of a file chunk package.
 */
public class FileChunkContent extends Content {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = FileChunkContent.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The code which identifies a file header content.
     */
    private static final int CONTENT_HEADER_CODE = 0x43ece390;

    /**
     * Minimum acceptable content size (in bytes).
     */
    private static final int MINIMUM_CONTENT_SIZE = 8;

    /**
     * Size of the byte array representing the content header (in bytes).
     */
    private static final int CONTENT_HEADER_BYTE_ARRAY_SIZE = 4;

    /**
     * Size of the byte array representing the file chunk size (in bytes).
     */
    private static final int CHUNK_SIZE_BYTE_ARRAY_SIZE = 4;

    /**
     * The fileChunk stored in the content.
     */
    private byte[] fileChunk;

    /**
     * Creates a new file chunk content.
     *
     * @param data          The fileChunk to be stored on the content.
     * @param fileChunkSize The size of the file chunk to be stored on the content.
     */
    @SuppressWarnings("unused")
    public FileChunkContent(byte[] data, int fileChunkSize) {
        if (fileChunkSize != data.length) {
            throw new IllegalArgumentException("The file chunk specified does not match the file chunk length.");
        }
        this.fileChunk = data;
    }

    /**
     * Returns the file chunk stored in the content.
     *
     * @return The file chunk stored in the content.
     */
    public byte[] getFileChunk() {
        return fileChunk;
    }

    /**
     * Creates a file chunk content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public FileChunkContent(byte[] bytes) {

        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file chunk has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + CONTENT_HEADER_BYTE_ARRAY_SIZE);
        int contentHeader = ByteBuffer.wrap(byteArraySlice).getInt();

        if (contentHeader != CONTENT_HEADER_CODE) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file chunk code (" + Integer.toHexString(CONTENT_HEADER_CODE) + ").");
        }

        byteArrayCounter += CONTENT_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + CHUNK_SIZE_BYTE_ARRAY_SIZE);
        int chunkSize = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());

        if (bytes.length != (MINIMUM_CONTENT_SIZE + chunkSize)) {
            throw new IllegalArgumentException("The chunk size informed on byte array is incorrect. Chunk size indicated: " + chunkSize + " byte(s). Actual chunk size: " + (bytes.length - MINIMUM_CONTENT_SIZE) + ".");
        }
        byteArrayCounter += CHUNK_SIZE_BYTE_ARRAY_SIZE;

        this.fileChunk = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + chunkSize);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] contentHeaderBytes = ByteBuffer.allocate(CONTENT_HEADER_BYTE_ARRAY_SIZE).putInt(CONTENT_HEADER_CODE).array();
        byte[] chunkSizeBytes = ByteBuffer.allocate(CHUNK_SIZE_BYTE_ARRAY_SIZE).putInt(Integer.reverseBytes(this.fileChunk.length)).array();

        int totalByteArrayLength = contentHeaderBytes.length + chunkSizeBytes.length + fileChunk.length;
        byte[] byteArray = new byte[totalByteArrayLength];
        int byteArrayCounter = 0;

        System.arraycopy(contentHeaderBytes, 0, byteArray, byteArrayCounter, contentHeaderBytes.length);
        byteArrayCounter += contentHeaderBytes.length;

        System.arraycopy(chunkSizeBytes, 0, byteArray, byteArrayCounter, chunkSizeBytes.length);
        byteArrayCounter += chunkSizeBytes.length;

        System.arraycopy(this.fileChunk, 0, byteArray, byteArrayCounter, this.fileChunk.length);

        return byteArray;
    }
}
