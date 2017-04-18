package org.marceloleite.projetoanna.bluetooth.operator;

import android.content.Context;

import org.marceloleite.projetoanna.bluetooth.btpackage.BTPackage;
import org.marceloleite.projetoanna.bluetooth.btpackage.TypeCode;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.CommandResultContent;
import org.marceloleite.projetoanna.bluetooth.communication.Communication;
import org.marceloleite.projetoanna.bluetooth.communication.CommunicationReturnCodes;
import org.marceloleite.projetoanna.bluetooth.communication.ReceivePackageResult;
import org.marceloleite.projetoanna.bluetooth.operator.filereceiver.FileReceiver;
import org.marceloleite.projetoanna.bluetooth.operator.filereceiver.FileReceiverException;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.bluetooth.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.bluetooth.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.bluetooth.utils.retryattempts.RetryAttemptsReturnCodes;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Operator {

    private static final int MAXIMUM_RECEIVE_PACKAGE_RETRIES = 30;

    private Context context;

    private Communication communication;

    private RetryAttempts retryAttempts;

    public Operator(Context context, Communication communication) {
        this.context = context;
        this.communication = communication;
        this.retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRIES);
    }

    private void receivePackage() throws OperatorException {
        ReceivePackageResult receivePackageResult;
        try {
            receivePackageResult = communication.receivePackage();
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while receiving a package.", communicationException);
        }

        switch (receivePackageResult.getReturnValue()) {
            case CommunicationReturnCodes.SUCCESS:
                BTPackage btPackage = receivePackageResult.getBtPackage();
                checkPackageReceived(btPackage);
                break;
            case CommunicationReturnCodes.NO_PACKAGE_RECEIVED:
            case CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION:
                break;
            default:
                throw new OperatorException("Unknown return code received from \"receivePackage\" function.");
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

    private void checkPackageReceived(BTPackage btPackage) throws OperatorException {
        switch (btPackage.getTypeCode()) {
            case CHECK_CONNECTION:
                retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRIES);
                break;
            case COMMAND_RESULT:
            case CONFIRMATION:
            case REQUEST_AUDIO_FILE:
            case SEND_FILE_CHUNK:
            case SEND_FILE_HEADER:
            case SEND_FILE_TRAILER:
            case START_RECORD:
            case STOP_RECORD:
                throw new OperatorException("Received a \"" + btPackage.getTypeCode().getDescription() + "\" package type, which should not be received.");
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
                throw new OperatorException("Unknown package type \"" + btPackage.getTypeCode().getDescription() + "\".");
        }
    }

    private int startRecord() throws OperatorException {
        return sendStartStopRecord(TypeCode.START_RECORD);
    }

    private int stopRecord() throws OperatorException {
        int returnValue;
        returnValue = sendStartStopRecord(TypeCode.STOP_RECORD);
        if (returnValue == GenericReturnCodes.SUCCESS) {
            requestLatestAudioFile();
        }

        return returnValue;
    }

    private int sendStartStopRecord(TypeCode typeCode) throws OperatorException {
        BTPackage btPackage = new BTPackage(typeCode, null);
        int returnValue;

        try {
            communication.sendPackage(btPackage);
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while sending \"" + typeCode.getDescription() + "\" package.", communicationException);
        }

        ReceivePackageResult receivePackageResult;
        try {
            receivePackageResult = communication.receivePackage();
        } catch (CommunicationException communicationException) {
            throw new OperatorException("Error while receiving result after send \"" + typeCode.getDescription() + "\" package.", communicationException);
        }

        switch (receivePackageResult.getReturnValue()) {
            case CommunicationReturnCodes.SUCCESS:

                BTPackage resultPackage = receivePackageResult.getBtPackage();
                switch (resultPackage.getTypeCode()) {
                    case COMMAND_RESULT:
                        CommandResultContent commandResultContent = (CommandResultContent) resultPackage.getContent();
                        switch (commandResultContent.getResultCode()) {
                            case GenericReturnCodes.SUCCESS:
                            case GenericReturnCodes.GENERIC_ERROR:
                                returnValue = commandResultContent.getResultCode();
                                break;
                            default:
                                throw new OperatorException("Unknown return value received from device after send \"" + typeCode.getDescription() + "\" package.");
                        }
                        break;
                    default:
                        throw new OperatorException("Received a \"" + typeCode.getDescription() + "\" package, when expecting a \"" + TypeCode.COMMAND_RESULT.getDescription() + "\" package.");
                }
                break;
            case CommunicationReturnCodes.NO_PACKAGE_RECEIVED:
                throw new OperatorException("No package received.");
            case CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION:
                throw new OperatorException("Could not send package confirmation.");
            default:
                throw new OperatorException("Unknown return code received from \"receivePackage\" function.");
        }

        return returnValue;
    }

    private void requestLatestAudioFile() throws OperatorException {

        BTPackage requestAudioFilePackage = new BTPackage(TypeCode.REQUEST_AUDIO_FILE, null);
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
