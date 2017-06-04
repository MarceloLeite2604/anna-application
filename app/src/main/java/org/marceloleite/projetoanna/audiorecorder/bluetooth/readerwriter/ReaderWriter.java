package org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter;

import android.bluetooth.BluetoothSocket;

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
        Log.addClassToLog(ReaderWriter.class);
    }

    /**
     * The bluetooth socket which the reading and writing is being controlled.
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * The bluetooth socket input stream.
     */
    private InputStream bluetoothSocketInputStream;

    /**
     * The bluetooth socket output stream.
     */
    private OutputStream bluetoothSocketOuptutStream;

    public ReaderWriter(BluetoothSocket bluetoothSocket) throws IOException {

        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothSocketInputStream = bluetoothSocket.getInputStream();
        this.bluetoothSocketOuptutStream = bluetoothSocket.getOutputStream();
    }

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

                        if (previousBytes != null) {
                            /* Concatenates on content the bytes read on previous reading. */
                            bytes = ByteBuffer.allocate(previousBytes.length + bytes.length).put(previousBytes).put(bytes).array();
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
            readSocketContentResult = new ReadSocketContentResult(ReaderWriterReturnValues.SUCCESS, bytes);
        } else {
            readSocketContentResult = new ReadSocketContentResult(ReaderWriterReturnValues.CONNECTION_LOST, null);
        }
        return readSocketContentResult;
    }

    public int writeContentOnSocket(byte[] bytes) throws ReaderWriterException {

        if (isBluetoothConnected()) {
            try {
                /*Log.d(LOG_TAG, "writeContentOnSocket, 70: Writing " + bytes.length + " byte(s) on socket.");*/
                bluetoothSocketOuptutStream.write(bytes);
                bluetoothSocketOuptutStream.flush();
            } catch (IOException ioException) {
                String exceptionMessage = "Error writing content on socket.";
                throw new ReaderWriterException(exceptionMessage, ioException);
            }
            return ReaderWriterReturnValues.SUCCESS;
        } else {
            return ReaderWriterReturnValues.CONNECTION_LOST;
        }
    }

    public void closeConnection() throws ReaderWriterException {
        if (isBluetoothConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException ioException) {
                String exceptionMessage = "Error closing socket connection.";
                throw new ReaderWriterException(exceptionMessage, ioException);
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
