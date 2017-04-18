package org.marceloleite.projetoanna.bluetooth.communication;

import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.CommandResultContent;
import org.marceloleite.projetoanna.bluetooth.connection.Connection;
import org.marceloleite.projetoanna.bluetooth.ConnectionException;
import org.marceloleite.projetoanna.bluetooth.btpackage.BTPackage;
import org.marceloleite.projetoanna.bluetooth.btpackage.TypeCode;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.bluetooth.btpackage.content.Content;
import org.marceloleite.projetoanna.bluetooth.connection.ConnectionReturnCodes;
import org.marceloleite.projetoanna.bluetooth.connection.ReadSocketContentResult;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.bluetooth.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.bluetooth.utils.retryattempts.RetryAttemptsReturnCodes;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Communication {

    private static final int MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS = 30;

    Connection connection;

    public Communication(Connection connection) {
        this.connection = connection;
    }

    public ReceivePackageResult receivePackage() throws CommunicationException {
        ReceivePackageResult receivePackageResult = null;
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);
        boolean doneReading = false;
        BTPackage btPackage = null;

        while (!doneReading) {

            try {
                ReadSocketContentResult readSocketContentResult = connection.readSocketContent();

                switch (readSocketContentResult.getReturnValue()) {
                    case ConnectionReturnCodes.SUCCESS:
                        btPackage = new BTPackage(readSocketContentResult.getBytes());
                        doneReading = true;
                        break;
                    case ConnectionReturnCodes.NO_CONTENT_TO_READ:
                        switch (RetryAttempts.wait(retryAttempts)) {
                            case RetryAttemptsReturnCodes.SUCCESS:
                                break;
                            case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                                doneReading = true;
                                receivePackageResult = new ReceivePackageResult(CommunicationReturnCodes.NO_PACKAGE_RECEIVED, null);
                                break;
                            default:
                                throw new CommunicationException("Unknown return code received from \"wait\" function.");
                        }
                        break;
                    default:
                        throw new CommunicationException("Unknown return code received from \"readSocketContent\" function.");
                }
            } catch (ConnectionException connectionException) {
                throw new CommunicationException("Error while receiving a package.", connectionException);
            }
        }

        if (receivePackageResult != null && receivePackageResult.getReturnValue() != CommunicationReturnCodes.NO_PACKAGE_RECEIVED) {
            if (sendConfirmation(btPackage)) {
                receivePackageResult = new ReceivePackageResult(CommunicationReturnCodes.SUCCESS, btPackage);
            } else {
                receivePackageResult = new ReceivePackageResult(CommunicationReturnCodes.COULD_NOT_SEND_CONFIRMATION, null);
            }
        }

        return receivePackageResult;
    }

    private boolean sendConfirmation(BTPackage btPackage) throws CommunicationException {
        boolean returnValue;
        Content confirmationContent = new ConfirmationContent(btPackage.getId());
        BTPackage btPackageConfirmation = new BTPackage(TypeCode.CONFIRMATION, confirmationContent);
        byte[] btPackageConfirmationBytes = btPackageConfirmation.convertToBytes();

        try {
            connection.writeContentOnSocket(btPackageConfirmationBytes);
            returnValue = true;
        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error while sending confirmation package.");
        }
        return returnValue;
    }

    public boolean sendPackage(BTPackage btPackage) throws CommunicationException {
        boolean returnValue;
        try {
            connection.writeContentOnSocket(btPackage.convertToBytes());

            if (receiveConfirmation(btPackage)) {
                returnValue = true;
            } else {
                returnValue = false;
            }

        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error while sending a package.", connectionException);
        }
        return returnValue;
    }

    private boolean receiveConfirmation(BTPackage btPackage) throws CommunicationException {
        boolean returnValue = false;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);

        try {
            while (!doneReceivingConfirmation) {
                ReadSocketContentResult readSocketContentResult = connection.readSocketContent();
                switch (readSocketContentResult.getReturnValue()) {
                    case ConnectionReturnCodes.SUCCESS:
                        BTPackage confirmationPackage = new BTPackage(readSocketContentResult.getBytes());
                        if (confirmationPackage.getTypeCode() == TypeCode.CONFIRMATION) {
                            ConfirmationContent confirmationContent = (ConfirmationContent) confirmationPackage.getContent();
                            if (confirmationContent.getPackageId() == btPackage.getId()) {
                                doneReceivingConfirmation = true;
                                returnValue = true;
                            }
                        }
                        break;
                    case ConnectionReturnCodes.NO_CONTENT_TO_READ:
                        break;
                    default:
                        throw new CommunicationException("Unknown return code received from \"readSocketContent\" function.");
                }
                switch (RetryAttempts.wait(retryAttempts)) {
                    case RetryAttemptsReturnCodes.SUCCESS:
                        break;
                    case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                        doneReceivingConfirmation = true;
                        returnValue = false;
                    default:
                        throw new CommunicationException("Unknown return code received from \"wait\" function.");
                }
            }

        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error receiving a confirmation package.", connectionException);
        }
        return returnValue;
    }

    public void disconnect() throws CommunicationException {
        try {
            connection.closeConnection();
        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error while disconnecting from equipment.", connectionException);
        }
    }
}
