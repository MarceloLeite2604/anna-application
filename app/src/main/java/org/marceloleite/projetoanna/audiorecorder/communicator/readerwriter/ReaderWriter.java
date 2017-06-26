package org.marceloleite.projetoanna.audiorecorder.communicator.readerwriter;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderReturnCodes;
import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Controls the input and output of data from a bluetooth socket.
 */
public class ReaderWriter {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ReaderWriter.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The bluetooth socket where the reading and writing is being controlled.
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * The bluetooth socket input stream.
     */
    private InputStream bluetoothSocketInputStream;

    /**
     * The bluetooth socket output stream.
     */
    private OutputStream bluetoothSocketOutputStream;

    public ReaderWriter(BluetoothSocket bluetoothSocket) {
        Log.d(LOG_TAG, "ReaderWriter (46): ");
        this.bluetoothSocket = bluetoothSocket;
        try {
            this.bluetoothSocketInputStream = bluetoothSocket.getInputStream();
            this.bluetoothSocketOutputStream = bluetoothSocket.getOutputStream();
        } catch (IOException ioException) {
            throw new RuntimeException("Could not create ReaderWriter object.", ioException);
        }
    }

    /**
     * Reads a content from bluetooth socket.
     *
     * @return A {@link ReadSocketContentResult} object with the code returned and the content read from it. If the bluetooth socket is still connected, the code returned will be {@link AudioRecorderReturnCodes#SUCCESS}. Otherwise, it will be {@link AudioRecorderReturnCodes#CONNECTION_LOST}.
     * @throws IOException If an exception occurred while reading bluetooth socket.
     */
    public ReadSocketContentResult readSocketContent() throws IOException {
        ReadSocketContentResult readSocketContentResult;
        byte[] bytes = null;
        byte[] previousBytes = null;
        boolean doneReading = false;
        if (isBluetoothConnected()) {
            while (!doneReading) {
                try {
                    /* Checks the total of bytes available on input stream. */
                    int totalBytesAvailable = bluetoothSocketInputStream.available();
                    if (totalBytesAvailable > 0) {

                        if (bytes == null) {
                            bytes = new byte[totalBytesAvailable];
                        } else {
                            previousBytes = ByteBuffer.allocate(bytes.length).put(bytes).array();
                            bytes = new byte[totalBytesAvailable];
                        }

                        /* Read content from input socket. */
                        int totalRead = bluetoothSocketInputStream.read(bytes);


                        if (totalRead >= 0) {
                            if (previousBytes != null) {
                            /* Concatenates on content the bytes read on previous reading. */
                                bytes = ByteBuffer.allocate(previousBytes.length + bytes.length).put(previousBytes).put(bytes).array();
                            }
                        } else {
                            throw new RuntimeException("Error while reading content from bluetooth socket input stream.");
                        }

                    } else {
                        doneReading = true;
                    }
                    /* If something went wrong while checking bluetooth input stream. */
                } catch (IOException ioException) {
                    String exceptionMessage = "Error while reading bluetooth socket input stream.";
                    throw new IOException(exceptionMessage, ioException);
                }
            }
            readSocketContentResult = new ReadSocketContentResult(AudioRecorderReturnCodes.SUCCESS, bytes);
        } else {
            readSocketContentResult = new ReadSocketContentResult(AudioRecorderReturnCodes.CONNECTION_LOST, null);
        }
        return readSocketContentResult;
    }

    /**
     * Writes a content on bluetooth socket.
     *
     * @param bytes The content to be written.
     * @return {@link AudioRecorderReturnCodes#SUCCESS} if content was written successfully. {@link AudioRecorderReturnCodes#CONNECTION_LOST} if bluetooth connection was lost.
     * @throws IOException If an exception occurred while writing content on bluetooth socket.
     */
    public int writeContentOnSocket(byte[] bytes) throws IOException {

        /* Checks if bluetooth socket is connected. */
        if (isBluetoothConnected()) {
            try {

                /* Writes content on bluetooth socket. */
                bluetoothSocketOutputStream.write(bytes);
                bluetoothSocketOutputStream.flush();
            } catch (IOException ioException) {
                String exceptionMessage = "Error while writing content on bluetooth socket.";
                throw new IOException(exceptionMessage, ioException);
            }
            return AudioRecorderReturnCodes.SUCCESS;
        } else {
            return AudioRecorderReturnCodes.CONNECTION_LOST;
        }
    }

    /**
     * Closes bluetooth socket connection.
     *
     * @throws IOException If an exception occurred while closing bluetooth socket connection.
     */
    public void closeConnection() throws IOException {
        if (isBluetoothConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException ioException) {
                String exceptionMessage = "Error while closing bluetooth socket.";
                throw new IOException(exceptionMessage, ioException);
            }
        }
    }

    /**
     * Checks is bluetooth socket is still connected.
     *
     * @return True if bluetooth socket is still connected. False otherwise.
     */
    private boolean isBluetoothConnected() {
        return bluetoothSocket.isConnected();
    }
}
