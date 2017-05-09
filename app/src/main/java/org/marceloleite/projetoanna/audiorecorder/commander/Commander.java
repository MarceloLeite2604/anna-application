package org.marceloleite.projetoanna.audiorecorder.commander;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.CommandResultContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.audiorecorder.commander.filereceiver.FileReceiver;
import org.marceloleite.projetoanna.audiorecorder.commander.filereceiver.FileReceiverException;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;

import java.io.File;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Commander {

    private static final String LOG_TAG = Commander.class.getSimpleName();

    private SenderReceiver senderReceiver;

    public Commander(BluetoothSocket bluetoothSocket) {
        this.senderReceiver = new SenderReceiver(bluetoothSocket);
    }

    public int startRecord() throws CommanderException {
        return sendPackageAndWaitForResult(PackageType.START_RECORD);
    }

    public int stopRecord() throws CommanderException {
        int returnValue;
        returnValue = sendPackageAndWaitForResult(PackageType.STOP_RECORD);
        return returnValue;
    }

    public int disconnect() throws CommanderException {
        int result = 0;
        DataPackage disconnectDataPackage = new DataPackage(PackageType.DISCONNECT, null);
        try {
            senderReceiver.sendPackage(disconnectDataPackage);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while sending \"disconnect\" package.");
        }
        return result;
    }

    private int sendPackageAndWaitForResult(PackageType packageType) throws CommanderException {
        Log.d(LOG_TAG, "sendPackageAndWaitForResult, 117: Sending command \"" + packageType + "\".");
        DataPackage dataPackage = new DataPackage(packageType, null);
        int returnValue;

        try {
            senderReceiver.sendPackage(dataPackage);
        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while sending \"" + packageType + "\" package.", communicationException);
        }

        DataPackage receivedDataPackage;
        try {
            Log.d(LOG_TAG, "sendPackageAndWaitForResult, 128: Waiting for command \"" + packageType + "\" result.");
            receivedDataPackage = senderReceiver.receivePackage();

            if (receivedDataPackage != null) {
                Log.d(LOG_TAG, "sendPackageAndWaitForResult, 129: Received a package.");
                switch (receivedDataPackage.getPackageType()) {
                    case COMMAND_RESULT:
                        CommandResultContent commandResultContent = (CommandResultContent) receivedDataPackage.getContent();
                        switch (commandResultContent.getResultCode()) {
                            case GenericReturnCodes.SUCCESS:
                            case GenericReturnCodes.GENERIC_ERROR:
                                returnValue = commandResultContent.getResultCode();
                                break;
                            default:
                                throw new CommanderException("Unknown return value received from device after send \"" + packageType + "\" package.");
                        }
                        break;
                    default:
                        Log.d(LOG_TAG, "sendPackageAndWaitForResult, 143: The package received is not a \"" + PackageType.COMMAND_RESULT + "\" package.");
                        throw new CommanderException("Received a \"" + receivedDataPackage.getPackageType() + "\" package, when expecting a \"" + PackageType.COMMAND_RESULT + "\" package.");
                }
            } else {
                Log.d(LOG_TAG, "sendPackageAndWaitForResult, 145: Didn't received the \"" + packageType + "\" command result.");
                throw new CommanderException("No package received.");
            }

        } catch (CommunicationException communicationException) {
            throw new CommanderException("Error while receiving result after send \"" + packageType + "\" package.", communicationException);
        }

        return returnValue;
    }

    public File requestLatestAudioFile() throws CommanderException {
        Log.d(LOG_TAG, "requestLatestAudioFile, 159: Requesting latest audio file.");

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
        Log.d(LOG_TAG, "checkConnection, 175: Checking connection with audio recorder.");
        boolean result;

        DataPackage checkConnectionDataPackage = new DataPackage(PackageType.CHECK_CONNECTION, null);

        try {
            senderReceiver.sendPackage(checkConnectionDataPackage);
            result = true;
        } catch (CommunicationException communicationException) {
            Log.d(LOG_TAG, "checkConnection, 50: Error while checking connection: " + communicationException.getMessage());
            communicationException.printStackTrace();
            result = false;
        }

        return result;
    }

}
