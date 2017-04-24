package org.marceloleite.projetoanna.audioRecorder.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.connection.Connection;
import org.marceloleite.projetoanna.audioRecorder.connection.ConnectionException;
import org.marceloleite.projetoanna.audioRecorder.datapackage.DataPackage;
import org.marceloleite.projetoanna.audioRecorder.datapackage.PackageType;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audioRecorder.datapackage.content.Content;
import org.marceloleite.projetoanna.audioRecorder.connection.ConnectionReturnCodes;
import org.marceloleite.projetoanna.audioRecorder.connection.ReadSocketContentResult;
import org.marceloleite.projetoanna.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audioRecorder.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.audioRecorder.utils.retryattempts.RetryAttemptsReturnCodes;

/**
 * Controls the communication between the application and the recorder.
 */
public class Communication {

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
     * @param bluetoothSocket The bluetooth socket which stabilishes a connection between the application and the recorder.
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

        while (!doneReading) {

            try {
                ReadSocketContentResult readSocketContentResult = connection.readSocketContent();

                switch (readSocketContentResult.getReturnValue()) {
                    case ConnectionReturnCodes.SUCCESS:
                        Log.d(MainActivity.LOG_TAG, "receivePackage, 65: Bytes received: " + readSocketContentResult.getBytes().length);
                        dataPackage = new DataPackage(readSocketContentResult.getBytes());
                        sendConfirmation(dataPackage);
                        doneReading = true;
                        break;
                    case ConnectionReturnCodes.NO_CONTENT_TO_READ:
                        Log.d(MainActivity.LOG_TAG, "receivePackage, 68: No content received.");
                        switch (RetryAttempts.wait(retryAttempts)) {
                            case RetryAttemptsReturnCodes.SUCCESS:
                                break;
                            case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                                doneReading = true;
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
        return dataPackage;
    }

    private boolean sendConfirmation(DataPackage dataPackage) throws CommunicationException {
        Log.d(MainActivity.LOG_TAG, "sendConfirmation, 94: Sending confirmation for package \"" + Integer.toHexString(dataPackage.getId()) + "\".");
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
        boolean returnValue = false;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);

        try {
            while (!doneReceivingConfirmation) {
                ReadSocketContentResult readSocketContentResult = connection.readSocketContent();
                switch (readSocketContentResult.getReturnValue()) {
                    case ConnectionReturnCodes.SUCCESS:
                        DataPackage confirmationPackage = new DataPackage(readSocketContentResult.getBytes());
                        if (confirmationPackage.getPackageType() == PackageType.CONFIRMATION) {
                            ConfirmationContent confirmationContent = (ConfirmationContent) confirmationPackage.getContent();
                            if (confirmationContent.getPackageId() == dataPackage.getId()) {
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
                        break;
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
