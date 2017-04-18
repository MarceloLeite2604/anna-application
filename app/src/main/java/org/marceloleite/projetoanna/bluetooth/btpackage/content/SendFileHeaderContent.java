package org.marceloleite.projetoanna.bluetooth.btpackage.content;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class SendFileHeaderContent extends Content {

    private static final int CONTENT_HEADER = 0x47b24e67;

    private static final int MINIMUM_CONTENT_SIZE = 12;

    private static final int CONTENT_HEADER_BYTE_ARRAY_SIZE = 4;

    private static final int FILE_SIZE_BYTE_ARRAY_SIZE = 4;

    private static final int FILE_NAME_SIZE_BYTE_ARRAY_SIZE = 4;

    private int fileSize;

    private String fileName;

    public SendFileHeaderContent(int fileSize, String fileName) {
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SendFileHeaderContent(byte[] bytes) {

        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file header has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, CONTENT_HEADER_BYTE_ARRAY_SIZE);
        int contentHeader = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (contentHeader != CONTENT_HEADER) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file header code (" + Integer.toHexString(CONTENT_HEADER) + ").");
        }

        byteArrayCounter += CONTENT_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, FILE_SIZE_BYTE_ARRAY_SIZE);
        this.fileSize = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        byteArrayCounter += FILE_SIZE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, FILE_NAME_SIZE_BYTE_ARRAY_SIZE);
        int fileNameSize = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (bytes.length != (MINIMUM_CONTENT_SIZE + fileNameSize)) {
            throw new IllegalArgumentException("The file name size informed on byte array is incorrect. File name size indicated: " + fileNameSize + " byte(s). Actual chunk size: " + (bytes.length - MINIMUM_CONTENT_SIZE) + ".");
        }
        byteArrayCounter += FILE_NAME_SIZE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, fileNameSize);
        this.fileName = new String(byteArraySlice);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] contentHeaderBytes = ByteBuffer.allocate(CONTENT_HEADER_BYTE_ARRAY_SIZE).putInt(CONTENT_HEADER).array();
        byte[] fileSizeBytes = ByteBuffer.allocate(FILE_SIZE_BYTE_ARRAY_SIZE).putInt(this.fileSize).array();

        int fileNameSize = fileName.getBytes().length;
        byte[] fileNameSizeBytes = ByteBuffer.allocate(FILE_NAME_SIZE_BYTE_ARRAY_SIZE).putInt(fileNameSize).array();

        int totalByteArrayLength = contentHeaderBytes.length + fileSizeBytes.length + fileNameSizeBytes.length + fileNameSize;
        byte[] byteArray = new byte[totalByteArrayLength];
        int byteArrayCounter = 0;

        System.arraycopy(contentHeaderBytes, 0, byteArray, byteArrayCounter, contentHeaderBytes.length);
        byteArrayCounter += contentHeaderBytes.length;

        System.arraycopy(fileSizeBytes, 0, byteArray, byteArrayCounter, fileSizeBytes.length);
        byteArrayCounter += fileSizeBytes.length;

        System.arraycopy(fileNameSizeBytes, 0, byteArray, byteArrayCounter, fileNameSizeBytes.length);
        byteArrayCounter += fileNameSizeBytes.length;

        System.arraycopy(fileName.getBytes(), 0, byteArray, byteArrayCounter, fileNameSize);
        return byteArray;
    }
}
