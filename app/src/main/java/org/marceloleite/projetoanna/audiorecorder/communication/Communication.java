package org.marceloleite.projetoanna.audiorecorder.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.connection.Connection;
import org.marceloleite.projetoanna.audiorecorder.connection.ConnectionException;
import org.marceloleite.projetoanna.audiorecorder.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audiorecorder.datapackage.content.Content;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audiorecorder.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.audiorecorder.utils.retryattempts.RetryAttemptsReturnCodes;

/**
 * Controls the communication between the application and the recorder.
 */
public class Communication {

    private static final String LOG_TAG = Communication.class.getSimpleName();

    /**
     * Maximum retry attempts to receive a package.
     */
    private static final int MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS = 10;

    /**
     * The connection between the application and the recorder.
     */
    Connection connection;

    /**
     * Creates a new communication controller.
     *
     * @param bluetoothSocket The bluetooth socket which stablishes a connection between the application and the recorder.
     */
    public Communication(BluetoothSocket bluetoothSocket) {
        this.connection = new Connection(bluetoothSocket);
    }

    /**
     * Receives a package through the connection.
     * TODO: Conclude documentation.
     *
     * @return
     * @throws CommunicationException
     */
    public DataPackage receivePackage() throws CommunicationException {
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);
        boolean doneReading = false;
        DataPackage dataPackage = null;
        byte[] bytes;

        while (!doneReading) {

            try {
                bytes = connection.readSocketContent();
                if (bytes != null) {
                    Log.d(LOG_TAG, "receivePackage, 65: Bytes received: " + bytes.length);
                    dataPackage = new DataPackage(bytes);
                    sendConfirmation(dataPackage);
                    doneReading = true;
                } else {
                    switch (RetryAttempts.wait(retryAttempts)) {
                        case RetryAttemptsReturnCodes.SUCCESS:
                            break;
                        case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                            doneReading = true;
                            break;
                        default:
                            throw new CommunicationException("Unknown return code received from \"wait\" function.");
                    }
                }
            } catch (ConnectionException connectionException) {
                Log.d(LOG_TAG, "receivePackage, 78: Exception thrown.");
                throw new CommunicationException("Error while receiving a package.", connectionException);
            }
        }
        return dataPackage;
    }

    private boolean sendConfirmation(DataPackage dataPackage) throws CommunicationException {
        Log.d(LOG_TAG, "sendConfirmation, 94: Sending confirmation for package \"" + Integer.toHexString(dataPackage.getId()) + "\".");
        boolean returnValue;
        Content confirmationContent = new ConfirmationContent(dataPackage.getId());
        DataPackage dataPackageConfirmation = new DataPackage(PackageType.CONFIRMATION, confirmationContent);
        byte[] dataPackageConfirmationBytes = dataPackageConfirmation.convertToBytes();

        try {
            connection.writeContentOnSocket(dataPackageConfirmationBytes);
            returnValue = true;
        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error while sending confirmation package.");
        }
        return returnValue;
    }

    public boolean sendPackage(DataPackage dataPackage) throws CommunicationException {
        Log.d(LOG_TAG, "sendPackage, 101: Sending \"" + dataPackage.getPackageType() + "\" package.");
        boolean returnValue;
        try {
            connection.writeContentOnSocket(dataPackage.convertToBytes());

            if (receiveConfirmation(dataPackage)) {
                returnValue = true;
            } else {
                returnValue = false;
            }

        } catch (ConnectionException connectionException) {
            throw new CommunicationException("Error while sending a package.", connectionException);
        }
        return returnValue;
    }

    private boolean receiveConfirmation(DataPackage dataPackage) throws CommunicationException {
        Log.d(LOG_TAG, "receiveConfirmation, 120: Receiving confirmation.");
        byte[] bytes = null;
        boolean returnValue = false;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);

        try {
            while (!doneReceivingConfirmation) {
                bytes = connection.readSocketContent();
                if (bytes != null) {
                    DataPackage receivedPackage = new DataPackage(bytes);
                    if (receivedPackage.getPackageType() == PackageType.CONFIRMATION) {
                        ConfirmationContent confirmationContent = (ConfirmationContent) receivedPackage.getContent();
                        if (confirmationContent.getPackageId() == dataPackage.getId()) {
                            Log.d(LOG_TAG, "receiveConfirmation, 134: Confirmation received.");
                            doneReceivingConfirmation = true;
                            returnValue = true;
                        } else {
                            Log.d(LOG_TAG, "receiveConfirmation, 137: Received a confirmation, but not for package id " + Integer.toHexString(dataPackage.getId()) + ".");
                        }
                    } else {
                        Log.d(LOG_TAG, "receiveConfirmation, 138: Received a \"" + receivedPackage.getPackageType() + "\" package");
                    }
                } else {
                    switch (RetryAttempts.wait(retryAttempts)) {
                        case RetryAttemptsReturnCodes.SUCCESS:
                            break;
                        case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                            Log.d(LOG_TAG, "receiveConfirmation, 148: Maximum retries reached.");
                            doneReceivingConfirmation = true;
                            returnValue = false;
                            break;
                        default:
                            throw new CommunicationException("Unknown return code received from \"wait\" function.");
                    }
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
