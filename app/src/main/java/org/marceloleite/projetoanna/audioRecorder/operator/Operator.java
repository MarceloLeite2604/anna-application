package org.marceloleite.projetoanna.audioRecorder.operator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.datapackage.DataPackage;
import org.marceloleite.projetoanna.audioRecorder.datapackage.PackageType;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.CommandResultContent;
import org.marceloleite.projetoanna.audioRecorder.communication.Communication;
import org.marceloleite.projetoanna.audioRecorder.communication.CommunicationReturnCodes;
import org.marceloleite.projetoanna.audioRecorder.communication.ReceivePackageResult;
import org.marceloleite.projetoanna.audioRecorder.operator.filereceiver.FileReceiver;
import org.marceloleite.projetoanna.audioRecorder.operator.filereceiver.FileReceiverException;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audioRecorder.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.audioRecorder.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.audioRecorder.utils.retryattempts.RetryAttemptsReturnCodes;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Operator {

    /**
     * Maximum retry attempts to receive a package.
     */
    private static final int MAXIMUM_RECEIVE_PACKAGE_RETRIES = 30;

    private Context context;

    private Communication communication;

    private RetryAttempts retryAttempts;

    public Operator(Context context, BluetoothSocket bluetoothSocket) {
        this.context = context;
        this.communication = new Communication(bluetoothSocket);
        this.retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRIES);
    }

    public void receivePackage() throws OperatorException {
        DataPackage dataPackage;
        try {
            dataPackage = communication.receivePackage();
            if (dataPackage != null) {
                checkPackageReceived(dataPackage);
            }
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while receiving a package.", communicationException);
        }

        switch (RetryAttempts.wait(retryAttempts)) {
            case RetryAttemptsReturnCodes.SUCCESS:
                break;
            case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                try {
                    communication.disconnect();
                } catch (CommunicationException communicationException) {
                    throw new OperatorException("Error while requesting to disconnect from equipment.", communicationException);
                }
                break;
        }
    }

    private void checkPackageReceived(DataPackage dataPackage) throws OperatorException {
        switch (dataPackage.getPackageType()) {
            case CHECK_CONNECTION:
                Log.d(MainActivity.LOG_TAG, "checkPackageReceived, 73: Checking connection.");
                retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRIES);
                break;
            case COMMAND_RESULT:
            case CONFIRMATION:
            case REQUEST_AUDIO_FILE:
            case FILE_CHUNK:
            case FILE_HEADER:
            case FILE_TRAILER:
            case START_RECORD:
            case STOP_RECORD:
                throw new OperatorException("Received a \"" + dataPackage.getPackageType() + "\" package type, which should not be received.");
            case DISCONNECT:
                try {
                    communication.disconnect();
                } catch (CommunicationException communicationException) {
                    throw new OperatorException("Error while requesting to disconnect from equipment.", communicationException);
                }
                break;
            case ERROR:
                /* TODO: Report error to user. */
                break;
            default:
                throw new OperatorException("Unknown package type \"" + dataPackage.getPackageType() + "\".");
        }
    }

    public int startRecord() throws OperatorException {
        return sendStartStopRecord(PackageType.START_RECORD);
    }

    public int stopRecord() throws OperatorException {
        int returnValue;
        returnValue = sendStartStopRecord(PackageType.STOP_RECORD);
        /*if (returnValue == GenericReturnCodes.SUCCESS) {
            requestLatestAudioFile();
        }*/

        return returnValue;
    }

    private int sendStartStopRecord(PackageType packageType) throws OperatorException {
        Log.d(MainActivity.LOG_TAG, "sendStartStopRecord, 117: Sending command \"" + packageType + "\".");
        DataPackage dataPackage = new DataPackage(packageType, null);
        int returnValue;

        try {
            communication.sendPackage(dataPackage);
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while sending \"" + packageType + "\" package.", communicationException);
        }

        DataPackage receivedDataPackage;
        try {
            Log.d(MainActivity.LOG_TAG, "sendStartStopRecord, 128: Waiting for command \"" + packageType + "\" result.");
            receivedDataPackage = communication.receivePackage();

            if (receivedDataPackage != null) {
                Log.d(MainActivity.LOG_TAG, "sendStartStopRecord, 129: Received a package.");
                switch (receivedDataPackage.getPackageType()) {
                    case COMMAND_RESULT:
                        CommandResultContent commandResultContent = (CommandResultContent) receivedDataPackage.getContent();
                        switch (commandResultContent.getResultCode()) {
                            case GenericReturnCodes.SUCCESS:
                            case GenericReturnCodes.GENERIC_ERROR:
                                returnValue = commandResultContent.getResultCode();
                                break;
                            default:
                                throw new OperatorException("Unknown return value received from device after send \"" + packageType + "\" package.");
                        }
                        break;
                    default:
                        Log.d(MainActivity.LOG_TAG, "sendStartStopRecord, 143: The package received is not a \"" + PackageType.COMMAND_RESULT + "\" package.");
                        throw new OperatorException("Received a \"" + receivedDataPackage.getPackageType() + "\" package, when expecting a \"" + PackageType.COMMAND_RESULT + "\" package.");
                }
            } else {
                Log.d(MainActivity.LOG_TAG, "sendStartStopRecord, 145: Didn't received the \"" + packageType + "\" command result.");
                throw new OperatorException("No package received.");
            }

        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while receiving result after send \"" + packageType + "\" package.", communicationException);
        }

        return returnValue;
    }

    private void requestLatestAudioFile() throws OperatorException {

        DataPackage requestAudioFilePackage = new DataPackage(PackageType.REQUEST_AUDIO_FILE, null);
        try {
            communication.sendPackage(requestAudioFilePackage);
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while requesting latest audio file.", communicationException);
        }

        FileReceiver fileReceiver = new FileReceiver(context, communication);
        try {
            fileReceiver.receiveFile();
        } catch (FileReceiverException fileReceiverException) {
            throw new OperatorException("Error while receiving latest audio file.", fileReceiverException);
        }
    }
}
