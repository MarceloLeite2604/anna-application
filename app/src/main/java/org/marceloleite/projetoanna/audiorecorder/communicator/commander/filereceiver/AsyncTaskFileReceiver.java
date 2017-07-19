package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import android.content.Context;
import android.os.AsyncTask;

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
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressMonitorAlertDialog;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Receives a file from audio recorder.
 */
public class AsyncTaskFileReceiver extends AsyncTask<FileReceiverParameters, ProgressReport, ReceiveFileResult> {

    private static final String INITIAL_MESSAGE = "Receiving audio file";

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskFileReceiver.class.getSimpleName();

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
     * THe size of the file received from audio recorder.
     */
    private int fileSize;

    /**
     * The application context which shall be used to create the file received.
     */
    private Context context;

    /**
     *
     */
    private ProgressMonitorAlertDialog progressMonitorAlertDialog;

    /**
     * Constructor.
     */
    public AsyncTaskFileReceiver() {
        this.fileSize = 0;
    }

    /**
     * Receives the file.
     *
     * @return {@link GenericReturnCodes#SUCCESS} if file was received successfully. {@link GenericReturnCodes#GENERIC_ERROR} otherwise.
     */
    private ReceiveFileResult receiveFile(FileReceiverParameters fileReceiverParameters) {
        Log.d(LOG_TAG, "receiveFile (54): Receiving file.");

        this.context = fileReceiverParameters.getContext();
        this.senderReceiver = fileReceiverParameters.getSenderReceiver();
        this.progressMonitorAlertDialog = fileReceiverParameters.getProgressMonitorAlertDialog();

        File receivedFile;
        ReceiveFileResult receiveFileResult;

        if (receiveFileHeader() == GenericReturnCodes.SUCCESS) {
            ReceiveFileResult receiveFileContentResult = receiveFileContent();
            if (receiveFileContentResult.getReturnCode() == GenericReturnCodes.SUCCESS) {
                receivedFile = receiveFileContentResult.getFileReceived();
                if (receiveFileTrailer() == GenericReturnCodes.SUCCESS) {
                    Log.d(LOG_TAG, "receiveFile (59): File stored on \"" + receivedFile.getAbsolutePath() + "\".");
                    receiveFileResult = new ReceiveFileResult(GenericReturnCodes.SUCCESS, receivedFile);
                } else {
                    receiveFileResult = new ReceiveFileResult(GenericReturnCodes.GENERIC_ERROR, null);
                }
            } else {
                receiveFileResult = new ReceiveFileResult(GenericReturnCodes.GENERIC_ERROR, null);
            }
        } else {
            receiveFileResult = new ReceiveFileResult(GenericReturnCodes.GENERIC_ERROR, null);
        }
        return receiveFileResult;
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
    private File createFile() {
        //return FileUtils.createFile(context, FileType.AUDIO_MP3_FILE);
        return FileUtils.createTemporaryFile(context, FileType.AUDIO_MP3_FILE);
    }

    /**
     * Receives the file content from audio recorder.
     *
     * @return A {@link ReceiveFileResult} object with the status code of the execution and the file
     * received. If file was received successfully the code returned will be
     * {@link GenericReturnCodes#SUCCESS} and the file will be available. If an error occurred the
     * code returned will be {@link GenericReturnCodes#GENERIC_ERROR} and no file will be available.
     */
    private ReceiveFileResult receiveFileContent() {
        Log.d(LOG_TAG, "receiveFileContent (151): Receiving file content.");
        int totalBytesReceived = 0;
        boolean doneReceiveFileContent = false;
        double percentageConcluded;
        BufferedOutputStream bufferedOutputStream;

        File fileToReceiveContent = createFile();

        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileToReceiveContent));
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
                        Log.d(LOG_TAG, "receiveFileContent (201): Bytes received: " + totalBytesReceived + ". File size: " + this.fileSize);

                        percentageConcluded = (double) totalBytesReceived / (double) (this.fileSize);
                        publishProgress(new ProgressReport(INITIAL_MESSAGE, percentageConcluded));

                        if (totalBytesReceived >= this.fileSize) {
                            doneReceiveFileContent = true;
                        }
                    } else {
                        Log.d(LOG_TAG, "receiveFileContent (198): Package received is not a \"" + PackageType.FILE_CHUNK + "\". It is \"" + receivedDataPackage.getPackageType() + "\".");
                        throw new RuntimeException("The package received is not a file content. Package type: \"" + receivedDataPackage.getPackageType() + "\".");
                    }
                } else {
                    Log.e(LOG_TAG, "receiveFileContent (202): No package received.");
                    return new ReceiveFileResult(GenericReturnCodes.GENERIC_ERROR, null);
                }
            } else {
                Log.e(LOG_TAG, "receiveFileContent (203): Error while receiving a package.");
                return new ReceiveFileResult(GenericReturnCodes.GENERIC_ERROR, null);
            }
        }


        try {
            bufferedOutputStream.close();
        } catch (IOException ioException) {
            throw new RuntimeException("Exception thrown while closing the buffered output stream used to receive file from audio recorder.");
        }
        return new ReceiveFileResult(GenericReturnCodes.SUCCESS, fileToReceiveContent);
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

    @Override
    protected void onPreExecute() {
        ProgressReport progressReport = new ProgressReport(INITIAL_MESSAGE, 0.0);
        this.progressMonitorAlertDialog.updateProgressInformations(progressReport);
        this.progressMonitorAlertDialog.show();
    }

    @Override
    protected ReceiveFileResult doInBackground(FileReceiverParameters... fileReceiverParameterses) {
        return receiveFile(fileReceiverParameterses[0]);
    }

    @Override
    protected void onProgressUpdate(ProgressReport... progressReports) {
        ProgressReport progressReport = progressReports[0];
        this.progressMonitorAlertDialog.updateProgressInformations(progressReport);
    }
}
