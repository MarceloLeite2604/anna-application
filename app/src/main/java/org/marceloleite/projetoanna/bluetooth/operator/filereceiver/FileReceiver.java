package org.marceloleite.projetoanna.bluetooth.operator.filereceiver;

import android.content.Context;

import org.marceloleite.projetoanna.bluetooth.btpackage.BTPackage;
import org.marceloleite.projetoanna.bluetooth.btpackage.TypeCode;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.SendFileChunkContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.SendFileHeaderContent;
import org.marceloleite.projetoanna.bluetooth.communication.Communication;
import org.marceloleite.projetoanna.bluetooth.communication.CommunicationReturnCodes;
import org.marceloleite.projetoanna.bluetooth.communication.ReceivePackageResult;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class FileReceiver {

    private Context context;

    private Communication communication;

    private File file;
    private int fileSize;

    public FileReceiver(Context context, Communication communication) {
        this.context = context;
        this.communication = communication;
        this.file = null;
        this.fileSize = 0;
    }

    public String getFileName() {
        String fileName = null;
        if (file != null) {
            fileName = file.getName();
        }
        return fileName;
    }

    public void receiveFile() throws FileReceiverException {
        receiveFileHeader();
        receiveFileContent();
        receiveFileTrailer();
    }

    private void receiveFileHeader() throws FileReceiverException {

        ReceivePackageResult receivePackageResult;
        try {
            receivePackageResult = communication.receivePackage();
        } catch (CommunicationException communicationException) {
            throw new FileReceiverException("Error while receiving file header.", communicationException);
        }
        switch (receivePackageResult.getReturnValue()) {
            case CommunicationReturnCodes.SUCCESS:
                BTPackage receivedPackage = receivePackageResult.getBtPackage();
                if (receivedPackage.getTypeCode() == TypeCode.SEND_FILE_HEADER) {
                    SendFileHeaderContent sendFileHeaderContent = (SendFileHeaderContent) receivedPackage.getContent();
                    this.fileSize = sendFileHeaderContent.getFileSize();
                    createFile(sendFileHeaderContent.getFileName());
                } else {
                    throw new FileReceiverException("The package received is not a file header. Package type: \"" + receivedPackage.getTypeCode().getDescription() + "\".");
                }
                break;
            case CommunicationReturnCodes.NO_PACKAGE_RECEIVED:
                throw new FileReceiverException("No package received.");
            case CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION:
                throw new FileReceiverException("Could not send package confirmation.");
            default:
                throw new FileReceiverException("The function \"receivePackageResult\" returned an unknown value.");
        }
    }

    private void createFile(String fileName) throws FileReceiverException {
        String preffix = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length() - fileName.lastIndexOf(".") - 1);
        File cacheDir = context.getCacheDir();
        try {
            this.file = File.createTempFile(preffix, suffix, cacheDir);
        } catch (IOException ioException) {
            throw new FileReceiverException("Error while creating temporary file.", ioException);
        }
    }

    private void receiveFileContent() throws FileReceiverException {
        int totalBytesReceived = 0;
        int chunkSize;
        byte[] chunkData;
        boolean doneReceiveFileContent = false;
        BufferedOutputStream bufferedOutputStream;

        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.file));
        } catch (FileNotFoundException fileNotFoundException) {
            throw new FileReceiverException("Error opening temporary file to write content.", fileNotFoundException);
        }

        while (!doneReceiveFileContent) {
            ReceivePackageResult receivePackageResult = null;
            try {
                receivePackageResult = communication.receivePackage();
            } catch (CommunicationException communicationException) {
                throw new FileReceiverException("Error while receiving file content.", communicationException);
            }
            switch (receivePackageResult.getReturnValue()) {
                case CommunicationReturnCodes.SUCCESS:
                    BTPackage receivedPackage = receivePackageResult.getBtPackage();
                    if (receivedPackage.getTypeCode() == TypeCode.SEND_FILE_CHUNK) {
                        SendFileChunkContent sendFileChunkContent = (SendFileChunkContent) receivedPackage.getContent();
                        chunkSize = sendFileChunkContent.getChunkSize();
                        chunkData = sendFileChunkContent.getChunkData();

                        try {
                            bufferedOutputStream.write(chunkData);
                        } catch (IOException ioException) {
                            throw new FileReceiverException("Error while writing content on temporary file.", ioException);
                        }

                        totalBytesReceived += chunkSize;

                        if (totalBytesReceived >= this.fileSize) {
                            doneReceiveFileContent = true;
                        }
                    } else {
                        throw new FileReceiverException("The package received is not a file content. Package type: \"" + receivedPackage.getTypeCode().getDescription() + "\".");
                    }
                    break;
                case CommunicationReturnCodes.NO_PACKAGE_RECEIVED:
                    throw new FileReceiverException("No package received.");
                case CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION:
                    throw new FileReceiverException("Could not send package confirmation.");
                default:
                    throw new FileReceiverException("The function \"receivePackageResult\" returned an unknown value.");
            }
        }
    }

    private void receiveFileTrailer() throws FileReceiverException {
        ReceivePackageResult receivePackageResult;

        try {
            receivePackageResult = communication.receivePackage();
        } catch (CommunicationException communicationException) {
            throw new FileReceiverException("Error while receiving file trailer.", communicationException);
        }

        switch (receivePackageResult.getReturnValue()) {
            case CommunicationReturnCodes.SUCCESS:
                BTPackage receivedPackage = receivePackageResult.getBtPackage();
                if (receivedPackage.getTypeCode() == TypeCode.SEND_FILE_TRAILER) {
                } else {
                    throw new FileReceiverException("The package received is not a file trailer. Package type: \"" + receivedPackage.getTypeCode().getDescription() + "\".");
                }
                break;
            case CommunicationReturnCodes.NO_PACKAGE_RECEIVED:
                throw new FileReceiverException("No package received.");
            case CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION:
                throw new FileReceiverException("Could not send package confirmation.");
            default:
                throw new FileReceiverException("The function \"receivePackageResult\" returned an unknown value.");
        }
    }
}
