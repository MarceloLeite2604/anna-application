package org.marceloleite.projetoanna.audioRecorder.operator.filereceiver;

import android.content.Context;

import org.marceloleite.projetoanna.audioRecorder.datapackage.DataPackage;
import org.marceloleite.projetoanna.audioRecorder.datapackage.PackageType;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.FileChunkContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.FileHeaderContent;
import org.marceloleite.projetoanna.audioRecorder.communication.Communication;
import org.marceloleite.projetoanna.audioRecorder.communication.CommunicationReturnCodes;
import org.marceloleite.projetoanna.audioRecorder.communication.ReceivePackageResult;
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

        try {
            DataPackage dataPackage = communication.receivePackage();

            if (dataPackage != null) {
                if (dataPackage.getPackageType() == PackageType.FILE_HEADER) {
                    FileHeaderContent fileHeaderContent = (FileHeaderContent) dataPackage.getContent();
                    this.fileSize = fileHeaderContent.getFileSize();
                    createFile(fileHeaderContent.getFileName());
                } else {
                    throw new FileReceiverException("The package received is not a file header. Package type: \"" + dataPackage.getPackageType().getTitle() + "\".");
                }
            } else {
                throw new FileReceiverException("No package received.");
            }

        } catch (CommunicationException communicationException) {
            throw new FileReceiverException("Error while receiving file header.", communicationException);
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
            try {
                DataPackage dataPackage = communication.receivePackage();

                if (dataPackage != null) {
                    if (dataPackage.getPackageType() == PackageType.FILE_CHUNK) {
                        FileChunkContent fileChunkContent = (FileChunkContent) dataPackage.getContent();
                        chunkSize = fileChunkContent.getFileChunk().length;
                        chunkData = fileChunkContent.getFileChunk();

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
                        throw new FileReceiverException("The package received is not a file content. Package type: \"" + dataPackage.getPackageType().getTitle() + "\".");
                    }
                } else {
                    throw new FileReceiverException("No package received.");
                }
            } catch (CommunicationException communicationException) {
                throw new FileReceiverException("Error while receiving file content.", communicationException);
            }
        }
    }

    private void receiveFileTrailer() throws FileReceiverException {
        try {
            DataPackage dataPackage = communication.receivePackage();
            if (dataPackage != null) {
                if (dataPackage.getPackageType() != PackageType.FILE_TRAILER) {
                    throw new FileReceiverException("The package received is not a file trailer. Package type: \"" + dataPackage.getPackageType().getTitle() + "\".");
                }
            } else {
                throw new FileReceiverException("No package received.");
            }
        } catch (CommunicationException communicationException) {
            throw new FileReceiverException("Error while receiving file trailer.", communicationException);
        }
    }
}
