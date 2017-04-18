package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class SendFileChunkContent extends Content {

    private static final int CONTENT_HEADER = 0x43ece390;

    private static final int MINIMUM_CONTENT_SIZE = 8;

    private static final int CONTENT_HEADER_BYTE_ARRAY_SIZE = 4;

    private static final int CHUNK_SIZE_BYTE_ARRAY_SIZE = 4;

    private int chunkSize;

    byte[] chunkData;

    public SendFileChunkContent(int chunkSize, byte[] chunkData) {
        this.chunkSize = chunkSize;
        this.chunkData = chunkData;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public void setChunkData(byte[] chunkData) {
        this.chunkData = chunkData;
    }

    public SendFileChunkContent(byte[] bytes) {

        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file chunk has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, CONTENT_HEADER_BYTE_ARRAY_SIZE);
        int contentHeader = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (contentHeader != CONTENT_HEADER) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file chunk code (" + Integer.toHexString(CONTENT_HEADER) + ").");
        }

        byteArrayCounter += CONTENT_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, CHUNK_SIZE_BYTE_ARRAY_SIZE);
        this.chunkSize = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (bytes.length != (MINIMUM_CONTENT_SIZE + this.chunkSize)) {
            throw new IllegalArgumentException("The chunk size informed on byte array is incorrect. Chunk size indicated: " + this.chunkSize + " byte(s). Actual chunk size: " + (bytes.length - MINIMUM_CONTENT_SIZE) + ".");
        }
        byteArrayCounter += CHUNK_SIZE_BYTE_ARRAY_SIZE;

        this.chunkData = Arrays.copyOfRange(bytes, byteArrayCounter, this.chunkSize);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] contentHeaderBytes = ByteBuffer.allocate(CONTENT_HEADER_BYTE_ARRAY_SIZE).putInt(CONTENT_HEADER).array();
        byte[] chunkSizeBytes = ByteBuffer.allocate(CHUNK_SIZE_BYTE_ARRAY_SIZE).putInt(this.chunkSize).array();

        int totalByteArrayLength = contentHeaderBytes.length + chunkSizeBytes.length + chunkData.length;
        byte[] byteArray = new byte[totalByteArrayLength];
        int byteArrayCounter = 0;

        System.arraycopy(contentHeaderBytes, 0, byteArray, byteArrayCounter, contentHeaderBytes.length);
        byteArrayCounter += contentHeaderBytes.length;

        System.arraycopy(chunkSizeBytes, 0, byteArray, byteArrayCounter, chunkSizeBytes.length);
        byteArrayCounter += chunkSizeBytes.length;

        System.arraycopy(this.chunkData, 0, byteArray, byteArrayCounter, this.chunkData.length);

        return byteArray;
    }
}
