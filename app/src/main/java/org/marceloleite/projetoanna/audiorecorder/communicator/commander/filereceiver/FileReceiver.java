package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import android.content.Context;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content.FileChunkContent;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content.FileHeaderContent;
import org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver.ReceivePackageResult;
import org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Receives a file from audio recorder.
 */
public class FileReceiver {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = FileReceiver.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Sends and receives packages to the audio recorder.
     */
    private SenderReceiver senderReceiver;

    /**
     * The file received from audio recorder.
     */
    private File file;

    /**
     * THe size of the file received from audio recorder.
     */
    private int fileSize;

    /**
     * The application context which shall be used to create the file received.
     */
    private Context context;

    /**
     * Constructor.
     *
     * @param senderReceiver The object to send and receive packages from the audio recorder.
     */
    public FileReceiver(Context context, SenderReceiver senderReceiver) {
        this.context = context;
        this.senderReceiver = senderReceiver;
        this.file = null;
        this.fileSize = 0;
    }

    /**
     * Returns the file received from audio recorder.
     *
     * @return The file received from audio recorder.
     */
    public File getFile() {
        return file;
    }

    /**
     * Starts the file reception.
     */
    public void startFileReception() {
        Log.d(LOG_TAG, "startFileReception (54): Receiving file.");

        receiveFileHeader();
        receiveFileContent();
        receiveFileTrailer();

        Log.d(LOG_TAG, "startFileReception (59): File stored on \"" + file.getAbsolutePath() + "\".");
    }

    /**
     * Receives the file header.
     *
     * @return {@link GenericReturnCodes#SUCCESS} if the file header was received successfully.
     * {@link GenericReturnCodes#GENERIC_ERROR} otherwise.
     */
    private int receiveFileHeader() {
        Log.d(LOG_TAG, "receiveFileHeader (63): Receiving file header.");

        ReceivePackageResult receivePackageResult = senderReceiver.receivePackage();

        /* If the package reception was executed successfully. */
        if (receivePackageResult.getReturnCode() == AudioRecorderReturnCodes.SUCCESS) {

            DataPackage receivedDataPackage = receivePackageResult.getDataPackage();

            /* If a data package was received. */
            if (receivedDataPackage != null) {

                /* If the data package received is a file header package. */
                if (receivedDataPackage.getPackageType() == PackageType.FILE_HEADER) {
                    FileHeaderContent fileHeaderContent = (FileHeaderContent) receivedDataPackage.getContent();
                    this.fileSize = fileHeaderContent.getFileSize();
                    createFile();
                    return GenericReturnCodes.SUCCESS;
                } else {
                    Log.e(LOG_TAG, "receiveFileHeader (119): The package received is not a file header. Package type: \"" + receivedDataPackage.getPackageType() + "\".");
                    return GenericReturnCodes.GENERIC_ERROR;
                }
            } else {
                Log.e(LOG_TAG, "receiveFileHeader (128): No package received.");
                return GenericReturnCodes.GENERIC_ERROR;
            }
        } else {
            Log.e(LOG_TAG, "receiveFileHeader (132): Error while receiving package.");
            return GenericReturnCodes.GENERIC_ERROR;
        }
    }

    /**
     * Creates an empty file to receive the file content sent from audio recorder.
     */
    private void createFile() {
        this.file = FileUtils.createFile(context, FileType.AUDIO_MP3_FILE);
    }

    /**
     * Receives the file content from audio recorder.
     *
     * @return {@link GenericReturnCodes#SUCCESS} if the file content was received successfully.
     * {@link GenericReturnCodes#GENERIC_ERROR} otherwise.
     */
    private int receiveFileContent() {
        Log.d(LOG_TAG, "receiveFileContent (151): Receiving file content.");
        int totalBytesReceived = 0;
        boolean doneReceiveFileContent = false;
        BufferedOutputStream bufferedOutputStream;

        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.file));
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException("Error while creating a buffered output stream to write the received file content.", fileNotFoundException);
        }

        while (!doneReceiveFileContent) {
            Log.i(LOG_TAG, "receiveFileContent (123): Total of bytes received: " + totalBytesReceived + "/" + this.fileSize + ".");
            ReceivePackageResult receivePackageResult = senderReceiver.receivePackage();

            /* If the package reception was executed successfully. */
            if (receivePackageResult.getReturnCode() == AudioRecorderReturnCodes.SUCCESS) {

                DataPackage receivedDataPackage = receivePackageResult.getDataPackage();

                /* If a package was received. */
                if (receivedDataPackage != null) {
                    Log.d(LOG_TAG, "receiveFileContent (175): Package received.");

                    /* If the package received is a file chunk. */
                    if (receivedDataPackage.getPackageType() == PackageType.FILE_CHUNK) {
                        Log.d(LOG_TAG, "receiveFileContent (180): Package is a file chunk.");

                        FileChunkContent fileChunkContent = (FileChunkContent) receivedDataPackage.getContent();

                        /* Writes the chunk of data on file. */
                        try {
                            bufferedOutputStream.write(fileChunkContent.getFileChunk());
                            Log.d(LOG_TAG, "receiveFileContent (187): Chunk of data written on file.");
                        } catch (IOException ioException) {
                            throw new RuntimeException("Error while writing file content on buffered output stream.", ioException);
                        }

                        totalBytesReceived += fileChunkContent.getFileChunk().length;

                        if (totalBytesReceived >= this.fileSize) {
                            doneReceiveFileContent = true;
                        }
                    } else {
                        Log.d(LOG_TAG, "receiveFileContent (198): Package received is not a \"" + PackageType.FILE_CHUNK + "\". It is \"" + receivedDataPackage.getPackageType() + "\".");
                        throw new RuntimeException("The package received is not a file content. Package type: \"" + receivedDataPackage.getPackageType() + "\".");
                    }
                } else {
                    Log.e(LOG_TAG, "receiveFileContent (202): No package received.");
                    return GenericReturnCodes.GENERIC_ERROR;
                }
            } else {
                Log.e(LOG_TAG, "receiveFileContent (203): Error while receiving a package.");
                return GenericReturnCodes.GENERIC_ERROR;
            }
        }
        return GenericReturnCodes.SUCCESS;
    }

    /**
     * Receives the file trailer.
     *
     * @return {@link GenericReturnCodes#SUCCESS} if the file trailer was received successfully.
     * {@link GenericReturnCodes#GENERIC_ERROR} otherwise.
     */
    private int receiveFileTrailer() {
        Log.d(LOG_TAG, "receiveFileTrailer (160): Receiving file trailer.");
        ReceivePackageResult receivePackageResult = senderReceiver.receivePackage();

            /* If the package reception was executed successfully. */
        if (receivePackageResult.getReturnCode() == AudioRecorderReturnCodes.SUCCESS) {

            DataPackage receivedDataPackage = receivePackageResult.getDataPackage();

                /* If a package was received. */
            if (receivedDataPackage != null) {
                Log.d(LOG_TAG, "receiveFileContent (175): Package received.");

                    /* If the package received is a file trailer. */
                if (receivedDataPackage.getPackageType() == PackageType.FILE_TRAILER) {
                    return GenericReturnCodes.SUCCESS;
                } else {
                    Log.e(LOG_TAG, "receiveFileTrailer (166): Package received is not a \"" + PackageType.FILE_TRAILER + "\". It is \"" + receivedDataPackage.getPackageType() + "\".");
                    return GenericReturnCodes.GENERIC_ERROR;
                }
            } else {
                Log.e(LOG_TAG, "receiveFileTrailer (170): No package received.");
                return GenericReturnCodes.GENERIC_ERROR;
            }
        } else {
            Log.e(LOG_TAG, "receiveFileTrailer (241): Error while receiving a package.");
            return GenericReturnCodes.GENERIC_ERROR;
        }
    }
}
