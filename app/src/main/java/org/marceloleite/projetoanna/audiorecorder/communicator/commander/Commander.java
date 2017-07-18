package org.marceloleite.projetoanna.audiorecorder.communicator.commander;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver.AsyncTaskFileReceiver;
import org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver.FileReceiverParameters;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content.CommandResultContent;
import org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver.ReceivePackageResult;
import org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver.RequestLatestAudioFileResult;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.progressmonitor.AsyncTaskMonitorProgress;

import static org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.PackageType.COMMAND_RESULT;

/**
 * Sends commands and receives files from audio recorder.
 */
public class Commander {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Commander.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object to send and receive content from the audio recorder.
     */
    private SenderReceiver senderReceiver;

    /**
     * The application appCompatActivity which the received files will be stored.
     */
    private AppCompatActivity appCompatActivity;

    /**
     * Constructor.
     *
     * @param appCompatActivity The application which the received files will be stored.
     * @param bluetoothSocket   The bluetooth socket communication with audio recorder.
     */
    public Commander(AppCompatActivity appCompatActivity, BluetoothSocket bluetoothSocket) {
        this.appCompatActivity = appCompatActivity;
        this.senderReceiver = new SenderReceiver(bluetoothSocket);
    }

    /**
     * Requests for audio recorder to start recording.
     *
     * @return The result received from the command execution.
     */
    public CommandResult startRecord() {
        return sendPackageAndWaitForResult(PackageType.START_RECORD);
    }

    /**
     * Requests for audio recorder to stop recording.
     *
     * @return The result received from the command execution.
     */
    public CommandResult stopRecord() {
        return sendPackageAndWaitForResult(PackageType.STOP_RECORD);
    }

    /**
     * Requests the disconnection from the audio recorder.
     *
     * @return The result received from the disconnection command.
     */
    public CommandResult disconnect() {
        DataPackage disconnectDataPackage = new DataPackage(PackageType.DISCONNECT, null);
        senderReceiver.sendPackage(disconnectDataPackage);
        return new CommandResult(0, 0);
    }

    /**
     * Returns the communication delay measured.
     *
     * @return The communication delay measured.
     */
    public long getCommunicationDelay() {
        return senderReceiver.getCommunicationDelay();
    }

    /**
     * Sends a package to audio recorder and awaits for a result.
     *
     * @param packageType The package to be sent.
     * @return The result received from the package.
     */
    private CommandResult sendPackageAndWaitForResult(PackageType packageType) {
        Log.d(LOG_TAG, "sendPackageAndWaitForResult (103): Sending command \"" + packageType + "\".");
        DataPackage dataPackage = new DataPackage(packageType, null);
        CommandResult commandResult;

        senderReceiver.sendPackage(dataPackage);

        ReceivePackageResult receivePackageResult;
        Log.d(LOG_TAG, "sendPackageAndWaitForResult (110): Waiting for command \"" + packageType + "\" result.");
        receivePackageResult = senderReceiver.receivePackage();

            /* If the package reception was successfully done. */
        if (receivePackageResult.getReturnCode() == AudioRecorderReturnCodes.SUCCESS) {
            DataPackage receivedDataPackage = receivePackageResult.getDataPackage();

                /* If a package was received from audio recorder. */
            if (receivedDataPackage != null) {
                Log.d(LOG_TAG, "sendPackageAndWaitForResult (119): Received a package.");

                switch (receivedDataPackage.getPackageType()) {
                        /* If the package received was a command result. */
                    case COMMAND_RESULT:
                        CommandResultContent commandResultContent = (CommandResultContent) receivedDataPackage.getContent();
                        switch (commandResultContent.getResultCode()) {
                            case GenericReturnCodes.SUCCESS:
                            case GenericReturnCodes.GENERIC_ERROR:
                                int resultCode = commandResultContent.getResultCode();
                                long executionDelay = commandResultContent.getExecutionDelayMicroseconds();
                                executionDelay += commandResultContent.getExecutionDelaySeconds() * 1000000;
                                commandResult = new CommandResult(resultCode, executionDelay);
                                break;
                            default:
                                throw new RuntimeException("Unknown return value received from device after send \"" + packageType + "\" package.");
                        }
                        break;
                    default:
                        Log.d(LOG_TAG, "sendPackageAndWaitForResult (101): The package received is not a \"" + COMMAND_RESULT + "\" package.");
                        throw new RuntimeException("Received a \"" + receivedDataPackage.getPackageType() + "\" package, when expecting a \"" + COMMAND_RESULT + "\" package.");
                }
            } else {
                Log.d(LOG_TAG, "sendPackageAndWaitForResult (105): Didn't received the \"" + packageType + "\" command result.");
                    /* TODO: Must inform that the command result was not received, and not throw an exception. */
                throw new RuntimeException("No package received.");
            }
        } else {
                /* TODO: Inform that an error occurred while receiving the command result. */
            throw new RuntimeException("An error occurred while receiving the package result.");
        }

        return commandResult;
    }

    /**
     * Requests for audio recorder the latest audio file.
     *
     * @return A {@link RequestLatestAudioFileResult} object with a return code and the audio file
     * received. If an audio file was received successfully, the status code returned will be
     * {@link GenericReturnCodes#SUCCESS} and the audio file will be available through
     * {@link RequestLatestAudioFileResult#getAudioFile()} method. If any error occur, the code
     * returned will be {@link GenericReturnCodes#GENERIC_ERROR} an no audio file will be available.
     */
    public RequestLatestAudioFileResult requestLatestAudioFile() {
        Log.d(LOG_TAG, "requestLatestAudioFile (117): Requesting latest audio file.");

        DataPackage requestAudioFilePackage = new DataPackage(PackageType.REQUEST_AUDIO_FILE, null);

        int requestAudioFilePackageResult = senderReceiver.sendPackage(requestAudioFilePackage);
        if (requestAudioFilePackageResult == AudioRecorderReturnCodes.SUCCESS) {
            AsyncTaskFileReceiver asyncTaskFileReceiver = new AsyncTaskFileReceiver();
            FileReceiverParameters fileReceiverParameters = new FileReceiverParameters(appCompatActivity, senderReceiver);
            asyncTaskFileReceiver.execute(fileReceiverParameters);
            AsyncTaskMonitorProgress asyncTaskMonitorProgress = new AsyncTaskMonitorProgress(appCompatActivity);
            asyncTaskMonitorProgress.execute(asyncTaskFileReceiver);
            while (asyncTaskFileReceiver.getStatus() != AsyncTask.Status.FINISHED) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Exception while waiting for file monitoring a progress.", e);
                }
            }

            return asyncTaskFileReceiver.getRequestLatestAudioFileResult();

        } else {
            return new RequestLatestAudioFileResult(GenericReturnCodes.GENERIC_ERROR, null);
        }
    }

    /**
     * Checks the connection with audio recorder.
     *
     * @return True if audio recorder is still connected. False otherwise.
     */
    public boolean checkConnection() {
        DataPackage checkConnectionDataPackage = new DataPackage(PackageType.CHECK_CONNECTION, null);

        int sendPackageResult = senderReceiver.sendPackage(checkConnectionDataPackage);
        return (sendPackageResult == AudioRecorderReturnCodes.SUCCESS);
    }

}
