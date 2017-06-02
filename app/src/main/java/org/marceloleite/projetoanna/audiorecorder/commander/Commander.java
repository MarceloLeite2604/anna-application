package org.marceloleite.projetoanna.audiorecorder.commander;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.CommandResultContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.audiorecorder.commander.filereceiver.FileReceiver;
import org.marceloleite.projetoanna.audiorecorder.commander.filereceiver.FileReceiverException;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;

import java.io.File;

/**
 * Created by Marcelo Leite on 18/04/2017.
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
        Log.addClassToLog(Commander.class);
    }

    private SenderReceiver senderReceiver;

    public Commander(BluetoothSocket bluetoothSocket) throws CommanderException {
        try {
            this.senderReceiver = new SenderReceiver(bluetoothSocket);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while creating Commander object.", communicationException);
        }
    }

    public CommandResult startRecord() throws CommanderException {
        return sendPackageAndWaitForResult(PackageType.START_RECORD);
    }

    public CommandResult stopRecord() throws CommanderException {
        return sendPackageAndWaitForResult(PackageType.STOP_RECORD);
    }

    public CommandResult disconnect() throws CommanderException {
        DataPackage disconnectDataPackage = new DataPackage(PackageType.DISCONNECT, null);
        try {
            senderReceiver.sendPackage(disconnectDataPackage);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while sending \"disconnect\" package.");
        }
        return new CommandResult(0, 0);
    }

    public long getCommunicationDelay() {
        return senderReceiver.getCommunicationDelay();
    }

    private CommandResult sendPackageAndWaitForResult(PackageType packageType) throws CommanderException {
        Log.d(Commander.class, LOG_TAG, "sendPackageAndWaitForResult (68): Sending command \"" + packageType + "\".");
        DataPackage dataPackage = new DataPackage(packageType, null);
        CommandResult commandResult;

        try {
            senderReceiver.sendPackage(dataPackage);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while sending \"" + packageType + "\" package.", communicationException);
        }

        DataPackage receivedDataPackage;
        try {
            Log.d(Commander.class, LOG_TAG, "sendPackageAndWaitForResult (80): Waiting for command \"" + packageType + "\" result.");
            receivedDataPackage = senderReceiver.receivePackage();

            if (receivedDataPackage != null) {
                Log.d(Commander.class, LOG_TAG, "sendPackageAndWaitForResult (84): Received a package.");
                switch (receivedDataPackage.getPackageType()) {
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
                                throw new CommanderException("Unknown return value received from device after send \"" + packageType + "\" package.");
                        }
                        break;
                    default:
                        Log.d(Commander.class, LOG_TAG, "sendPackageAndWaitForResult (101): The package received is not a \"" + PackageType.COMMAND_RESULT + "\" package.");
                        throw new CommanderException("Received a \"" + receivedDataPackage.getPackageType() + "\" package, when expecting a \"" + PackageType.COMMAND_RESULT + "\" package.");
                }
            } else {
                Log.d(Commander.class, LOG_TAG, "sendPackageAndWaitForResult (105): Didn't received the \"" + packageType + "\" command result.");
                throw new CommanderException("No package received.");
            }

        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while receiving result after send \"" + packageType + "\" package.", communicationException);
        }

        return commandResult;
    }

    public File requestLatestAudioFile() throws CommanderException {
        Log.d(Commander.class, LOG_TAG, "requestLatestAudioFile (117): Requesting latest audio file.");

        DataPackage requestAudioFilePackage = new DataPackage(PackageType.REQUEST_AUDIO_FILE, null);
        try {
            senderReceiver.sendPackage(requestAudioFilePackage);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while requesting latest audio file.", communicationException);
        }

        FileReceiver fileReceiver = new FileReceiver(senderReceiver);
        try {
            fileReceiver.receiveFile();
        } catch (FileReceiverException fileReceiverException) {
            throw new CommanderException("Error while receiving latest audio file.", fileReceiverException);
        }


        return fileReceiver.getFile();
    }

    public boolean checkConnection() {
        // Log.d(LOG_TAG, "checkConnection, 175: Checking connection with audio recorder.");
        boolean result;

        DataPackage checkConnectionDataPackage = new DataPackage(PackageType.CHECK_CONNECTION, null);

        try {
            senderReceiver.sendPackage(checkConnectionDataPackage);
            result = true;
        } catch (CommunicationException communicationException) {
            Log.d(Commander.class, LOG_TAG, "checkConnection (147): Error while checking connection: " + communicationException.getMessage());
            communicationException.printStackTrace();
            result = false;
        }

        return result;
    }

}
