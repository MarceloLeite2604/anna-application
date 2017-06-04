package org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content.Content;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.CommunicationException;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter.ReadSocketContentResult;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter.ReaderWriter;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter.ReaderWriterException;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.average.Average;
import org.marceloleite.projetoanna.utils.chonometer.Chronometer;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.io.IOException;

/**
 * Controls the communication between the application and the recorder.
 */
public class SenderReceiver {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = SenderReceiver.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(SenderReceiver.class);
    }

    /**
     * Maximum retry attempts to receive a package.
     */
    private static final int RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS = 140;

    private static final int RECEIVE_PACKAGE_MINIMUM_WAIT_TIME = 30;

    private static final int RECEIVE_PACKAGE_STEP_TIME = 2;

    private static final int COMMUNICATION_DELAY_AVERAGE_BUFFER_SIZE = 5;

    /**
     * The readerWriter between the application and the recorder.
     */
    ReaderWriter readerWriter;

    private byte[] remainingBytes;

    private Average communicationDelayAverage;

    /**
     * Creates a new communication controller.
     *
     * @param bluetoothSocket The bluetooth socket which stablishes a readerWriter between the application and the recorder.
     */
    public SenderReceiver(BluetoothSocket bluetoothSocket) throws CommunicationException {
        try {
            this.readerWriter = new ReaderWriter(bluetoothSocket);
        } catch (IOException ioException) {
            throw new CommunicationException("Error while creating SenderReceiver object.", ioException);
        }
        this.remainingBytes = null;
        this.communicationDelayAverage = new Average(COMMUNICATION_DELAY_AVERAGE_BUFFER_SIZE);
    }

    /**
     * Receives a package through the readerWriter.
     * TODO: Conclude documentation.
     *
     * @return
     * @throws CommunicationException
     */
    public DataPackage receivePackage() throws CommunicationException {
        RetryAttempts retryAttempts = new RetryAttempts(RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS, RECEIVE_PACKAGE_MINIMUM_WAIT_TIME, RECEIVE_PACKAGE_STEP_TIME);
        boolean doneReading = false;
        DataPackage dataPackage = null;
        byte[] bytes;

        while (!doneReading) {

            try {
                bytes = readFromSocket();
                if (bytes != null) {
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
                Log.d(SenderReceiver.class, LOG_TAG, "receivePackage (105): Exception thrown.");
                throw new CommunicationException("Error while receiving a package.", readerWriterException);
            }
        }
        return dataPackage;
    }

    private boolean sendConfirmation(DataPackage dataPackage) throws CommunicationException {
        // Log.d(LOG_TAG, "sendConfirmation, 94: Sending confirmation for package \"" + Integer.toHexString(dataPackage.getId()) + "\".");
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
        // Log.d(LOG_TAG, "sendPackage, 101: Sending \"" + dataPackage.getPackageType() + "\" package.");

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
        // Log.d(LOG_TAG, "receiveConfirmation, 120: Receiving confirmation.");
        byte[] bytes;
        boolean returnValue = false;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS);
        Chronometer delayChronometer = new Chronometer();


        try {
            delayChronometer.start();
            while (!doneReceivingConfirmation) {
                bytes = readFromSocket();
                if (bytes != null) {
                    DataPackage receivedPackage = new DataPackage(bytes);
                    if (receivedPackage.getPackageType() == PackageType.CONFIRMATION) {
                        ConfirmationContent confirmationContent = (ConfirmationContent) receivedPackage.getContent();
                        if (confirmationContent.getPackageId() == dataPackage.getId()) {
                            doneReceivingConfirmation = true;
                            returnValue = true;
                        } else {
                            Log.d(SenderReceiver.class, LOG_TAG, "receiveConfirmation (168): Received a confirmation, but not for package id " + Integer.toHexString(dataPackage.getId()) + ".");
                        }
                    } else {
                        Log.d(SenderReceiver.class, LOG_TAG, "receiveConfirmation (171): Received a \"" + receivedPackage.getPackageType() + "\" package");
                    }
                } else {
                    switch (RetryAttempts.wait(retryAttempts)) {
                        case RetryAttemptsReturnCodes.SUCCESS:
                            break;
                        case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                            Log.d(SenderReceiver.class, LOG_TAG, "receiveConfirmation (178): Maximum retries reached.");
                            doneReceivingConfirmation = true;
                            returnValue = false;
                            break;
                        default:
                            throw new CommunicationException("Unknown return code received from \"wait\" function.");
                    }
                }
            }
            delayChronometer.stop();
            communicationDelayAverage.add(delayChronometer.getDifference());

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

    public long getCommunicationDelay() {
        return communicationDelayAverage.getAverage();
    }

    private byte[] readFromSocket() throws ReaderWriterException {
        ReadSocketContentResult readSocketContentResult;
        readSocketContentResult = readerWriter.readSocketContent();
        /*if (contentRead != null) {
            Log.d(LOG_TAG, "readFromSocket, 179: Read " + contentRead.length + " byte(s) from socket.");
        }*/
        byte[] bytes = readSocketContentResult.getContentRead();
        bytes = concatenateRemainingBytes(bytes);
        bytes = removeRemainingBytes(bytes);
        return bytes;
    }

    private byte[] removeRemainingBytes(byte[] bytes) {
        byte[] resultBytes;

        if (bytes != null) {
            int packageTrailerPosition = DataPackage.findPackageTrailerPosition(bytes);
            if (packageTrailerPosition >= 0) {
                int remainingBytesLength = (bytes.length - (packageTrailerPosition + DataPackage.PACKAGE_TRAILER_BYTE_ARRAY_SIZE));
                int resultBytesLength = bytes.length - remainingBytesLength;
                if (remainingBytesLength > 0) {
                    /*Log.d(LOG_TAG, "removeRemainingBytes, 195: " + remainingBytesLength + " byte(s) removed.");*/
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
            /*Log.d(LOG_TAG, "concatenateRemainingBytes, 202: Concatenating " + remainingBytes.length + " byte(s).");*/
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
