package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

import org.marceloleite.projetoanna.utils.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The content of a file header package.
 */
public class FileHeaderContent extends Content {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = FileHeaderContent.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(FileHeaderContent.class);
    }

    /**
     * The code which identifies a file content content.
     */
    private static final int CONTENT_HEADER_CODE = 0x47b24e67;

    /**
     * Minimum acceptable content size (in bytes).
     */
    private static final int MINIMUM_CONTENT_SIZE = 12;

    /**
     * Size of the byte array representing the content header (in bytes).
     */
    private static final int CONTENT_HEADER_BYTE_ARRAY_SIZE = 4;

    /**
     * Size of the byte array representing the file size (in bytes).
     */
    private static final int FILE_SIZE_BYTE_ARRAY_SIZE = 4;

    private static final int FILE_NAME_SIZE_BYTE_ARRAY_SIZE = 4;

    private int fileSize;

    private String fileName;

    public FileHeaderContent(int fileSize, String fileName) {
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

    /**
     * Creates a file header content with the information stored on the byte array.
     *
     * @param bytes The byte array containing the information to be stored on the content.
     */
    public FileHeaderContent(byte[] bytes) {

        if (bytes.length < MINIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. The send file header has at least " + MINIMUM_CONTENT_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + CONTENT_HEADER_BYTE_ARRAY_SIZE);
        int contentHeader = ByteBuffer.wrap(byteArraySlice).getInt();

        if (contentHeader != CONTENT_HEADER_CODE) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(contentHeader) + "\" is different from a send file header code (" + Integer.toHexString(CONTENT_HEADER_CODE) + ").");
        }

        byteArrayCounter += CONTENT_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + FILE_SIZE_BYTE_ARRAY_SIZE);
        this.fileSize = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());

        byteArrayCounter += FILE_SIZE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + FILE_NAME_SIZE_BYTE_ARRAY_SIZE);
        int fileNameSize = Integer.reverseBytes(ByteBuffer.wrap(byteArraySlice).getInt());

        if (bytes.length != (MINIMUM_CONTENT_SIZE + fileNameSize)) {
            throw new IllegalArgumentException("The file name size informed on byte array is incorrect. File name size indicated: " + fileNameSize + " byte(s). Actual chunk size: " + (bytes.length - MINIMUM_CONTENT_SIZE) + ".");
        }
        byteArrayCounter += FILE_NAME_SIZE_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + fileNameSize);
        this.fileName = new String(byteArraySlice);
    }

    @Override
    public byte[] convertToBytes() {
        byte[] contentHeaderBytes = ByteBuffer.allocate(CONTENT_HEADER_BYTE_ARRAY_SIZE).putInt(CONTENT_HEADER_CODE).array();
        byte[] fileSizeBytes = ByteBuffer.allocate(FILE_SIZE_BYTE_ARRAY_SIZE).putInt(Integer.reverseBytes(this.fileSize)).array();

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
