package org.marceloleite.projetoanna.audiorecorder.commander.filereceiver;

import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.FileChunkContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.FileHeaderContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class FileReceiver {

    private static final String LOG_TAG = FileReceiver.class.getSimpleName();

    private SenderReceiver senderReceiver;

    private File file;

    private int fileSize;

    public FileReceiver(SenderReceiver senderReceiver) {
        this.senderReceiver = senderReceiver;
        this.file = null;
        this.fileSize = 0;
    }

    public File getFile() {
        return file;
    }

    public void receiveFile() throws FileReceiverException {
        Log.d(LOG_TAG, "receiveFile, 49: Receiving file.");
        receiveFileHeader();
        receiveFileContent();
        receiveFileTrailer();

        Log.d(LOG_TAG, "receiveFile, 55: File stored on \"" + file.getAbsolutePath() + "\".");
    }

    private void receiveFileHeader() throws FileReceiverException {
        Log.d(LOG_TAG, "receiveFileHeader, 57: Receiving file header.");

        try {
            DataPackage dataPackage = senderReceiver.receivePackage();

            if (dataPackage != null) {
                if (dataPackage.getPackageType() == PackageType.FILE_HEADER) {
                    FileHeaderContent fileHeaderContent = (FileHeaderContent) dataPackage.getContent();
                    this.fileSize = fileHeaderContent.getFileSize();
                    createFile(fileHeaderContent.getFileName());
                } else {
                    throw new FileReceiverException("The package received is not a file header. Package type: \"" + dataPackage.getPackageType() + "\".");
                }
            } else {
                throw new FileReceiverException("No package received.");
            }

        } catch (CommunicationException communicationException) {
            throw new FileReceiverException("Error while receiving file header.", communicationException);
        }
    }

    private void createFile(String fileName) throws FileReceiverException {
        /* Log.d(LOG_TAG, "createFile, 80: Creating file \"" + fileName + "\".");
        int suffixDividerIndex = fileName.lastIndexOf(".");
        String preffix = fileName.substring(0, suffixDividerIndex);
        String suffix = fileName.substring(suffixDividerIndex + 1, fileName.length());
        File cacheDir = context.getCacheDir();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            this.file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC), fileName);
        } else {
            Log.d(LOG_TAG, "createFile, 94: The external storage is not available to write file.");
            throw new FileReceiverException("The external storage is not available to write file.");
        }*/
        try {
            this.file = FileUtils.createFile(FileType.AUDIO_MP3_FILE);
        } catch (IOException ioException) {
            Log.d(LOG_TAG, "createFile, 102: Error creating audio file.", ioException);
        }
    }

    private void receiveFileContent() throws FileReceiverException {
        Log.d(LOG_TAG, "receiveFileContent, 92: Receiving file content.");
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
            Log.d(LOG_TAG, "receiveFileContent, 107: Total of bytes received: " + totalBytesReceived + "/" + this.fileSize + ".");
            try {
                DataPackage dataPackage = senderReceiver.receivePackage();

                if (dataPackage != null) {
                    Log.d(LOG_TAG, "receiveFileContent, 112: Package received.");
                    if (dataPackage.getPackageType() == PackageType.FILE_CHUNK) {
                        Log.d(LOG_TAG, "receiveFileContent, 114: Package is a file chunk.");
                        FileChunkContent fileChunkContent = (FileChunkContent) dataPackage.getContent();
                        chunkSize = fileChunkContent.getFileChunk().length;
                        chunkData = fileChunkContent.getFileChunk();

                        try {
                            bufferedOutputStream.write(chunkData);
                            Log.d(LOG_TAG, "receiveFileContent, 121: Chunk of data written on file.");
                        } catch (IOException ioException) {
                            throw new FileReceiverException("Error while writing content on temporary file.", ioException);
                        }

                        totalBytesReceived += chunkSize;

                        if (totalBytesReceived >= this.fileSize) {
                            doneReceiveFileContent = true;
                        }
                    } else {
                        Log.d(LOG_TAG, "receiveFileContent, 128: Package received is not a \"" + PackageType.FILE_CHUNK + "\". It is \"" + dataPackage.getPackageType() + "\".");
                        throw new FileReceiverException("The package received is not a file content. Package type: \"" + dataPackage.getPackageType() + "\".");
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
        Log.d(LOG_TAG, "receiveFileTrailer, 139: Receiving file trailer.");
        try {
            DataPackage dataPackage = senderReceiver.receivePackage();
            if (dataPackage != null) {
                Log.d(LOG_TAG, "receiveFileTrailer, 149: Received a package.");
                if (dataPackage.getPackageType() != PackageType.FILE_TRAILER) {
                    Log.d(LOG_TAG, "receiveFileTrailer, 151: Package received is not a \"" + PackageType.FILE_TRAILER + "\". It is \"" + dataPackage.getPackageType() + "\".");
                    throw new FileReceiverException("The package received is not a file trailer. Package type: \"" + dataPackage.getPackageType() + "\".");
                }
            } else {
                Log.d(LOG_TAG, "receiveFileTrailer, 155: No package received.");
                throw new FileReceiverException("No package received.");
            }
        } catch (CommunicationException communicationException) {
            Log.d(LOG_TAG, "receiveFileTrailer, 159: Error while receiving file trailer.");
            throw new FileReceiverException("Error while receiving file trailer.", communicationException);
        }
    }
}
