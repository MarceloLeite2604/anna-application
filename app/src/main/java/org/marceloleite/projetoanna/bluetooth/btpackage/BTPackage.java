package org.marceloleite.projetoanna.bluetooth.btpackage;

import org.marceloleite.projetoanna.bluetooth.btpackage.content.CommandResultContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.Content;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.ErrorContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.SendFileChunkContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.SendFileHeaderContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.SendFileTrailerContent;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class BTPackage {

    private static final int MINIMUM_PACKAGE_SIZE = 16;

    private static final int PACKAGE_HEADER = 0x427103f0;

    private static final int PACKAGE_HEADER_BYTE_ARRAY_SIZE = 4;

    private static final int PACKAGE_ID_BYTE_ARRAY_SIZE = 4;

    private static final int TYPE_CODE_BYTE_ARRAY_SIZE = 4;

    private static final int PACKAGE_TRAILER = 0x04c22892;

    private static final int PACKAGE_TRAILER_BYTE_ARRAY_SIZE = 4;

    private int id;

    private TypeCode typeCode;

    Content content;

    public BTPackage(TypeCode typeCode, Content content) {
        Random random = new Random();
        this.id = random.nextInt();
        this.typeCode = typeCode;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public TypeCode getTypeCode() {
        return typeCode;
    }

    public Content getContent() {
        return content;
    }

    public BTPackage(byte[] bytes) {

        if (bytes.length < MINIMUM_PACKAGE_SIZE) {
            throw new IllegalArgumentException("The byte array informed for constructor has " + bytes.length + " bytes. A package has at least " + MINIMUM_PACKAGE_SIZE + " byte(s).");
        }

        int byteArrayCounter = 0;

        byte[] byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, PACKAGE_HEADER_BYTE_ARRAY_SIZE);
        int packageHeader = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (packageHeader != PACKAGE_HEADER) {
            throw new IllegalArgumentException("The byte array header \"" + Integer.toHexString(packageHeader) + "\" is different from a package header code (" + Integer.toHexString(PACKAGE_HEADER) + ").");
        }

        byteArrayCounter += PACKAGE_HEADER_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, PACKAGE_ID_BYTE_ARRAY_SIZE);
        this.id = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        byteArrayCounter += PACKAGE_ID_BYTE_ARRAY_SIZE;

        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, TYPE_CODE_BYTE_ARRAY_SIZE);
        int typeCodeFromByteArray = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        this.typeCode = TypeCode.getTypeCode(typeCodeFromByteArray);
        byteArrayCounter += TYPE_CODE_BYTE_ARRAY_SIZE;

        int contentSize = bytes.length - MINIMUM_PACKAGE_SIZE;

        if (contentSize > 0) {
            byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, contentSize);
            this.content = createContent(this.typeCode, byteArraySlice);
        }

        byteArrayCounter += contentSize;
        byteArraySlice = Arrays.copyOfRange(bytes, byteArrayCounter, PACKAGE_TRAILER_BYTE_ARRAY_SIZE);
        int packageTrailer = java.nio.ByteBuffer.wrap(byteArraySlice).getInt();

        if (packageTrailer != PACKAGE_TRAILER) {
            throw new IllegalArgumentException("The byte array trailer \"" + Integer.toHexString(packageTrailer) + "\" is different from a package trailer code (" + Integer.toHexString(PACKAGE_TRAILER) + ").");
        }
    }

    private Content createContent(TypeCode typeCode, byte[] bytes) {
        Content content;
        switch (typeCode) {
            case COMMAND_RESULT:
                content = new CommandResultContent(bytes);
                break;
            case CONFIRMATION:
                content = new ConfirmationContent(bytes);
                break;
            case ERROR:
                content = new ErrorContent(bytes);
                break;
            case SEND_FILE_CHUNK:
                content = new SendFileChunkContent(bytes);
                break;
            case SEND_FILE_HEADER:
                content = new SendFileHeaderContent(bytes);
                break;
            case SEND_FILE_TRAILER:
                content = new SendFileTrailerContent(bytes);
                break;
            default:
                throw new IllegalArgumentException("BTPackage type \"" + Integer.toHexString(typeCode.getCode()) + "\" does not have a content, but a content of " + bytes.length + " bytes(s) was found.");
        }

        return content;
    }

    public byte[] convertToBytes() {
        byte[] packageHeaderBytes = ByteBuffer.allocate(PACKAGE_HEADER_BYTE_ARRAY_SIZE).putInt(PACKAGE_HEADER).array();

        byte[] packageIdBytes = ByteBuffer.allocate(PACKAGE_ID_BYTE_ARRAY_SIZE).putInt(this.id).array();

        byte[] typeCodeBytes = ByteBuffer.allocate(TYPE_CODE_BYTE_ARRAY_SIZE).putInt(this.typeCode.getCode()).array();

        byte[] contentByte = content.convertToBytes();

        byte[] packageTrailerBytes = ByteBuffer.allocate(PACKAGE_TRAILER_BYTE_ARRAY_SIZE).putInt(PACKAGE_TRAILER).array();

        int totalByteArraySize = packageHeaderBytes.length + packageIdBytes.length + typeCodeBytes.length + contentByte.length + packageTrailerBytes.length;
        int packageBytesCounter = 0;

        byte[] packageBytes = new byte[totalByteArraySize];

        System.arraycopy(packageHeaderBytes, 0, packageBytes, packageBytesCounter, packageHeaderBytes.length);
        packageBytesCounter += packageHeaderBytes.length;

        System.arraycopy(packageIdBytes, 0, packageBytes, packageBytesCounter, packageIdBytes.length);
        packageBytesCounter += packageIdBytes.length;

        System.arraycopy(typeCodeBytes, 0, packageBytes, packageBytesCounter, typeCodeBytes.length);
        packageBytesCounter += typeCodeBytes.length;

        System.arraycopy(contentByte, 0, packageBytes, packageBytesCounter, contentByte.length);
        packageBytesCounter += contentByte.length;

        System.arraycopy(packageTrailerBytes, 0, packageBytes, packageBytesCounter, packageTrailerBytes.length);

        return packageBytes;
    }
}
