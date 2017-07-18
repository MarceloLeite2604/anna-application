package org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.DataPackage;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.PackageType;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content.ConfirmationContent;
import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.content.Content;
import org.marceloleite.projetoanna.audiorecorder.communicator.readerwriter.ReadSocketContentResult;
import org.marceloleite.projetoanna.audiorecorder.communicator.readerwriter.ReaderWriter;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.average.Average;
import org.marceloleite.projetoanna.utils.chonometer.Chronometer;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.io.IOException;

/**
 * Sends and receives the communication packages between the application and the audio recorder.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Maximum retry attempts to receive a package.
     */
    private static final int RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS = 50;

    /**
     * Minimum time to wait for a package (in milliseconds).
     */
    private static final int RECEIVE_PACKAGE_MINIMUM_WAIT_TIME = 30;

    /**
     * Time increased to wait each time an attempt to receive a package fails (in milliseconds).
     */
    private static final int RECEIVE_PACKAGE_STEP_TIME = 2;

    /**
     * Size of buffer to calculate the average communication delay.
     */
    private static final int COMMUNICATION_DELAY_AVERAGE_BUFFER_SIZE = 5;

    /**
     * The controller to read and write communication packages on bluetooth socket.
     */
    private ReaderWriter readerWriter;

    private byte[] remainingBytes;

    /**
     * Calculates the average communication delay between the application and the audio recorder.
     */
    private Average communicationDelayAverage;

    /**
     * Creates a new communication controller.
     *
     * @param bluetoothSocket The bluetooth socket communication between the application and the audio recorder.
     */
    public SenderReceiver(BluetoothSocket bluetoothSocket) {
        this.readerWriter = new ReaderWriter(bluetoothSocket);
        this.remainingBytes = null;
        this.communicationDelayAverage = new Average(COMMUNICATION_DELAY_AVERAGE_BUFFER_SIZE);
    }

    /**
     * Receives a package from bluetooth socket.
     *
     * @return The communication package received.
     */
    public ReceivePackageResult receivePackage() {
        boolean doneReading = false;
        DataPackage dataPackage;
        ReadSocketContentResult readSocketContentResult;

        /* Creates the object to control the receive package retry attempts. */
        RetryAttempts retryAttempts = new RetryAttempts(RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS, RECEIVE_PACKAGE_MINIMUM_WAIT_TIME, RECEIVE_PACKAGE_STEP_TIME);

        while (!doneReading) {

            try {
                /* Reads content from bluetooth socket. */
                readSocketContentResult = readFromSocket();
            } catch (IOException ioException) {
                disconnect();
                return new ReceivePackageResult(AudioRecorderReturnCodes.DISCONNECTED, null);
            }

            switch (readSocketContentResult.getReturnCode()) {
                case AudioRecorderReturnCodes.SUCCESS:

                    /* If a content was read from socket. */
                    if (readSocketContentResult.getContentRead() != null) {

                        /* Creates a new communication package from the content read. */
                        dataPackage = new DataPackage(readSocketContentResult.getContentRead());

                        /* Sends the received communication package confirmation. */
                        int sendConfirmationResult = sendConfirmation(dataPackage);

                        switch (sendConfirmationResult) {
                            case GenericReturnCodes.SUCCESS:
                                return new ReceivePackageResult(AudioRecorderReturnCodes.SUCCESS, dataPackage);
                            case GenericReturnCodes.GENERIC_ERROR:
                                return new ReceivePackageResult(AudioRecorderReturnCodes.GENERIC_ERROR, null);
                            default:
                                throw new RuntimeException("Unknown return code received from \"sendConfirmation\" method: " + sendConfirmationResult + ".");
                        }

                    /* If no content was read from socket. */
                    } else {

                        /* Waits to retry to receive a content from socket. */
                        int waitResult = retryAttempts.waitForNextAttempt();
                        switch (waitResult) {
                            case RetryAttemptsReturnCodes.SUCCESS:
                                break;

                            /* If the maximum retry attempts was reached. */
                            case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                                doneReading = true;
                                break;
                            default:
                                throw new RuntimeException("Unknown return code received from \"wait\" function: " + waitResult + ".");
                        }
                    }
                    break;

                default:
                    throw new RuntimeException("Unknown return code received from \"readFromSocket\" method: " + readSocketContentResult.getReturnCode() + ".");
            }
        }
        return new ReceivePackageResult(AudioRecorderReturnCodes.SUCCESS, null);
    }

    /**
     * Sends a confirmation package through bluetooth socket.
     *
     * @param dataPackage The communication package to be confirmed.
     * @return {@link GenericReturnCodes#SUCCESS} If the confirmation was send successfully. {@link GenericReturnCodes#GENERIC_ERROR} otherwise.
     */
    private int sendConfirmation(DataPackage dataPackage) {
        int returnValue;

        /* Creates a new confirmation package content. */
        Content confirmationContent = new ConfirmationContent(dataPackage.getId());

        /* Creates a new confirmation package. */
        DataPackage dataPackageConfirmation = new DataPackage(PackageType.CONFIRMATION, confirmationContent);

        /* Converts the confirmation package to a byte array. */
        byte[] dataPackageConfirmationBytes = dataPackageConfirmation.convertToBytes();

        try {
            /* Writes the confirmation package on bluetooth socket. */
            readerWriter.writeContentOnSocket(dataPackageConfirmationBytes);
            returnValue = GenericReturnCodes.SUCCESS;
        } catch (IOException ioException) {
            disconnect();
            returnValue = GenericReturnCodes.GENERIC_ERROR;
        }

        return returnValue;
    }

    /**
     * Sends a communication package through bluetooth socket.
     *
     * @param dataPackage The communication package to be send.
     * @return {@link AudioRecorderReturnCodes#SUCCESS} If the package was send successfully. {@link AudioRecorderReturnCodes#GENERIC_ERROR} otherwise.
     */
    public int sendPackage(DataPackage dataPackage) {

        try {
            /* Writes the communication package on bluetooth socket. */
            int writeContentOnSocketResult = readerWriter.writeContentOnSocket(dataPackage.convertToBytes());

            switch (writeContentOnSocketResult) {
                case AudioRecorderReturnCodes.SUCCESS:

                    /* Receives the package confirmation. */
                    int receiveConfirmationResult = receiveConfirmation(dataPackage);

                    switch (receiveConfirmationResult) {
                        case GenericReturnCodes.SUCCESS:
                            return AudioRecorderReturnCodes.SUCCESS;
                        case GenericReturnCodes.GENERIC_ERROR:
                            return AudioRecorderReturnCodes.GENERIC_ERROR;
                        default:
                            throw new RuntimeException("Unknown code returned from \"receiveConfirmation\" method: " + receiveConfirmationResult + ".");
                    }
                case AudioRecorderReturnCodes.CONNECTION_LOST:
                    return AudioRecorderReturnCodes.DISCONNECTED;
                default:
                    throw new RuntimeException("Unknown code returned from \"writeContentOnSocket\" method: " + writeContentOnSocketResult + ".");
            }

        } catch (IOException ioException) {
            disconnect();
            return AudioRecorderReturnCodes.DISCONNECTED;
        }
    }

    /**
     * Receives the confirmation from a package previous delivered.
     *
     * @param dataPackage The package previous delivered.
     * @return {@link AudioRecorderReturnCodes#SUCCESS} if confirmation was received successfully. {@link AudioRecorderReturnCodes#GENERIC_ERROR} if the confirmation was not received. {@link AudioRecorderReturnCodes#DISCONNECTED} if connection with audio recorder was lost.
     */
    private int receiveConfirmation(DataPackage dataPackage) {
        ReadSocketContentResult readSocketContentResult;
        int result = AudioRecorderReturnCodes.GENERIC_ERROR;
        boolean doneReceivingConfirmation = false;
        RetryAttempts retryAttempts = new RetryAttempts(RECEIVE_PACKAGE_MAXIMUM_RETRY_ATTEMPTS);
        Chronometer delayChronometer = new Chronometer();


        /* Starts counting the communication delay. */
        delayChronometer.start();
        while (!doneReceivingConfirmation) {

            /* Reads a content from socket. */
            try {
                readSocketContentResult = readFromSocket();
            } catch (IOException ioException) {
                disconnect();
                return AudioRecorderReturnCodes.DISCONNECTED;
            }

            switch (readSocketContentResult.getReturnCode()) {
                case AudioRecorderReturnCodes.SUCCESS:

                    /* If a content was read from socket. */
                    if (readSocketContentResult.getContentRead() != null) {

                        /* Creates a new communication package from bytes read on socket. */
                        DataPackage receivedPackage = new DataPackage(readSocketContentResult.getContentRead());

                        /* If package received was a confirmation. */
                        if (receivedPackage.getPackageType() == PackageType.CONFIRMATION) {

                            ConfirmationContent confirmationContent = (ConfirmationContent) receivedPackage.getContent();

                            /* If the package confirmed was the package previously sent. */
                            if (confirmationContent.getPackageId() == dataPackage.getId()) {
                                doneReceivingConfirmation = true;
                                result = AudioRecorderReturnCodes.SUCCESS;
                            } else {
                                Log.w(LOG_TAG, "receiveConfirmation (168): Received a confirmation, but package id mismatches: " + Integer.toHexString(dataPackage.getId()) + " != " + Integer.toHexString(confirmationContent.getPackageId()) + ".");
                            }
                        } else {
                            Log.w(LOG_TAG, "receiveConfirmation (171): Received a \"" + receivedPackage.getPackageType() + "\" package");
                        }
                    /* If not content was read from socket. */
                    } else {

                        /* Waits for another attempt to receive a confirmation. */
                        switch (retryAttempts.waitForNextAttempt()) {
                            case RetryAttemptsReturnCodes.SUCCESS:
                                break;
                            case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                                Log.d(LOG_TAG, "receiveConfirmation (178): Maximum retries reached.");
                                doneReceivingConfirmation = true;
                                result = AudioRecorderReturnCodes.GENERIC_ERROR;
                                break;
                            default:
                                throw new RuntimeException("Unknown return code received from \"wait\" function.");
                        }
                    }
                    break;
                case AudioRecorderReturnCodes.CONNECTION_LOST:
                    return AudioRecorderReturnCodes.DISCONNECTED;
            }


        }

        /* Stops counting the communication delay. */
        delayChronometer.stop();

        /* Adds to the communication delay time to the average calculator. */
        communicationDelayAverage.add(delayChronometer.getDifference());

        return result;
    }

    /**
     * Requests to close bluetooth socket connection.
     */
    private void disconnect() {
        try {
            readerWriter.closeConnection();
        } catch (IOException ioException) {
            throw new RuntimeException("Error while disconnecting from audio recorder.");
        }
    }

    /**
     * Returns the average communication delay time (in milliseconds).
     *
     * @return The average communication delay time (in milliseconds).
     */
    public long getCommunicationDelay() {
        return communicationDelayAverage.getAverage();
    }

    /**
     * Reads content from bluetooth socket trough {@link ReaderWriter#readSocketContent()} method, concatenating previous bytes read and removing bytes that aren't from the package received.
     *
     * @return The {@link ReadSocketContentResult} object returned from {@link ReaderWriter#readSocketContent()} method.
     * @throws IOException If an error occurred while reading bluetooth socket.
     */
    private ReadSocketContentResult readFromSocket() throws IOException {
        ReadSocketContentResult readSocketContentResult;
        byte[] bytes;

        /* Reads content from bluetooth socket. */
        readSocketContentResult = readerWriter.readSocketContent();

        switch (readSocketContentResult.getReturnCode()) {
            case AudioRecorderReturnCodes.SUCCESS:
                /* If a content was read. */
                if (readSocketContentResult.getContentRead() != null) {

                /* Concatenates with the content read the remaining bytes. */
                    bytes = concatenateRemainingBytes(readSocketContentResult.getContentRead());

                /* Removes from content read the bytes which does not belong to the package. */
                    bytes = removeRemainingBytes(bytes);

                /* Creates a new content result with the package bytes. */
                    readSocketContentResult = new ReadSocketContentResult(readSocketContentResult.getReturnCode(), bytes);
                }
                break;
            case AudioRecorderReturnCodes.CONNECTION_LOST:
                break;
            default:
                throw new RuntimeException("Unknown value returned from \"readSocketContent\" method: " + readSocketContentResult.getReturnCode() + ".");
        }

        return readSocketContentResult;
    }


    /**
     * Removes the remaining bytes read from bluetooth socket that does not belongs to the data package received.
     * The bytes removed will be stored and then concatenated on next data package received.
     *
     * @param bytes Bytes read from bluetooth socket.
     * @return The bytes read from bluetooth socket without the remaining bytes.
     */
    private byte[] removeRemainingBytes(byte[] bytes) {
        byte[] resultBytes = null;

        if (bytes != null) {

            /* Searches on bytes received the package trailer code. */
            int packageTrailerPosition = DataPackage.findPackageTrailerPosition(bytes);

            /* If a package trailer code was found. */
            if (packageTrailerPosition >= 0) {

                /* Splits the byte array after the package trailer position and stores the remaining bytes. */
                int remainingBytesLength = (bytes.length - (packageTrailerPosition + DataPackage.PACKAGE_TRAILER_BYTE_ARRAY_SIZE));
                int resultBytesLength = bytes.length - remainingBytesLength;
                if (remainingBytesLength > 0) {
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
        }

        return resultBytes;
    }

    /**
     * Concatenates the remaining bytes previously with the bytes informed on parameter.
     *
     * @param bytes Bytes which the remaining bytes will be concatenated.
     * @return The remaining bytes previously received concatenated with bytes received on parameter.
     */
    private byte[] concatenateRemainingBytes(byte[] bytes) {
        int resultBytesLength = 0;
        int bytesCopied = 0;
        byte[] resultBytes = null;

        if (remainingBytes != null) {
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
