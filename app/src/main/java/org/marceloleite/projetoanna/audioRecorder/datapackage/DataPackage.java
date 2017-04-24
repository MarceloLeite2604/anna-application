package org.marceloleite.projetoanna.audioRecorder.datapackage;

import org.marceloleite.projetoanna.audioRecorder.datapackage.content.CommandResultContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.Content;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.ErrorContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.FileChunkContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.FileHeaderContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.FileTrailerContent;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * A package of data exchanged between the application and the recorder.
 */

public class DataPackage {

    /**
     * Minimum acceptable package size (in bytes).
     */
    private static final int MINIMUM_PACKAGE_SIZE = 16;

    /**
     * Code to identify a package header.
     */
    private static final int PACKAGE_HEADER = 0x427103f0;

    /**
     * Size of a package header (in bytes).
     */
    private static final int PACKAGE_HEADER_BYTE_ARRAY_SIZE = 4;

    /**
     * Size of a package id (in bytes).
     */
    private static final int PACKAGE_ID_BYTE_ARRAY_SIZE = 4;

    /**
     * Size of a package type code (in bytes).
     */
    private static final int TYPE_CODE_BYTE_ARRAY_SIZE = 4;

    /**
     * Code to identify a package trailer.
     */
    private static final int PACKAGE_TRAILER = 0x04c22892;

    /**
     * Size of a package trailer (in bytes).
     */
    private static final int PACKAGE_TRAILER_BYTE_ARRAY_SIZE = 4;

    /**
     * The package identification.
     */
    private int id;

    /**
     * The package type.
     */
    private PackageType packageType;

    /**
     * The package content.
     */
    Content content;

    /**
     * Creates a new package.
     *
     * @param packageType The type of package to be created..
     * @param content     The content to be stored on the package.
     */
    public DataPackage(PackageType packageType, Content content) {
        Random random = new Random();
        this.id = random.nextInt();
        this.packageType = packageType;
        this.content = content;
    }

    /**
     * Returns the package identification.
     *
     * @return The package identification.
     */
    public int getId() {
        return id;
    }

    /**
     * Returna the package type.
     *
     * @return The package type.
     */
    public PackageType getPackageType() {
        return packageType;
    }

    /**
     * Returns the package content.
     *
     * @return The package content.
     */
    public Content getContent() {
        return content;
    }

    /**
     * Creates a package from a byte array.
     *
     * @param bytes The byte array to create te package from.
     */
    public DataPackage(byte[] bytes) {

        if (bytes.length < MINIMUM_PACKAGE_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. A package has at least " + MINIMUM_PACKAGE_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, PACKAGE_HEADER_BYTE_ARRAY_SIZE);
        int packageHeader = ByteBuffer.wrap(byteArraySlice).getInt();

        if (packageHeader != PACKAGE_HEADER) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(packageHeader) + "\" is different from a package header code (" + Integer.toHexString(PACKAGE_HEADER) + ").");
        }

        byteArrayCounter += PACKAGE_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + PACKAGE_ID_BYTE_ARRAY_SIZE);
        this.id = ByteBuffer.wrap(byteArraySlice).getInt();

        byteArrayCounter += PACKAGE_ID_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + TYPE_CODE_BYTE_ARRAY_SIZE);
        int typeCodeFromByteArray = ByteBuffer.wrap(byteArraySlice).getInt();

        this.packageType = PackageType.getTypeCode(typeCodeFromByteArray);
        byteArrayCounter += TYPE_CODE_BYTE_ARRAY_SIZE;

        int contentSize = bytes.length - MINIMUM_PACKAGE_SIZE;

        if (contentSize > 0) {
            byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + contentSize);
            this.content = createContent(this.packageType, byteArraySlice);
        }

        byteArrayCounter += contentSize;
        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, byteArrayCounter + PACKAGE_TRAILER_BYTE_ARRAY_SIZE);
        int packageTrailer = ByteBuffer.wrap(byteArraySlice).getInt();

        if (packageTrailer != PACKAGE_TRAILER) {
            throw new IllegalArgumentException("The byte array trailer \"" + Integer.toHexString(packageTrailer) + "\" is different from a package trailer code (" + Integer.toHexString(PACKAGE_TRAILER) + ").");
        }
    }

    /**
     * Creates a package content with the byte array content according to the type code informed.
     *
     * @param packageType The package type which indicates the content to be created.
     * @param bytes       The byte array to create the content from.
     * @return A package content with the byte array information on it.
     */
    private Content createContent(PackageType packageType, byte[] bytes) {
        Content content;
        switch (packageType) {
            case COMMAND_RESULT:
                content = new CommandResultContent(bytes);
                break;
            case CONFIRMATION:
                content = new ConfirmationContent(bytes);
                break;
            case ERROR:
                content = new ErrorContent(bytes);
                break;
            case FILE_CHUNK:
                content = new FileChunkContent(bytes);
                break;
            case FILE_HEADER:
                content = new FileHeaderContent(bytes);
                break;
            case FILE_TRAILER:
                content = new FileTrailerContent(bytes);
                break;
            default:
                throw new IllegalArgumentException("DataPackage type \"" + Integer.toHexString(packageType.getCode()) + "\" does not have a content, but a content of " + bytes.length + " bytes(s) was found.");
        }

        return content;
    }

    /**
     * Converts the package information to a byte array.
     *
     * @return A byte array with the package information.
     */
    public byte[] convertToBytes() {
        byte[] packageHeaderBytes = ByteBuffer.allocate(PACKAGE_HEADER_BYTE_ARRAY_SIZE).putInt(PACKAGE_HEADER).array();

        byte[] packageIdBytes = ByteBuffer.allocate(PACKAGE_ID_BYTE_ARRAY_SIZE).putInt(this.id).array();

        byte[] typeCodeBytes = ByteBuffer.allocate(TYPE_CODE_BYTE_ARRAY_SIZE).putInt(this.packageType.getCode()).array();

        byte[] contentByte = null;
        if (content != null) {
            contentByte = content.convertToBytes();
        }

        byte[] packageTrailerBytes = ByteBuffer.allocate(PACKAGE_TRAILER_BYTE_ARRAY_SIZE).putInt(PACKAGE_TRAILER).array();

        int totalByteArraySize = packageHeaderBytes.length + packageIdBytes.length + typeCodeBytes.length + packageTrailerBytes.length;

        if (contentByte != null) {
            totalByteArraySize += contentByte.length;
        }

        int packageBytesCounter = 0;

        byte[] packageBytes = new byte[totalByteArraySize];

        System.arraycopy(packageHeaderBytes, 0, packageBytes, packageBytesCounter, packageHeaderBytes.length);
        packageBytesCounter += packageHeaderBytes.length;

        System.arraycopy(packageIdBytes, 0, packageBytes, packageBytesCounter, packageIdBytes.length);
        packageBytesCounter += packageIdBytes.length;

        System.arraycopy(typeCodeBytes, 0, packageBytes, packageBytesCounter, typeCodeBytes.length);
        packageBytesCounter += typeCodeBytes.length;

        if (contentByte != null) {
            System.arraycopy(contentByte, 0, packageBytes, packageBytesCounter, contentByte.length);
            packageBytesCounter += contentByte.length;
        }

        System.arraycopy(packageTrailerBytes, 0, packageBytes, packageBytesCounter, packageTrailerBytes.length);

        return packageBytes;
    }
}
