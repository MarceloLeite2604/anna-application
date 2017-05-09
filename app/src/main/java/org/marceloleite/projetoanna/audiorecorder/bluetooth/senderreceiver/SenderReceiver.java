package org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter.ReaderWriter;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter.ReaderWriterException;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.Content;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.nio.ByteBuffer;

/**
 * Controls the communication between the application and the recorder.
 */
public class SenderReceiver {

    private static final String LOG_TAG = SenderReceiver.class.getSimpleName();

    /**
     * Maximum retry attempts to receive a package.
     */
    private static final int MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS = 10;

    /**
     * The readerWriter between the application and the recorder.
     */
    ReaderWriter readerWriter;

    private byte[] remainingBytes;

    /**
     * Creates a new communication controller.
     *
     * @param bluetoothSocket The bluetooth socket which stablishes a readerWriter between the application and the recorder.
     */
    public SenderReceiver(BluetoothSocket bluetoothSocket) {
        this.readerWriter = new ReaderWriter(bluetoothSocket);
        this.remainingBytes = null;
    }

    /**
     * Receives a package through the readerWriter.
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
                bytes = readFromSocket();
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
            } catch (ReaderWriterException readerWriterException) {
                Log.d(LOG_TAG, "receivePackage, 78: Exception thrown.");
                throw new CommunicationException("Error while receiving a package.", readerWriterException);
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
            readerWriter.writeContentOnSocket(dataPackageConfirmationBytes);
            returnValue = true;
        } catch (ReaderWriterException readerWriterException) {
            throw new CommunicationException("Error while sending confirmation package.");
        }
        return returnValue;
    }

    public boolean sendPackage(DataPackage dataPackage) throws CommunicationException {
        Log.d(LOG_TAG, "sendPackage, 101: Sending \"" + dataPackage.getPackageType() + "\" package.");
        boolean returnValue;
        try {
            readerWriter.writeContentOnSocket(dataPackage.convertToBytes());

            if (receiveConfirmation(dataPackage)) {
                returnValue = true;
            } else {
                returnValue = false;
            }

        } catch (ReaderWriterException readerWriterException) {
            throw new CommunicationException("Error while sending a package.", readerWriterException);
        }
        return returnValue;
    }

    private boolean receiveConfirmation(DataPackage dataPackage) throws CommunicationException {
        Log.d(LOG_TAG, "receiveConfirmation, 120: Receiving confirmation.");
        byte[] bytes;
        boolean returnValue = false;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(MAXIMUM_RECEIVE_PACKAGE_RETRY_ATTEMPTS);

        try {
            while (!doneReceivingConfirmation) {
                bytes = readFromSocket();
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

        } catch (ReaderWriterException readerWriterException) {
            throw new CommunicationException("Error receiving a confirmation package.", readerWriterException);
        }
        return returnValue;
    }

    public void disconnect() throws CommunicationException {
        try {
            readerWriter.closeConnection();
        } catch (ReaderWriterException readerWriterException) {
            throw new CommunicationException("Error while disconnecting from equipment.", readerWriterException);
        }
    }

    private byte[] readFromSocket() throws ReaderWriterException {
        byte[] contentRead;
        contentRead = readerWriter.readSocketContent();
        if (contentRead != null) {
            Log.d(LOG_TAG, "readFromSocket, 179: Read " + contentRead.length + " byte(s) from socket.");
        }
        contentRead = concatenateRemainingBytes(contentRead);
        contentRead = removeRemainingBytes(contentRead);
        return contentRead;
    }

    private byte[] removeRemainingBytes(byte[] bytes) {
        byte[] resultBytes;

        if (bytes != null) {
            int packageTrailerPosition = DataPackage.findPackageTrailerPosition(bytes);
            if (packageTrailerPosition >= 0) {
                int remainingBytesLength = (bytes.length - (packageTrailerPosition + DataPackage.PACKAGE_TRAILER_BYTE_ARRAY_SIZE));
                int resultBytesLength = bytes.length - remainingBytesLength;
                if (remainingBytesLength > 0) {
                    Log.d(LOG_TAG, "removeRemainingBytes, 195: " + remainingBytesLength + " byte(s) removed.");
                    remainingBytes = new byte[remainingBytesLength];
                    System.arraycopy(bytes, packageTrailerPosition + DataPackage.PACKAGE_TRAILER_BYTE_ARRAY_SIZE, remainingBytes, 0, remainingBytesLength);
                    resultBytes = new byte[resultBytesLength];
                    System.arraycopy(bytes, 0, resultBytes, 0, resultBytesLength);
                } else {
                    remainingBytes = null;
                    resultBytes = bytes;
                }
            } else {
                remainingBytes = bytes;
                resultBytes = null;
            }
        } else {
            resultBytes = bytes;
        }
        return resultBytes;
    }

    private byte[] concatenateRemainingBytes(byte[] bytes) {
        int resultBytesLength = 0;
        int bytesCopied = 0;
        byte[] resultBytes = null;

        if (remainingBytes != null) {
            Log.d(LOG_TAG, "concatenateRemainingBytes, 202: Concatenating " + remainingBytes.length + " byte(s).");
            resultBytesLength += remainingBytes.length;
        }

        if (bytes != null) {
            resultBytesLength += bytes.length;
        }

        if (resultBytesLength > 0) {
            resultBytes = new byte[resultBytesLength];

            if (remainingBytes != null) {
                System.arraycopy(remainingBytes, 0, resultBytes, bytesCopied, remainingBytes.length);
                bytesCopied += remainingBytes.length;
            }

            if (bytes != null) {
                System.arraycopy(bytes, 0, resultBytes, bytesCopied, bytes.length);
            }
        }

        return resultBytes;
    }
}